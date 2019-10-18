package com.balakrishnan.poorna.chattingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    String state="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        VerifyUserExistance();
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            state="online";
            updateUserStatus("online");
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            state="offline";
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (currentUser != null)
        {
            state="offline";
            updateUserStatus("offline");
        }
    }
    private void VerifyUserExistance()
    {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
                  /*  String saveCurrentTime, saveCurrentDate;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    HashMap<String, Object> onlineStateMap = new HashMap<>();
                    onlineStateMap.put("time", saveCurrentTime);
                    onlineStateMap.put("date", saveCurrentDate);
                    onlineStateMap.put("state", state);

                    RootRef.child("Users").child(currentUserID).child("userState")
                            .updateChildren(onlineStateMap);*/
                   // Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option)
        {
            updateUserStatus("offline");
            state="offline";
            mAuth.signOut();
            SendUserToLoginActivity();
        }


        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }
    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }


}
