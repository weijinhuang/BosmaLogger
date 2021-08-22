package com.bosma.logger

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class FloatButton : View {

    /**
     * 宽度
     */
    var mWidth = 50

    /**
     * 高度
     */
    var mHeight = 50

    private val mPaint = Paint()

    var mDragging = false


    constructor(size: Int, context: Context?) : super(context) {
        mWidth = size
        mHeight = size
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth, mHeight)
    }


    override fun onDraw(canvas: Canvas?) {
        mPaint.color = -0x11000001
        canvas?.drawText("Log", 0F, (mHeight / 2).toFloat(), mPaint)
        mPaint.color = -0x55cccccd
        canvas?.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (if (mWidth < mHeight) mWidth / 2 else mHeight / 2).toFloat(),
            mPaint
        )
    }
}