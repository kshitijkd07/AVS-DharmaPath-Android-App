package com.paid.myapplication.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class JaapRingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var count: Int = 0
        set(value) { field = value; invalidate() }
    var goal: Int = 108
        set(value) { field = value; invalidate() }

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#12FFFFFF")
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#E8B84A")
    }
    private val countPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4F5F7")
        textSize = 80f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B7289")
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }
    private val malaLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8B84A")
        textSize = 26f
        textAlign = Paint.Align.CENTER
    }

    private val oval = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(cx, cy) - 20f

        oval.set(cx - radius, cy - radius, cx + radius, cy + radius)

        // Track
        canvas.drawArc(oval, -90f, 360f, false, trackPaint)

        // Progress
        val progress = if (goal > 0) (count % 108).toFloat() / 108f else 0f
        canvas.drawArc(oval, -90f, 360f * progress, false, progressPaint)

        // Count text
        canvas.drawText("$count", cx, cy + 28f, countPaint)

        // "of X" label
        canvas.drawText("of $goal", cx, cy + 70f, labelPaint)

        // Mala label
        val malaIndex = (count / 108) + 1
        val malaTotal = maxOf(1, (goal + 107) / 108)
        canvas.drawText("Mala $malaIndex of $malaTotal", cx, cy + 108f, malaLabelPaint)
    }
}
