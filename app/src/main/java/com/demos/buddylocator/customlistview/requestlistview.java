package com.demos.buddylocator.customlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Demos on 11/28/2017.
 */

public class requestlistview extends BaseAdapter{
    HashMap<String,String> friendsrequest;
    String[] key;
    String[] value;
    Context Context;
    Users friends;
    String userkey;
    Users userdb;

    public requestlistview(Context Context, HashMap<String,String> friendsrequest,String userkey,Users userdb) {
        this.friendsrequest= friendsrequest;
        this.Context = Context;
        key= friendsrequest.keySet().toArray(new String[0]);
        value = friendsrequest.values().toArray(new String[0]);
        this.userkey = userkey;
        this.userdb = userdb;
    }

    @Override
    public int getCount() {
        return friendsrequest.size();
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
            view = mInflater.inflate(R.layout.listview_friendsrequest, viewGroup, false);


        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mRequsetRef = mRootRef.child("User").child(value[i]);
        final View finalView = view;
        mRequsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends = dataSnapshot.getValue(Users.class);
                TextView friendname = finalView.findViewById(R.id.emailrequest);
                if(friends.getName().equals("")){
                    friendname.setText(friends.getEmail());
                }else {
                    friendname.setText(friends.getName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Button confirm = view.findViewById(R.id.confirm);
        final Button delete = view.findViewById(R.id.delete);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference mUserRef = mRootRef.child("User");
                DatabaseReference mRequest = mRootRef.child("FriendsRequest");
                DatabaseReference mFriendRef = mRootRef.child("User").child(value[i]);
                mFriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users frienddb = dataSnapshot.getValue(Users.class);
                        userdb.updateFriends(value[i],frienddb.getName());
                        mUserRef.child(userkey).child("friends").setValue(userdb.getFriends());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mUserRef.child(userkey).child("friendsrequest").child(key[i]).removeValue();
                mRequest.child(key[i]).removeValue();
                final Users[] temp = new Users[1];
                mUserRef.child(value[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        temp[0] = dataSnapshot.getValue(Users.class);
                        temp[0].updateFriends(userkey,userdb.getName());
                        mUserRef.child(value[i]).child("addfriends").child(key[i]).removeValue();
                        mUserRef.child(value[i]).child("friends").setValue(temp[0].getFriends());
                        Toast.makeText(Context, "Accept friend request complete.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mUserRef = mRootRef.child("User");
                DatabaseReference mRequest = mRootRef.child("FriendsRequest");
                mUserRef.child(userkey).child("friendsrequest").child(key[i]).removeValue();
                mRequest.child(key[i]).removeValue();
                mUserRef.child(value[i]).child("addfriends").child(key[i]).removeValue();
                Toast.makeText(Context, "Remove friend request complete.", Toast.LENGTH_LONG).show();


            }
        });



        return finalView;
    }


}
