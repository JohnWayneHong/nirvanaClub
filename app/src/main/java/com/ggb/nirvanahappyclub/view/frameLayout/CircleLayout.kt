package com.ggb.nirvanahappyclub.view.frameLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout

class CircleLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        // 默认不调用 onDraw，设置成 false 就可以调用
        setWillNotDraw(false)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val path = Path().apply {
            if (width > height) {
                addCircle(width.toFloat() / 2, height.toFloat() / 2, height.toFloat() / 2, Path.Direction.CW)
            } else {
                addCircle(width.toFloat() / 2, height.toFloat() / 2, width.toFloat() / 2, Path.Direction.CW)
            }
        }
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }

}