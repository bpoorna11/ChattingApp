package com.balakrishnan.poorna.chattingapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private Button LoginButton;
    private EditText UserEmail, UserPassword,UserName;
    private TextView NeedNewAccountLink, ForgetPasswordLink;
    private String currentUserID;
    private DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();


        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToRegisterActivity();
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowUserToLogin();
            }
        });


        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
            }
        });
    }




    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
           SendUserToMainActivity();
        }
    }



    private void AllowUserToLogin()
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
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            /////////////////////
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                SharedPreferences sp=getSharedPreferences("sdata",MainActivity.MODE_PRIVATE);
                                SharedPreferences.Editor edd=sp.edit();
                                edd.putString("login", "yes");
                                edd.commit();
                                SendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this, "Logged in Successful...", Toast.LENGTH_SHORT).show();
                                                    try {
                                                        if ((loadingBar != null) && loadingBar.isShowing()) {
                                                            loadingBar.dismiss();
                                                        }
                                                    } catch (final IllegalArgumentException e) {
                                                        // Handle or log or ignore
                                                    } catch (final Exception e) {
                                                        // Handle or log or ignore
                                                    } finally {
                                                        loadingBar = null;
                                                    }
                                                    //loadingBar.dismiss();

                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }



    private void InitializeFields()
    {
        LoginButton = (Button) findViewById(R.id.login_button);
        UserName = (EditText) findViewById(R.id.user_name);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Runtime permission
        try {

            boolean permissionGranted_WriteExternal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_ReadExternal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean permissionGranted_ReadCon = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

            if (permissionGranted_ReadCon) {
                if (permissionGranted_WriteExternal) {
                    if (permissionGranted_ReadExternal) {
                        try {
                            // toggleButton.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 300);
            }

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200 || requestCode == 300 || requestCode == 100 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    //toggleButton.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
