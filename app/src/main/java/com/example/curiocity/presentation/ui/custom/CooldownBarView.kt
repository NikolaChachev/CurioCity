package com.example.curiocity.presentation.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.curiocity.R

class CooldownBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 1f // 1.0 = full, 0.0 = empty
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.colorPrimary, context.theme)// Customize color here
        style = Paint.Style.FILL
    }

    private val cornerRadius = 20f // adjust this for roundness

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barWidth = width * progress
        rect.set(0f, 0f, barWidth, height.toFloat())

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint)
    }
}