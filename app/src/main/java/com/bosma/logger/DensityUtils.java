/*
 * Copyright 2016 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2016-12-03 22:55:55
 *
 */

package com.bosma.logger;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtils {

    /**
     * 根据手机的分辨率将dp的单位转成px(像素)
     *
     * @param context the context
     * @param dpValue a value of dp
     * @return the result of px
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5f);
    }

    /**
     * 根据手机的分辨率将px(像素)的单位转成dp
     *
     * @param context the context
     * @param pxValue a value of px
     * @return the result of dp
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * 将sp值转换为px值
     *
     * @param context the context
     * @param spValue a value of sp
     * @return the result of px
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * getFontDensity(context) + 0.5);
    }

    /**
     * 将px值转换为sp值
     *
     * @param context the context
     * @param pxValue a value of px
     * @return the result of sp
     */
    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / getFontDensity(context) + 0.5);
    }

    /**
     * get the density of device screen.
     *
     * @param context the context
     * @return the screen density
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * get the scale density of device screen.
     * usually this value is the same as density.
     * but it can adjust by user.
     *
     * @param context the context
     * @return the screen scale density.
     */
    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    // 屏幕宽度（像素）
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getWindowHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }
}
