package com.demos.buddylocator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.demos.buddylocator.model.FriendsRequest;
import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

public class Home extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleMap mMap;

    //Navigation Bar
    private String[] mDrawerTitle = {"Add Name","Home","Hidden", "My Buddy", "Buddy Requests", "Sign out"};
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DatabaseReference mUserRef;
    private String userkey;
    private String userEmail;
    private Users userdata;

    //location
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int MY_REQUEST_CODE = 100;
    public HashMap<String,Users> allfriendslocation;
    public Context context;
    boolean firstmove = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermission();
        context = this;
        allfriendslocation= new HashMap<>();
        //map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                    mUserRef = mRootRef.child("User");
                    mUserRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userdata = dataSnapshot.getValue(Users.class);
                            updateLocation();
                            if(userdata!=null){
                                String name = userdata.getName();
                                userEmail = userdata.getEmail();
                                boolean checked = userdata.getHidden();
                                if (!name.equalsIgnoreCase("")) {
                                    mDrawerTitle[0] = name;
                                }
                                checkFriendsRequest();
                                allfriendslocation.put(userEmail,userdata);
                                updateFriendLocation(userdata.getFriends());
                                setupNavigationBar(checked);
                            }else{
                                System.exit(0);
                                Intent intent = new Intent(Home.this, AuthActivity.class);
                                startActivity(intent);

                            }


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Intent intent = new Intent(Home.this, AuthActivity.class);
                            startActivity(intent);

                        }
                    });



                    userkey = user.getUid();


                } else {
                    SmartLocation.with(context)
                            .location()
                            .stop();
                    Intent intent = new Intent(Home.this,AuthActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };



    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(!firstmove){
            updateLocation();
        }

        Log.d(userkey, "onLocationUpdated: ");


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        SmartLocation.with(this)
                .location()
                .stop();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

    }


    public void setupNavigationBar(boolean checked){
        //navigationbar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mListView = (ListView) findViewById(R.id.drawer);
        navigationlistview adapter = new navigationlistview(this,mDrawerTitle,userkey,checked);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                if(arg2==0){
                    showDialogChaneName(view);

                }

                else if (arg2==3){
                    Intent intent = new Intent(Home.this,Friends.class);
                    intent.putExtra("userdb",userdata);
                    intent.putExtra("userkey",userkey);
                    startActivity(intent);

                }
                else if(arg2==4){
                    Intent intent = new Intent(Home.this,Request.class);
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
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Buddy Locator</font>"));
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






    public void addFriends(View view){
        showDialogAddFriends(view);
    }

    public void showDialogChaneName(final View view){
        final Dialog dialog = new Dialog(Home.this);
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
                userdata.setName(mDrawerTitle[0]);
                Toast.makeText(Home.this, "Change Name Complete.", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });


        dialog.show();

    }

    public void showDialogAddFriends(final View view){

        final Dialog dialog = new Dialog(Home.this);
        dialog.setContentView(R.layout.dialog_addfriend);

        final EditText receiver = (EditText) dialog.findViewById(R.id.receiver);

        Button buttonCancel = (Button) dialog.findViewById(R.id.button_addfriend_cancel);
        Button buttonAdd = (Button) dialog.findViewById(R.id.button_add);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsRequest request = new FriendsRequest(userkey,receiver.getText().toString());
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                String[] split = receiver.getText().toString().split("@");
                mRootRef.child("FriendsRequest").child(userkey+split[0]).setValue(request);
                userdata.updateAddFriends(userkey+split[0],receiver.getText().toString());
                mUserRef.child(userkey).setValue(userdata);
                Toast.makeText(Home.this, "Send friend invite to "+receiver.getText()+" complete", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });


        dialog.show();

    }

    public void checkFriendsRequest(){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mCheckRequest = mRootRef.child("FriendsRequest");

        mCheckRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                FriendsRequest request = dataSnapshot.getValue(FriendsRequest.class);
                if(request.getReciever().equalsIgnoreCase(userEmail)){
                    userdata.updateFriendsRequest(dataSnapshot.getKey(),request.getSender());
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("friendsrequest",userdata.getFriendsrequest() );
                    mUserRef.child(userkey).updateChildren(childUpdates);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                FriendsRequest request = dataSnapshot.getValue(FriendsRequest.class);
                if(request.getReciever().equalsIgnoreCase(userEmail)){
                    userdata.updateFriendsRequest(dataSnapshot.getKey(),request.getSender());
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("friendsrequest",userdata.getFriendsrequest() );
                    mUserRef.child(userkey).updateChildren(childUpdates);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_REQUEST_CODE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_REQUEST_CODE);
            }
        }
    }

    public  void updateLocation(){
        if(SmartLocation.with(this).location().state().locationServicesEnabled()) {
            SmartLocation.with(this)
                    .location()
                    .config(LocationParams.NAVIGATION)
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            Log.d(String.valueOf(userdata.getHidden()), "onLocationUpdated: ");
                            if(!userdata.getHidden()){
                                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference mUsersRef = mRootRef.child("User").child(userkey);
                                Log.d(userkey, "onLocationUpdated: ");
                                Map<String,Object> updatelatitude = new HashMap<>();
                                updatelatitude.put("latitude",location.getLatitude());
                                Log.d(String.valueOf(location.getLatitude()), "onLocationUpdated: ");

                                Map<String,Object> updatelongitude = new HashMap<>();
                                updatelongitude.put("longitude",location.getLongitude());

                                mUsersRef.updateChildren(updatelatitude);
                                mUsersRef.updateChildren(updatelongitude);
                                if(firstmove){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 13.0f));
                                    firstmove=false;
                                }
                                userdata.setLatitude(location.getLatitude());
                                userdata.setLongitude(location.getLongitude());
                                updateMarker(userdata);

                            }else {
                                if(firstmove){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 13.0f));
                                    firstmove=false;
                                }
                                userdata.setLatitude(location.getLatitude());
                                userdata.setLongitude(location.getLongitude());
                                updateMarker(userdata);
                                Log.d("hidden jaaa", "onLocationUpdated: ");
                            }
                        }
                    });
        } else {
            //do someting
        }
    }

    public void updateFriendLocation(HashMap<String,String> myfriend){
        String[] key = myfriend.keySet().toArray(new String[0]);
        String[] value = myfriend.values().toArray(new String[0]);
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        for(int i=0;i<key.length;i++){
            DatabaseReference mUsersRef = mRootRef.child("User").child(key[i]);
            mUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users friend = dataSnapshot.getValue(Users.class);
                    Log.d(friend.getLatitude()+" "+friend.getEmail(), "onDataChange: ");
                    updateMarker(friend);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }

    public void updateMarker(Users post){
        mMap.clear();
        allfriendslocation.put(post.getEmail(), post);
        Log.d("sizemap", String.valueOf(allfriendslocation.size()));

        for (Map.Entry<String, Users> entry : allfriendslocation.entrySet()) {
            Log.d(entry.getKey()+" "+userEmail, "updateMarker: ");
            if (entry.getKey().equals(userEmail)) {
                Log.d(entry.getValue().getLatitude()+" "+userEmail, "updateMarker: ");
                LatLng IAmHere = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
                mMap.addMarker(new MarkerOptions().position(IAmHere).title(entry.getValue().getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker_lightorange)));
            } else {
                LatLng IAmHere = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
                mMap.addMarker(new MarkerOptions().position(IAmHere).title(entry.getValue().getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker_sky)));

            }
        }

    }









}
