package com.example.liubin.expandablelistviewdemo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.squareup.okhttp.Request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ExpandAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        requestData();

    }

    private void requestData() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("min", 0);
        param.put("max", 6);
        param.put("token", "4f965e8df8d94b88bd65c18a40cf428d");
        param.put("flag", 1);
        param.put("propId", "cy");
        String url = "http://adios.huerkang.cn/doctorRecom/queryMealGoodsList.json";
        Log.d("liubin", "____param:" + param.toString());
        Http.getInstance().postAsync(url, param, new Http.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) {
                Log.d("liubin", "_____queryMealGoodsList:" + response);
            }
        });
    }

    private void initView() {
        expandableListView = (ExpandableListView) findViewById(R.id.expand_list);
        adapter = new ExpandAdapter(this);

        findViewById(R.id.btn_open_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.PowerHideModeActivity"));
                startActivityForResult(intent, 1);
            }
        });

        try {
            Log.d("flag","===============start===============");
            Class<?> settingClass = Class.forName("com.example.liubin.expandablelistviewdemo.WorkActivity");
//            Field[] declaredFields = settingClass.getDeclaredFields();
//            for (int i = 0; i < declaredFields.length; i++) {
//                Log.d("flag", "____declareFieldName:" + declaredFields[i]);
//            }
//            Field[] fields = settingClass.getFields();
//            for (int i = 0; i < fields.length; i++) {
//                Log.d("flag", "____fieldName:" + fields[i]);
//
//            }
//            Method[] methods = settingClass.getMethods();
//            for (Method method : methods) {
//                Log.d("flag", "____method:" + method);
//            }
            Method init = settingClass.getMethod("init", settingClass);
            Log.d("flag","===============end===============");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
