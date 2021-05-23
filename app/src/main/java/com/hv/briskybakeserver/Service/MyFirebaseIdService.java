package com.hv.briskybakeserver.Service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Model.Token;

import java.util.Objects;

public class MyFirebaseIdService extends FirebaseMessagingService {

    //@Override
    public void onNewToken(@NonNull Task<String> s) {
        super.onNewToken(String.valueOf(s));
        Log.d("NEW_TOKEN",String.valueOf(s));
        Task<String> token=s;
        updateToServer(token);
    }

    private void updateToServer(Task<String> token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

}
