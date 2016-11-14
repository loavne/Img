package com.botu.img.callback;

import com.lzy.okgo.callback.AbsCallback;

import java.io.InputStream;

import okhttp3.Response;

/**
 * @author: swolf
 * @date : 2016-11-09 16:07
 */
public abstract class InputStreamCallback extends AbsCallback<InputStream>{
    @Override
    public InputStream convertSuccess(Response response) throws Exception {
        return response.body().byteStream();
    }
}
