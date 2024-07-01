package com.example.mawadda3_0;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private Button mSendButton;
    private String userSex;
    private String oppositeUserSex;
    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Log.d(TAG, "onCreate: Started");

        if (getIntent().getExtras() != null) {
            matchId = getIntent().getExtras().getString("matchId");
            Log.d(TAG, "onCreate: matchId = " + matchId);
        } else {
            Log.e(TAG, "onCreate: No matchId found in intent extras");
            finish();
            return;
        }

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "onCreate: currentUserID = " + currentUserID);

        checkUserSex();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        Log.d(TAG, "onCreate: Finished setup");
    }

    private void initializeDatabaseReferences() {
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();

        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map<String, String> newMessage = new HashMap<>();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
            Log.d(TAG, "sendMessage: Message sent");
        } else {
            Log.d(TAG, "sendMessage: Empty message, not sent");
        }

        mSendEditText.setText(null);
    }

    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    Log.d(TAG, "getChatId: chatId = " + chatId);
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                } else {
                    Log.e(TAG, "getChatId: No chatId found for this match");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getChatId: DatabaseError = " + databaseError.getMessage());
            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String message = null;
                    String createdByUser = null;

                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null) {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if (message != null && createdByUser != null) {
                        Boolean currentUserBoolean = createdByUser.equals(currentUserID);
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onChildAdded: New message added");
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getChatMessages: DatabaseError = " + databaseError.getMessage());
            }
        });
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
                    Log.d(TAG, "checkUserSex: User is Male");
                    initializeDatabaseReferences();  // Initialize references once userSex is known
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "checkUserSex: DatabaseError = " + databaseError.getMessage());
            }
        });

        usersDb.child("Female").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSex = "Female";
                    oppositeUserSex = "Male";
                    Log.d(TAG, "checkUserSex: User is Female");
                    initializeDatabaseReferences();  // Initialize references once userSex is known
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "checkUserSex: DatabaseError = " + databaseError.getMessage());
            }
        });
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }
}
