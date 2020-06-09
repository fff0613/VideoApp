package com.example.videoapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class DisplayUtil {
    /**
     * 获取屏幕的宽高
     * @param activity
     * @return
     */
    public static int[] getDisplayXY(Activity activity){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int[] displayXY=new int[2];
        displayXY[0]= point.x;
        displayXY[1]= point.y;

        return displayXY;
    }

    /**
     * 获取屏幕的高
     * @param activity
     * @return
     */
    public static int getDisplayX(Activity activity){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);

        return point.x;
    }


    /**
     * 跟进屏幕的宽高和视频的宽高计算视频的显示宽高
     * @param displayXY
     * @param videoX
     * @param videoY
     * @return
     */
    public static int[] resetVideoXY(int[] displayXY,int videoX,int videoY){
        double rateX=videoX*1.0/displayXY[0];
        double rateY=videoY*1.0/displayXY[1];
        double rate=rateX>rateY?rateX:rateY;

        int[] resetXY=new int[2];
        resetXY[0]= (int) (videoX/rate);
        resetXY[1]= (int) (videoY/rate);

        return resetXY;
    }

    /**
     * dp转换为px
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, int dp){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dp*scale+0.5f);
    }
}