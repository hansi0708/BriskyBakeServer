package com.hv.briskybakeserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hv.briskybakeserver.Common.Common;
import com.hv.briskybakeserver.Model.User;

import java.time.LocalDate;

public class SignIn extends AppCompatActivity {

    EditText tphone,tpassword;
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tphone=findViewById(R.id.TextPhonesignin);
        tpassword=findViewById(R.id.TextPasswordsignin);
        btnSignIn=findViewById(R.id.btnsignin);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Staff");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(tphone.getText().toString(),tpassword.getText().toString());

            }
        });
    }

    private void signInUser(String phone, String password) {

        ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(localPhone).exists())
                {
                    Log.d("TAG", "onDataChange: "+snapshot.child(localPhone).exists());
                    mDialog.dismiss();
                    User user = snapshot.child(localPhone).getValue(User.class);
                    user.setEmail(localPhone);

                        if (user.getPassword().equals(localPassword))
                        {
                            Intent Login = new Intent(SignIn.this,Home.class);
                            Common.currentUser = user;
                            startActivity(Login);
//                            finish();
                        }
                        else
                            Log.d("TAG", "Wrong Password");
                }
                else
                {
                    mDialog.dismiss();
                    Log.d("TAG", "User not exist in DataBase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}