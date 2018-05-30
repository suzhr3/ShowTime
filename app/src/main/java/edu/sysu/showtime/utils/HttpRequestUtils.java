package edu.sysu.showtime.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import edu.sysu.showtime.bean.ResponseObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HttpRequestUtils {
    public static final int WHAT_FAIL = 0;
    public static final int WHAT_SUCC = 1;
    public static Gson gson = new Gson();
    private static OkHttpClient okHttpClient = new OkHttpClient();

    public interface OnResultListener<T> {
        void onFail(int code, String msg);
        void onSuccess(T object);
    }
    private OnResultListener listener;

    public void setOnResultListener(OnResultListener l) {
        listener = l;
    }

    //在子线程的Handler中传入主线程的Looper，就能获取到主线程的Handler
    private Handler uiHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            int what = msg.what;
            if (what == WHAT_FAIL){
                if(listener != null){
                    listener.onFail(msg.arg1, (String)msg.obj);
                }
            } else if(what == WHAT_SUCC){
                if (listener != null){
                    listener.onSuccess(msg.obj);    //回调，发消息给主线程
                }
            }
        }
    };

    public void request(String url){
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailMsg(-100, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response resp) throws IOException {
                if(resp.isSuccessful()){
                    //响应成功，解析服务器返回的数据，注意这里要用string()方法而不是toString()方法
                    String responseBody = resp.body().string();
                    Log.i("test", "responseBody = " + responseBody);
                    ResponseObj responseObj = gson.fromJson(responseBody, ResponseObj.class);

                    //数据格式有错误，gson的str->responseObj转换失败
                    if(responseObj == null){
                        sendFailMsg(-101, "服务器返回数据有误");
                    }else{                      //返回数据成功
                        if("SUCCESS".equals(responseObj.getCode())){
                            Object data = onSuccess(responseBody);
                            sendSuccessMsg(data);
                        }else{
                            sendFailMsg(Integer.valueOf(responseObj.getErrCode()), responseObj.errMsg);
                        }
                    }
                } else {
                    sendFailMsg(resp.code(), "服务器异常");
                }
            }
        });
    }

    private void sendFailMsg(int errCode, String errMsg) {
        Message msg = uiHandler.obtainMessage();
        msg.what = WHAT_FAIL;
        msg.arg1 = errCode;
        msg.obj = errMsg;
        uiHandler.sendMessage(msg);
    }
    private void sendSuccessMsg(Object data){
        Message msg = uiHandler.obtainMessage();
        msg.what = WHAT_SUCC;
        msg.obj = data;
        uiHandler.sendMessage(msg);
    }

    public abstract Object onSuccess(String body);
}
