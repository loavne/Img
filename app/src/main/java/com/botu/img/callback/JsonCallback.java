package com.botu.img.callback;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author: swolf
 * @date : 2016-11-09 18:11
 */
public class JsonCallback<T> extends AbsCallback<T>{
    private Class<T> clazz;
    private Type type;

    /**
     * 传class,直接返回解析生成的对象
     */
    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 对于需要返回集合类型的,可以传type
     * type = new TypeToken<List<你的数据类型>>(){}.getType()
     */
    public JsonCallback(Type type) {
        this.type = type;
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //主要用于在所有请求之前添加公共的请求头或请求参数，例如登录授权的 token,使用的设备信息等,可以随意添加,也可以什么都不传

    }

    @Override
    public void onSuccess(T t, Call call, Response response) {

    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertSuccess(Response response) throws Exception {
        String result = response.body().string();
        Log.e("WxEntryActivity", "convertSuccess: " + result);
        if(TextUtils.isEmpty(result)) return null;

        /**
         * 一般来说，服务器返回的响应码基本包含 code, msg, data三部分，根据业务需要完成相应的逻辑分析
         * 以下是一个实例
         */
        JSONObject jsonObject = new JSONObject(result);
        int code = jsonObject.optInt("code", 0);
        String msg = jsonObject.optString("msg", "");
        String data = jsonObject.optString("data", "");
        switch (code) {
            case 0:
                if(clazz == String.class) return (T)data;
                if(clazz != null) return new Gson().fromJson(data, clazz);
                if(type!= null) return new Gson().fromJson(data, type);
                break;

        }

        return null;
    }
}
