package com.demos.buddylocator.customlistview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * Created by Demos on 11/29/2017.
 */

public class friendslistview extends BaseAdapter {

    HashMap<String,String> friends;
    Context Context;
    String userkey;
    Users userdb;
    String[] friendkey;
    String[] friendvalue;

    public friendslistview(Context Context, HashMap<String,String> friends, String userkey, Users userdb) {
        this.friends= friends;
        this.Context = Context;
        this.userkey = userkey;
        this.userdb = userdb;
        friendkey= friends.keySet().toArray(new String[0]);
        friendvalue = friends.values().toArray(new String[0]);
    }
    @Override
    public int getCount() {
        return friends.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater mInflater =
                (LayoutInflater)Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null)
            view = mInflater.inflate(R.layout.listview_friends, viewGroup, false);
        final View finalview = view;
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        TextView friendname = finalview.findViewById(R.id.friendname);
        friendname.setText(friendvalue[i]);

        Log.d("hi friends layout", "getView: ");
        Button deletefriends = finalview.findViewById(R.id.deletefriend);
        deletefriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(friendkey[i],finalview);
            }
        });



        return finalview;
    }

    public void showDeleteDialog(final String friendnamedelete, final View v){
        final Dialog dialog = new Dialog(Context);
        dialog.setContentView(R.layout.dialog_deletefriend);
        Button button_ok = dialog.findViewById(R.id.ok_deletefriend);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference mUserRef = mRootRef.child("User").child(userkey).child("friends");
                mUserRef.child(friendnamedelete).removeValue();
                DatabaseReference mFriendRef = mRootRef.child("User").child(friendnamedelete).child("friends");
                mFriendRef.child(userkey).removeValue();
                Toast.makeText(Context, "Remove friend complete.", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        Button button_cancel = dialog.findViewById(R.id.cancel_deletefriend);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
