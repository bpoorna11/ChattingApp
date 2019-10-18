package com.balakrishnan.poorna.chattingapp;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsFragment extends Fragment {
    private View ContactsView;
    private RecyclerView myContactsList;
    private ArrayList<Contacts> mArrayList = new ArrayList<>();
    static HashSet<String> allusers=new HashSet<>();
    static HashSet<String> firebaseContacts=new HashSet<>();
    //private RecyclerView mRecyclerView1;
    private CustomContactAdapter mAdapter;
    private DatabaseReference ContacsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    Cursor phones;
    public ContactsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        myContactsList = (RecyclerView) ContactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CustomContactAdapter(mArrayList);
        myContactsList.setItemAnimator( new DefaultItemAnimator());
        myContactsList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        myContactsList.setAdapter(mAdapter);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userId = ds.child("status").getValue().toString();
                    allusers.add(userId);
                    Log.d("TAG", userId);
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
                phones=getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,"upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
                String lastnam="";
                while (phones.moveToNext()){
                    String displayName=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phnno=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String phnnowithoutsp=phnno.replaceAll("\\s", "");
                    if(phnno.contains("+91"))
                         phnnowithoutsp=phnnowithoutsp.substring(3);
                    if(!firebaseContacts.contains(phnnowithoutsp)) {
                        //System.out.println(firebaseContacts+" "+phnno);
                        Contacts contacts = new Contacts(displayName, phnno);
                        mArrayList.add(contacts);
                        if (displayName.equals(lastnam)) {
                            mArrayList.remove(contacts);
                        } else {
                            lastnam = displayName;
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        UsersRef.addListenerForSingleValueEvent(eventListener);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ContacsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        return ContactsView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
