package com.mrgao.luckrecyclerview.utils;

import android.content.Context;

/**
 * Created by mr.gao on 2018/1/24.
 * Package:    com.mrgao.popupwindowviews.utils
 * Create Date:2018/1/24
 * Project Name:PopupWindowViews
 * Description:
 */

public class ScreenUtils {


    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


}
