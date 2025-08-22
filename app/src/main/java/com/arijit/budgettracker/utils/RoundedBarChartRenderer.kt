package com.arijit.budgettracker.utils

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class RoundedBarChartRenderer(
    chart: BarChart,
    animator: com.github.mikephil.charting.animation.ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val barRadius = 20f // control roundness here

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = dataSet.barBorderWidth

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val barData = mChart.barData
        val barWidthHalf = barData.barWidth / 2.0f

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(barData.barWidth)
        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        for (j in buffer.buffer.indices step 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) continue
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

            mRenderPaint.color = dataSet.getColor(j / 4)

            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            val rect = RectF(left, top, right, bottom)
            c.drawRoundRect(rect, barRadius, barRadius, mRenderPaint)
        }
    }
}
