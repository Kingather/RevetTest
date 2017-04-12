package com.example.liubin.expandablelistviewdemo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by liubin on 2017/3/27.
 */

public class Http {

    private static Http instance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    public Http() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setReadTimeout(3000, TimeUnit.MILLISECONDS);
        mOkHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);
        mOkHttpClient.setConnectTimeout(6000, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static Http getInstance() {
        if (instance == null) {
            synchronized (Http.class) {
                if (instance == null) {
                    instance = new Http();
                }
            }
        }
        return instance;
    }

    /**
     * 文件下载
     */
    public void downLoadFile(String url, HashMap<String, Object> param, final String filePath, final String fileName) {
        Request request = buildPostRequest(url, param);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                ResponseBody body = response.body();
                long l = body.contentLength();
                BufferedInputStream bis = new BufferedInputStream(body.byteStream());
                FileOutputStream fos = new FileOutputStream(new File(filePath, fileName), true);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] buffer = new byte[8 * 1024];
                int c = 0;
                int count = 0;
                while ((c = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, c);
                    count += c;
                    if (l > 0)
                        updateProgressUI(count, l);
                    bos.flush();
                }
            }
        });
    }

    private void updateProgressUI(int count, long l) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * 同步的post请求
     *
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    public String post(String url, HashMap<String, Object> param) throws IOException {
        Request request = buildPostRequest(url, param);
        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 异步请求分发
     *
     * @param url            目标url
     * @param param          post请求参数
     * @param resultCallback 服务器返回结果处理回调
     */
    public void postAsync(String url, HashMap<String, Object> param, ResultCallback
            resultCallback) {
        Log.d("liubin", "url:" + url + "\n param:" + param);
        _postAsync(url, param, resultCallback);
    }

    /**
     * 文件异步上传
     *
     * @param url
     * @param param
     * @param files
     * @param resultCallback
     */
    public void postFilesAsync(String url, HashMap<String, Object> param, File[] files, String[]
            fileKeys, ResultCallback resultCallback) {
        _postFilesAsync(url, param, files, fileKeys, resultCallback);
    }

    private void _postFilesAsync(String url, HashMap<String, Object> param, File[] files,
                                 String[] fileKeys, ResultCallback resultCallback) {
        Request request = buildMultipartFormRequest(url, param, files, fileKeys);
        deliveryResult(request, resultCallback);
    }

    /**
     * 单文件异步上传，以post表单形式实现
     *
     * @param url
     * @param param
     * @param file
     */
    public void postFileAsync(String url, HashMap<String, Object> param, File file, String fileKey,
                              ResultCallback resultCallback) {
        _postFileAsync(url, param, file, fileKey, resultCallback);
    }

    private void _postFileAsync(String url, HashMap<String, Object> param, File file, String
            fileKey, ResultCallback resultCallback) {
        Request request = buildMultipartFormRequest(url, param, new File[]{file}, new
                String[]{fileKey});
        deliveryResult(request, resultCallback);
    }

    private void _postAsync(String url, HashMap<String, Object> param, ResultCallback
            resultCallback) {
        Request request = buildPostRequest(url, param);
        deliveryResult(request, resultCallback);
    }

    private Request buildMultipartFormRequest(String url, HashMap<String, Object> param, File[]
            files, String[] fileKeys) {
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Iterator<String> it = param.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = String.valueOf(param.get(key));
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                    RequestBody.create(null, value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.exists()) {
                    String fileName = file.getName();
                    Log.d("liubin", fileName);
                    Log.d("liubin", "fileKey:" + fileKeys[i] + "\t" + "file:" +
                            file.toString());
                    fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" +
                            fileKeys[i] + "\";" + " " + "filename=\"" + fileName + "\""), fileBody);
                }
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private void deliveryResult(Request request, final ResultCallback resultCallback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedCallback(request, e, resultCallback);
            }

            @Override
            public void onResponse(Response response) {
                try {
                    String strResult = response.body().string();

                    sendSucceedCallback(strResult, resultCallback);
                } catch (IOException e) {
                    sendFailedCallback(response.request(), e, resultCallback);
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendSucceedCallback(final String strResult, final ResultCallback resultCallback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (resultCallback != null) {
                    resultCallback.onResponse(strResult);
                }
            }
        });
    }

    private void sendFailedCallback(final Request request, final IOException e, final
    ResultCallback resultCallback) {
        mDelivery.post(new Runnable() { //UI Thread
            @Override
            public void run() {
                if (resultCallback != null) {
                    resultCallback.onError(request, e);
                }
            }
        });
    }

    /**
     * 构建请求体
     *
     * @param url
     * @param param
     * @return
     */
    private Request buildPostRequest(String url, HashMap<String, Object> param) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (param != null && !param.isEmpty()) {
            for (Iterator<String> it = param.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                String value = String.valueOf(param.get(key));
                builder.addEncoded(key, value);
            }
        }
        RequestBody body = builder.build();
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static abstract class ResultCallback {

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(String response);
    }
}
