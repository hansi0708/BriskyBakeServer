package com.hv.briskybakeserver.Remote;

import com.hv.briskybakeserver.Model.MyResponse;
import com.hv.briskybakeserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAQOPvJTk:APA91bFtzROS2U7C-YXrIjJhlcI0uWQqaOobyvMlv6ga3bBFkVAPkxAgZl0jXPX1r_fVhkt4vjs4QNJVCxeOfWMnySCD0au7qXcS9EOAGlZf0r1W-kPyKvqg5h942P5y25uqSJzm9Gta"
            }
    )
    @POST("v1/projects/brisky-bake-4674e/")
    Call<MyResponse> sendNotification(@Body Sender body);
}
