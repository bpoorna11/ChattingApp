package com.balakrishnan.poorna.chattingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    private Button resetButton;
    private EditText forgetEmail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        resetButton=findViewById(R.id.resetpass_button);
        forgetEmail=findViewById(R.id.forget_email);
        mAuth=FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=forgetEmail.getText().toString().trim();
                if(useremail.equals("")) {
                    Toast.makeText(getApplicationContext(),"Please enter your registered email ID",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Password reset email sent",Toast.LENGTH_SHORT).show();
                                    finish();
                                  //  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }else{
                                    Toast.makeText(getApplicationContext(),"Error in sending password",Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
            }
        });
    }
}
