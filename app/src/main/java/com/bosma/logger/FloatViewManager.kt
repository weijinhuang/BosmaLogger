package com.bosma.logger

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.animation.LinearInterpolator


object FloatViewManager {

    private var floatMenuParams: WindowManager.LayoutParams? = null
    private var hasMenuLoad = false
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mButtonSize = 0

    private var mFloatButton: FloatButton? = null
    private var mFloatLogView: FloatLogView? = null
    private var mBtnAdded = false

    private var mStartX = 0
    private var mStartY = 0
    var mFloatBallParams: WindowManager.LayoutParams? = null
    private var ballWindowMargin = 0

    private var mWm: WindowManager? = null


    fun init(context: Context) {
        ballWindowMargin = DensityUtils.dip2px(context, 14f)
        mWm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= 30) {
            val bounds = mWm?.currentWindowMetrics?.bounds
            mScreenWidth = bounds?.right ?: 0
            mScreenHeight = bounds?.bottom ?: 0

        } else {
            val point = Point()
            mWm?.defaultDisplay?.getSize(point)
            mScreenHeight = point.y
            mScreenWidth = point.x
        }
        val density = context.resources.displayMetrics.density
        mButtonSize = (density * 45).toInt()
        mFloatButton = FloatButton(mButtonSize, context)

        mFloatBallParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        mFloatBallParams?.gravity = Gravity.TOP or Gravity.LEFT
        mFloatBallParams?.x = mScreenWidth - mButtonSize + ballWindowMargin
        mFloatBallParams?.y = (mScreenHeight - mButtonSize) / 2
        mFloatButton?.setOnTouchListener(floatingViewTouchListener)
        if (!mBtnAdded) {
            mWm?.addView(mFloatButton, mFloatBallParams)
            mBtnAdded = true
        }

        floatMenuParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            mScreenHeight / 2,
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT
        )
//        floatMenuParams?.width = mScreenWidth
//        floatMenuParams?.height = mScreenHeight / 2

        floatMenuParams?.gravity = Gravity.TOP or Gravity.LEFT
        if (mFloatLogView == null) {
            mFloatLogView = FloatLogView(context)
        }
    }

    var floatingViewTouchListener: OnTouchListener = object : OnTouchListener {
        private var downTime = 0f
        private var upTime = 0f
        private var xMargin = 0
        private var yMargin = 0
        private var xInitParam = 0
        private var yInitParam = 0
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val mRawX = event.rawX.toInt()
            val mRawY = event.rawY.toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downTime = System.currentTimeMillis().toFloat()
                    xMargin = mFloatBallParams?.x ?: 0
                    yMargin = mFloatBallParams?.y ?: 0
                    xInitParam = mFloatBallParams?.x ?: 0
                    yInitParam = mFloatBallParams?.y ?: 0
                    mStartX = mRawX
                    mStartY = mRawY

                }
                MotionEvent.ACTION_MOVE -> {
                    mFloatButton?.mDragging = true
                    xMargin = mRawX + (xInitParam - mStartX)
                    yMargin = mRawY + (yInitParam - mStartY)
                    mFloatBallParams?.x = xMargin
                    mFloatBallParams?.y = yMargin
                    mWm?.updateViewLayout(mFloatButton, mFloatBallParams)

                }
                MotionEvent.ACTION_UP -> {
                    mFloatButton?.mDragging = false
                    upTime = System.currentTimeMillis().toFloat()
                    if (Math.abs(mRawX - mStartX) < 20 && Math.abs(mRawY - mStartY) < 20 && upTime - downTime < 100) {
                        showFloatMenu()
                    } else {
//                        if (mFloatLogView != null && hasMenuLoad) {
//                            mWm?.removeView(mFloatLogView);
//                            mFloatLogView = null;
//                            hasMenuLoad = false;
//                        }
                    }
                    resetPosition(xMargin, yMargin)
                }
                else -> {
//                    val rect = Rect()
//                    if (floatMenuParams != null) {
//                        rect.left = floatMenuParams.x
//                        rect.right = floatMenuParams.x + menuWidth
//                        rect.top = floatMenuParams.y
//                        rect.bottom = floatMenuParams.y + menuHeight
//                        if (!rect.contains(mRawX, mRawY)) {
//                            if (floatMenu != null) {
//                                mWm?.removeView(floatMenu)
//                                floatMenu = null
//                                hasMenuLoad = false
//                            }
//                        }
//                    }
                }
            }
            return true
        }
    }

    private fun showFloatMenu() {
        if (!hasMenuLoad && mFloatLogView != null) {
            mWm?.addView(mFloatLogView, floatMenuParams)
            hasMenuLoad = true
        }else{
            mWm?.removeView(mFloatLogView)
            hasMenuLoad = false
        }
    }

    private fun removeFloatMenu() {
        if (hasMenuLoad && mFloatLogView != null) {
            mWm?.removeView(mFloatLogView)
            hasMenuLoad = false
        }
    }

    private fun removeFloatButton() {
        if (mBtnAdded && mFloatButton != null) {
            mWm?.removeView(mFloatButton)
            mBtnAdded = false
        }
    }

    private var isLeft = false
    private fun resetPosition(xMargin: Int, yMargin: Int) {
        if (xMargin <= mScreenWidth / 2) {
            isLeft = true
            moveToLeft(xMargin, yMargin)
        } else {
            isLeft = false
            moveToRight(xMargin, yMargin)
        }
    }


    private fun moveToLeft(x_cord_now: Int, y_cord_now: Int) {
        val animator = ValueAnimator()
        animator.setIntValues(x_cord_now, -ballWindowMargin)
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = 0
        animator.duration = 300
        animator.addUpdateListener { animation ->
            mFloatBallParams?.x = animator.animatedValue as Int

//                if (y_cord_now < ballWindowMargin) {
//                    mFloatBallParams?.y = (int) (animation.getCurrentPlayTime() * ballWindowMargin / 300);
//                } else if (y_cord_now > mHeight - 2 * ballSize - ballWindowMargin) {
//                    mFloatBallParams?.y = (int) (mHeight - 2 * ballSize - animation.getCurrentPlayTime() * ballWindowMargin / 300);
//                }
            //y方向边界
            if (y_cord_now < ballWindowMargin + 200) {
                mFloatBallParams?.y =
                    (animation.currentPlayTime * (ballWindowMargin + 200) / 300).toInt()
            } else if (y_cord_now > mScreenHeight - 2 * mButtonSize - (ballWindowMargin + 200)) {
                mFloatBallParams?.y =
                    (mScreenHeight - 2 * mButtonSize - animation.currentPlayTime * (ballWindowMargin + 200) / 300).toInt()
            }
            mWm?.updateViewLayout(mFloatButton, mFloatBallParams)
        }
        animator.start()
    }

    private fun moveToRight(x_cord_now: Int, y_cord_now: Int) {
        val animator = ValueAnimator()
        animator.setIntValues(x_cord_now, mScreenWidth - mButtonSize + ballWindowMargin)
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = 0
        animator.duration = 300
        animator.addUpdateListener { animation ->
            mFloatBallParams?.x = animator.animatedValue as Int
            //y方向边界
            if (y_cord_now < ballWindowMargin + 200) {
                mFloatBallParams?.y =
                    (animation.currentPlayTime * (ballWindowMargin + 200) / 300).toInt()
            } else if (y_cord_now > mScreenHeight - 2 * mButtonSize - (ballWindowMargin + 200)) {
                mFloatBallParams?.y =
                    (mScreenHeight - 2 * mButtonSize - animation.currentPlayTime * (ballWindowMargin + 200) / 300).toInt()
            }
            mWm?.updateViewLayout(mFloatButton, mFloatBallParams)
        }
        animator.start()
    }

}