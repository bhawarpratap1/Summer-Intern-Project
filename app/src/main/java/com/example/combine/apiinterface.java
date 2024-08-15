package com.example.combine;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface apiinterface {

//    @FormUrlEncoded
//    @POST("send")
//    Call<model> sendModel(@Body ModelData data);



//    @Headers("Content-Type: application/json")
//    @POST("send")
//    Call<model> sendmodel(@Body @Field("jerk_value")float jerk_value,
//                          @Field("noise_value") float noise_value,
//                          @Field("speed_value") float speed_value,
//                          @Field("street_address") String street_address);
    @Headers("Content-Type: application/json")
    @POST("send")
    Call<model>  sendmodel(@Body requestModel data);

    @Headers("Content-Type: application/json")
    @POST("userfeedback")
    Call<userResponseModel>  sendUserRequest(@Body userRequestModel data);
}
