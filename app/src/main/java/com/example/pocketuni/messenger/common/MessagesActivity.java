package com.example.pocketuni.messenger.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pocketuni.R;
import com.example.pocketuni.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MessagesActivity extends AppCompatActivity {
    private String userName, email, userId, dp, userType, batch, course;
    private TextView usernameTextView, statusTextView, emailTextView;
    private ImageView dpImageView, statusImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private EditText newMessageEditText;
    private CardView sendButtonCardView;
    private String sender, receiver;
    private MessageViewerAdapter messageViewerAdapter;
    private RecyclerView messagesRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<Message> messages = new ArrayList<Message>();
    private static final String TAG = "USR_MSG_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        usernameTextView = findViewById(R.id.userNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        dpImageView = findViewById(R.id.profilePictureImageView);
        statusImageView = findViewById(R.id.userStatus);
        sendButtonCardView = findViewById(R.id.sendBtn);
        newMessageEditText = findViewById(R.id.sendMsgTextBox);
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages);

        //getting Extras
        intent = getIntent();
        userName = intent.getExtras().getString("userName");
        email = intent.getExtras().getString("userEmail");
        dp = intent.getExtras().getString("userDp");
        userId = intent.getExtras().getString("userId");
        userType = intent.getExtras().getString("userType");
        course = intent.getExtras().getString("userCourse");
        batch = intent.getExtras().getString("userBatch");

        sender = firebaseUser.getUid();
        receiver = intent.getExtras().getString("userId");

        usernameTextView.setText(userName);
        emailTextView.setText(email);

        if (dp.equalsIgnoreCase("default")) {
            dpImageView.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefault));
        } else {
            //GlideLoad
            Glide.with(MessagesActivity.this).load(dp).into(dpImageView);
        }

        getUserStatus();

        messagesRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(MessagesActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        getPreviousMessages();

        sendButtonCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newMessage = newMessageEditText.getText().toString().trim();
                if (!newMessage.equalsIgnoreCase("")) {
                    sendMessage(newMessage);
                } else {
                    showToast("TYPE A MESSAGE FIRST.");
                }


            }
        });
    }

    private void getUserStatus(){
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null) {
                    String status = (String) documentSnapshot.get("status");
                    if(status.equalsIgnoreCase("online")){
                        statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.notifblue));
                    } else {
                        statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.notifred));
                    }
                }
            }
        });
    }

    private void sendMessage(String newMessage) {
        Calendar calendar = Calendar.getInstance();
        Date sendingDate = calendar.getTime();

        final Map<String, Object> messageInfo = new HashMap<String, Object>();
        messageInfo.put("messageBody", newMessage);
        messageInfo.put("senderId", sender);
        messageInfo.put("receiverId", receiver);
        messageInfo.put("sentDate", sendingDate);

        final Map<String, Object> newSenderId = new HashMap<String, Object>();
        newSenderId.put("myId", sender);

        final Map<String, Object> newReceiverId = new HashMap<String, Object>();
        newReceiverId.put("myId", receiver);

        final DocumentReference documentReferenceReceiverChat = firebaseFirestore.collection("chats").document(receiver+firebaseAuth.getCurrentUser().getUid());
        documentReferenceReceiverChat.set(newReceiverId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReferenceReceiverMessages = firebaseFirestore.collection("chats").document(receiver+firebaseAuth.getCurrentUser().getUid()).collection("messages").document();
                    documentReferenceReceiverMessages.set(messageInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG,"Chat saved in sender's end");
                                newMessageEditText.setText("");
                            }
                        }
                    });
                }
            }
        });

        DocumentReference documentReferenceReceiverChatlistCollection = firebaseFirestore.collection("chatlist").document(receiver);
        documentReferenceReceiverChatlistCollection.set(newReceiverId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> newChatListItemReceiver = new HashMap<String, Object>();
                    newChatListItemReceiver.put("userid", sender);

                    DocumentReference documentReferenceReceiverChatListItem = firebaseFirestore.collection("chatlist").document(receiver).collection("chatids").document(sender);
                    documentReferenceReceiverChatListItem.set(newChatListItemReceiver).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "user is added to chat list at the sender's end");
                            }
                        }
                    });
                }
            }
        });

        final DocumentReference documentReferenceSenderChat = firebaseFirestore.collection("chats").document(firebaseAuth.getCurrentUser().getUid()+ "" + receiver);
        documentReferenceSenderChat.set(newSenderId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReferenceSenderMessages = firebaseFirestore.collection("chats").document(firebaseAuth.getCurrentUser().getUid() + "" + receiver).collection("messages").document();
                    documentReferenceSenderMessages.set(messageInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG,"Chat saved in receivers end");
                            }
                        }
                    });
                }
            }
        });

        DocumentReference documentReferenceSenderChatlistCollection = firebaseFirestore.collection("chatlist").document(firebaseAuth.getCurrentUser().getUid());
        documentReferenceSenderChatlistCollection.set(newSenderId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> newChatListItemSender = new HashMap<String, Object>();
                    newChatListItemSender.put("userid", receiver);

                    DocumentReference documentReferenceSenderChatListItem = firebaseFirestore.collection("chatlist").document(firebaseAuth.getCurrentUser().getUid()).collection("chatids").document(receiver);
                    documentReferenceSenderChatListItem.set(newChatListItemSender).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "user is added to chat list at the raceiver's end");
                            }
                        }
                    });
                }
            }
        });
    }

    private void getPreviousMessages() {
        CollectionReference collectionReference = firebaseFirestore.collection("chats").document(sender+receiver).collection("messages");
        collectionReference.orderBy("sentDate").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                messages.clear();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    Log.d(TAG, "Current data: " + queryDocumentSnapshots.getDocumentChanges());

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Message message = documentSnapshot.toObject(Message.class);
                        if ((message.getSenderId().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid()) && message.getReceiverId().equalsIgnoreCase(receiver)) || (message.getReceiverId().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid()) && (message.getSenderId().equalsIgnoreCase(receiver)))) {
                            messages.add(message);
                        }
                        messageViewerAdapter = new MessageViewerAdapter(MessagesActivity.this, messages);
                        messagesRecyclerView.setAdapter(messageViewerAdapter);
                        messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                    }
                    Log.i(TAG, messageViewerAdapter.getItemCount() + " messages are found.");

                } else {
                    Log.d(TAG, "Current data: null");
                    messageViewerAdapter = new MessageViewerAdapter(MessagesActivity.this, messages);
                    messagesRecyclerView.setAdapter(messageViewerAdapter);
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MessagesActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserOnlineStatus(String status){
        HashMap<String,Object> userStatus = new HashMap<String, Object>();
        userStatus.put("status", status);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(userStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserOnlineStatus("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserOnlineStatus("online");
    }
}
