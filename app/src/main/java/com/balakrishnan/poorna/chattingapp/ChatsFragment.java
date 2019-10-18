package com.balakrishnan.poorna.chattingapp;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment
{
    private View PrivateChatsView;
    private RecyclerView chatsList;
    static HashSet<String> allusers=new HashSet<>();
    static HashSet<String> firebaseContacts=new HashSet<>();
    private DatabaseReference ChatsRef, UsersRef,ContactsRef,RootRef,ContactofUserRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";
    Cursor phones;

    public ChatsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        RootRef = FirebaseDatabase.getInstance().getReference();
        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        RootRef.child("Contacts").child(currentUserID).setValue("");
        ValueEventListener eventListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String usernumber = ds.child("status").getValue().toString();
                    allusers.add(usernumber);
                    Log.d("TAG", usernumber);
                }
                phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
                while (phones.moveToNext()) {
                    String phnno = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String phnnowithoutsp=phnno.replaceAll("\\s", "");
                    if(phnno.contains("+91"))
                        phnnowithoutsp=phnnowithoutsp.substring(3);
                    if (allusers.contains(phnnowithoutsp)) firebaseContacts.add(phnnowithoutsp);
                }
                Log.d("TAG", allusers.toString());
                //Firebase contacts contains contacts using the app and in the users contact list
                Log.d("TAG", firebaseContacts.toString());
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String usernumber = ds.child("status").getValue().toString();
                    String userId = ds.getKey();
                    System.out.println("user ids "+userId);
                    if(!currentUserID.equals(userId) && firebaseContacts.contains(usernumber)) {
                        System.out.println(currentUserID+" current n user ids "+userId);
                        RootRef.child("Contacts").child(currentUserID).child(userId).child("Contacts").setValue("Saved");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        UsersRef.addListenerForSingleValueEvent(eventListener);
        return PrivateChatsView;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart()
    {
        super.onStart();
                FirebaseRecyclerOptions<Contacts> options =
                        new FirebaseRecyclerOptions.Builder<Contacts>()
                                .setQuery(ChatsRef, Contacts.class)
                                .build();

                FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                        new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                            {
                                final String usersIDs = getRef(position).getKey();
                                System.out.println(currentUserID+" "+usersIDs);
                                    UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                final String retName = dataSnapshot.child("name").getValue().toString();
                                                final String retStatus = dataSnapshot.child("status").getValue().toString();
                                                holder.userName.setText(retName);


                                                if (dataSnapshot.child("userState").hasChild("state")) {
                                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                                    String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                                    if (state.equals("online")) {
                                                        holder.userStatus.setText("online");
                                                    } else if (state.equals("offline")) {
                                                        holder.userStatus.setText("Last Seen: " + date + " " + time);
                                                    }
                                                } else {
                                                    holder.userStatus.setText("offline");
                                                }

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                        chatIntent.putExtra("visit_user_id", usersIDs);
                                                        chatIntent.putExtra("visit_user_name", retName);
                                                        startActivity(chatIntent);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            }

                            @NonNull
                            @Override
                            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                            {
                                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                                return new ChatsViewHolder(view);
                            }
                        };

                chatsList.setAdapter(adapter);
                adapter.startListening();

    }
    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
