package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDb;
    private String currentUId;
    ListView listView;
    List<cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth= FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();


        checkUserSex();


        rowItems = new ArrayList<cards>();


        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserSex).child(userId ).child("connections").child("pass").child(currentUId).setValue(true);
                //If you want to use it just cast it (String) dataObject

                Toast.makeText(HomeActivity.this, "Pass", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("like").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(HomeActivity.this, "Like", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }
private void isConnectionMatch(String userId) {
    DatabaseReference currentUserConnectionsDb = usersDb.child(userSex).child(currentUId).child("connections").child("like").child(userId);
    currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                showInfoPopup();
                String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                usersDb.child(oppositeUserSex).child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                usersDb.child(userSex).child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });
}

    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDb = FirebaseDatabase. getInstance ( ).getReference().child("Users") . child ( "Male") ;
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot,  String previousChildName) {
                if (dataSnapshot.getKey().equals(user.getUid())){
                    userSex="Male";
                    oppositeUserSex="Female";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged( DataSnapshot snapshot,  String previousChildName) {
            }
            @Override
            public void onChildRemoved( DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved( DataSnapshot snapshot,  String previousChildName) {
            }
            @Override
            public void onCancelled( DatabaseError error) {
            }
        });

        DatabaseReference femaleDb = FirebaseDatabase. getInstance ( ).getReference().child("Users") . child ( "Female") ;
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot,  String previousChildName) {
                if (dataSnapshot.getKey().equals(user.getUid())){
                    userSex="Female";
                    oppositeUserSex="Male";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged( DataSnapshot snapshot,  String previousChildName) {
            }
            @Override
            public void onChildRemoved( DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved( DataSnapshot snapshot,  String previousChildName) {
            }
            @Override
            public void onCancelled( DatabaseError error) {
            }
        });


    }

    public void getOppositeSexUsers() {
        DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeSexDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("like").hasChild(currentUId)) {
                        String profileImageUrl = "default";
                        if (dataSnapshot.child("profileImageUrl").getValue() != null && !dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void showInfoPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Match Found")
                .setMessage("You have a new match!")
                .setPositiveButton("OK", null) // Set null OnClickListener to dismiss dialog without action
                .show();
    }



    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(HomeActivity.this, "Successful Sign Out", Toast.LENGTH_SHORT).show();
        finish();
        return;
    }
    public void goToSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(HomeActivity.this, MatchesActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;
    }

    public void goToChat(View view) {
        Intent intent = new Intent(HomeActivity.this, MatchesActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;
    }
    public void goToHome(View view) {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        return;
    }
    public void goToFilter(View view) {
        Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
        startActivity(intent);
        return;
    }
}


