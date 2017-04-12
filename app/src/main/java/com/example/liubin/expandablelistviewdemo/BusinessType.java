package com.example.liubin.expandablelistviewdemo;

import android.util.Log;

/**
 * Created by liubin on 2017/4/10.
 */

public class BusinessType {

    public static String businessType = "000000010000";

    public static void main(String[] args) {
        StringBuffer buffer = new StringBuffer();
        byte[] bytes = businessType.getBytes();
        System.out.print("byte[]:" + buffer.toString());
    }
}
