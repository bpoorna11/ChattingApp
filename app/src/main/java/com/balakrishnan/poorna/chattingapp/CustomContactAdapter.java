package com.balakrishnan.poorna.chattingapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomContactAdapter extends RecyclerView.Adapter<CustomContactAdapter.MyViewHolder> {
    // declaring some fields.
    private ArrayList<Contacts> arrayList = new ArrayList<>();

    public CustomContactAdapter(ArrayList<Contacts> arrayList) {
        this.arrayList = arrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number, addedOn;
        public MyViewHolder(View itemView) {
            super(itemView);
            Log.v("ViewHolder","in View Holder");
            name = itemView.findViewById(R.id.user_profile_name);
            number = itemView.findViewById(R.id.user_status);

        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int   viewType) {
        Log.v("CreateViewHolder", "in onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_display_layout,parent,false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int  position) {
        Log.v("BindViewHolder", "in onBindViewHolder");
        Contacts contact = arrayList.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getStatus());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}