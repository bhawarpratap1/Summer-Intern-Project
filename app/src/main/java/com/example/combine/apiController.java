package com.example.combine;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public  class apiController {
    private static final String url="http://192.168.247.203:3002/";

    private static apiController clientObject;

    private static Retrofit retrofit;

    public apiController(){
        retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized apiController getInstance(){
        if(clientObject==null){
            clientObject=new apiController();
        }
        return clientObject;
    }

    public apiinterface getApiInterface(){
        return retrofit.create(apiinterface.class);
    }
}
