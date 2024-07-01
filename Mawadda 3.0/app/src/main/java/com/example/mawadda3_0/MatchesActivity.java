package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private String currentUserID;
    private String userSex;
    private String oppositeUserSex;
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matches);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        checkUserSex();
    }

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        usersDb.child("Male").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSex = "Male";
                    oppositeUserSex = "Female";
                    getUserMatchId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        usersDb.child("Female").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSex = "Female";
                    oppositeUserSex = "Male";
                    getUserMatchId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userSex).child(currentUserID).child("connections").child("matches");

        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(oppositeUserSex).child(key);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String hobby = "";
                    String education = "";
                    String plan = "";
                    String age = "";
                    String profileImageUrl = "";


                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("age").getValue() != null) {
                        age = dataSnapshot.child("age").getValue().toString();
                    }
                    if (dataSnapshot.child("hobby").getValue() != null) {
                        hobby = dataSnapshot.child("hobby").getValue().toString();
                    }
                    if (dataSnapshot.child("education").getValue() != null) {
                        education = dataSnapshot.child("education").getValue().toString();
                    }
                    if (dataSnapshot.child("plan").getValue() != null) {
                        plan = dataSnapshot.child("plan").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }


                    MatchesObject obj = new MatchesObject( name, age, hobby, education, plan, profileImageUrl, userId);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MatchesActivity.this, SettingsActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MatchesActivity.this, MatchesActivity.class);
        intent.putExtra("userSex",userSex);
        startActivity(intent);
        return;
    }
    public void goToChat(View view) {
        Intent intent = new Intent(MatchesActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
    public void goToHome(View view) {
        Intent intent = new Intent(MatchesActivity.this, HomeActivity.class);
        startActivity(intent);
        return;
    }

    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
}
