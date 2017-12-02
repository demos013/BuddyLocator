package com.demos.buddylocator.customlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Demos on 11/27/2017.
 */

public class navigationlistview extends BaseAdapter {

    Context mContext;
    String[] strName;
    String userkey;
    boolean checked;


    Users userdb;

    public navigationlistview(Context mContext, String[] strName,String userkey,boolean checked) {
        this.mContext = mContext;
        this.strName = strName;
        this.userkey = userkey;
        this.checked=checked;
    }

    @Override
    public int getCount() {
        return strName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null)
            view = mInflater.inflate(R.layout.listview_row, viewGroup, false);
        TextView itemname = view.findViewById(R.id.item_navi);
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserRef = mRootRef.child("User").child(userkey);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userdb = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       final Switch naviswitch = view.findViewById(R.id.switch_navi);
       naviswitch.setChecked(checked);
       naviswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b){
                   DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                   DatabaseReference mUserRef = mRootRef.child("User").child(userkey);
                   String key = mUserRef.getKey();
                   Map<String, Object> childUpdates = new HashMap<>();
                   childUpdates.put("hidden", b);
                   mUserRef.updateChildren(childUpdates);

               }else {
                   DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                   DatabaseReference mUserRef = mRootRef.child("User").child(userkey);
                   String key = mUserRef.getKey();
                   Map<String, Object> childUpdates = new HashMap<>();
                   childUpdates.put("/hidden/", b);
                   mUserRef.updateChildren(childUpdates);
               }
           }
       });
        itemname.setText(strName[i]);
        if(!strName[i].equalsIgnoreCase("Hidden")){
            naviswitch.setVisibility(View.GONE);

        }
        if (strName[i].equalsIgnoreCase("")) {
            itemname.setText("Add Name");
        }
        /*if(i==3){
            HashMap<String,String> request = userdb.getFriendsrequest();
            itemname.setText(strName[i]+" ("+request.size()+")");*/





        return view;
    }
}


