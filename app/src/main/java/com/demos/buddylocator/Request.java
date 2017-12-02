package com.demos.buddylocator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demos.buddylocator.customlistview.navigationlistview;
import com.demos.buddylocator.customlistview.requestlistview;
import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Request extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //Navigation Bar
    public static final String KEY_DRAWABLE_ID = "drawableId";
    private String[] mDrawerTitle = {"Add Name","Home","Hidden", "My Buddy", "Buddy Requests", "Sign out"};
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ListView listView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DatabaseReference mUserRef;
    private String userkey;

    private Users userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d("kuyyy", "onAuthStateChanged: ");
                }else{
                    Intent intent = new Intent(Request.this,AuthActivity.class);
                    startActivity(intent);
                }

            }
        };

        userkey=getIntent().getStringExtra("userkey");
        onUserdataUpdate(this);



    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void setupNavigationBar(boolean checked){
        //navigationbar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mListView = (ListView) findViewById(R.id.drawer);
        navigationlistview adapter = new navigationlistview(this,mDrawerTitle,userkey,checked);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                TextView nvitem = view.findViewById(R.id.item_navi);
                if(arg2==0){
                    showDialogChaneName(view);

                }else if(arg2 ==1) {
                    Intent intent = new Intent(Request.this, Home.class);
                    intent.putExtra("userdb", userdata);
                    intent.putExtra("userkey", userkey);
                    startActivity(intent);
                }
                else if (arg2==3){
                    Intent intent = new Intent(Request.this,Friends.class);

                    intent.putExtra("userdb",userdata);
                    intent.putExtra("userkey",userkey);
                    startActivity(intent);



                }
                else if(arg2==5){
                    mAuth.signOut();
                }
            }
        });


        //toggle
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Buddy Requests</font>"));
        mDrawerToggle = new ActionBarDrawerToggle(
                this,   // Context
                mDrawerLayout,  // DrawerLayout
                R.drawable.ic_drawer, // รูปภาพที่จะใช้
                R.string.drawer_open // ค่า String ในไฟล์ strings.xml

        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogChaneName(final View view){
        final Dialog dialog = new Dialog(Request.this);
        dialog.setTitle("Devahoy");
        dialog.setContentView(R.layout.dialog_custom);

        final EditText username = (EditText) dialog.findViewById(R.id.username);

        Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button buttonLogin = (Button) dialog.findViewById(R.id.button_login);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check username password
                TextView txtusername = view.findViewById(R.id.item_navi);
                txtusername.setText(username.getText());
                mUserRef.child(userkey).child("name").setValue(username.getText().toString());
                mDrawerTitle[0]=username.getText().toString();
                userdata.setName(username.getText().toString());
                Toast.makeText(Request.this, "Change Name Complete.", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });


        dialog.show();

    }

    public  void onUserdataUpdate(final Context context){
        DatabaseReference mRootRef= FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("User");
        mUserRef.child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userdata = dataSnapshot.getValue(Users.class);
                mDrawerTitle[0]=userdata.getName();
                listView = (ListView) findViewById(R.id.listview_request);
                requestlistview adapter = new requestlistview(context,userdata.getFriendsrequest(),userkey,userdata);
                if(listView.getAdapter() == null){ //Adapter not set yet.
                    listView.setAdapter(adapter);
                }
                else{ //Already has an adapter
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                }
                setupNavigationBar(userdata.getHidden());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
