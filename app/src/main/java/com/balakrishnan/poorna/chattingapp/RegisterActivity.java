package com.balakrishnan.poorna.chattingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword,UserName,UserNumber;
    private TextView AlreadyHaveAccountLink;
    
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();


        InitializeFields();


        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SendUserToLoginActivity();
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }




    private void CreateNewAccount()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we wre creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) 
                        {
                            if (task.isSuccessful())
                            {
                               //  deviceToken = FirebaseInstanceId.getInstance().getToken();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if(!task.isSuccessful()){
                                            Log.w("TAG", "getInstanceId failed", task.getException());
                                            return;
                                        }
                                       String deviceToken=task.getResult().getToken();
                                        String currentUserID = mAuth.getCurrentUser().getUid();
                                        RootRef.child("Users").child(currentUserID).setValue("");


                                        RootRef.child("Users").child(currentUserID).child("device_token")
                                                .setValue(deviceToken);
                                        String setUserName =UserName.getText().toString();// mAuth.getCurrentUser().getDisplayName();
                                        String setStatus = UserNumber.getText().toString();
                                        HashMap<String, Object> profileMap = new HashMap<>();
                                        profileMap.put("uid", currentUserID);
                                        profileMap.put("name", setUserName);
                                        profileMap.put("status", setStatus);
                                        RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            SendUserToMainActivity();
                                                            Toast.makeText(RegisterActivity.this, "Registered Successfully...", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            String message = task.getException().toString();
                                                            Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                      //  Intent loginIntent = new Intent(RegisterActivity.this, PhoneLoginActivity.class);
                                       // startActivity(loginIntent);
                                        SendUserToMainActivity();
                                        Toast.makeText(RegisterActivity.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                });
                            }
                            else 
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }




    private void InitializeFields()
    {
        CreateAccountButton = (Button) findViewById(R.id.register_button);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserName = (EditText) findViewById(R.id.user_name);
        UserNumber = (EditText) findViewById(R.id.register_phoneno);
        UserPassword = (EditText) findViewById(R.id.register_password);
        AlreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);

        loadingBar = new ProgressDialog(this);
    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
