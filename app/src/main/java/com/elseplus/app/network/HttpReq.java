package com.elseplus.app.network;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HttpReq {

    private final static Logger log = LoggerFactory.getLogger(HttpReq.class);

    private static String PrefixURL = "https://devsdkapi.fanfq.com/sdk/api/req/analysis"; //  //

    private final OkHttpClient mOkHttpClient;
    private volatile static HttpReq INSTANCE;

    public HttpReq() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpReq getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpReq.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpReq();
                }
            }
        }
        return INSTANCE;
    }

    public void get(String uri,HttpCallBack httpCallBack){
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json; charset=utf-8")
                .url(uri)
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("IOException>>"+e.toString());
                httpCallBack.fail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful()){
                    String ret = response.body().string();
                    System.out.println("response>>"+ret);
                    httpCallBack.success(ret);
                }else{
                    httpCallBack.fail("error");
                }

            }
        });
    }

    public void post(Map<String,String> map, String uri, HttpCallBack callBack){

        FormBody.Builder builder = new FormBody.Builder();
        for (String in : map.keySet()) {
            builder.add(in, map.get(in));
        }
        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json; charset=utf-8")
                .url(PrefixURL + uri)
                .post(body).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean successful = response.isSuccessful();
                if(successful){
//                    callBack.success(response.body().string());
                    String success = response.body().string();
                    log.debug("http ret:{}",success);

                    JSONObject jsonObject=JSONObject.parseObject(success);
                    //log.debug("http ret:{}",jsonObject.toJSONString());

                    callBack.success("s");
                    //callBack.success(jsonObject.toJSONString());

//                    Boolean aBoolean = jsonObject.getBoolean("success");
//                    if(aBoolean){
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        callBack.success(data.toJSONString());
//                    }else{
//                        String message = jsonObject.getString("message");
//                        if(StringUtils.isEmpty(message)){
//                            callBack.fail("请求失败");
//                        }else{
//                            callBack.fail(message);
//                        }
//                    }
                }else{
                    callBack.fail("请求失败");
                }
            }
        });
    }


}
