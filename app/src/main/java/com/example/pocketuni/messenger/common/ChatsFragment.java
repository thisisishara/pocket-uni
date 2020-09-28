package com.example.pocketuni.messenger.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.ChatList;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.User;
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
import java.util.List;

import javax.annotation.Nullable;

public class ChatsFragment extends Fragment {
    private RecyclerView chatsRecyclerView;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;
    private List<User> users = new ArrayList<User>();
    private List<ChatList> chatIds = new ArrayList<ChatList>();
    private View view;
    private static final String TAG = "CHATS_FRG";


    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        chatsRecyclerView = view.findViewById(R.id.recyclerViewChats);
        chatsRecyclerView.hasFixedSize();
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = firebaseFirestore.collection("chatlist").document(firebaseUser.getUid()).collection("chatids");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                chatIds.clear();

                for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    ChatList chatListItem = new ChatList();
                    chatListItem.setChatId((String) queryDocumentSnapshot.get("userid"));
                    chatIds.add(chatListItem);
                }

                getAllChats();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getAllChats() {

        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                users.clear();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        //putting into a list of slots
                        User user = documentSnapshot.toObject(User.class);
                        for (ChatList chatId : chatIds) {
                            if (user.getUserId().equalsIgnoreCase(chatId.getChatId())) {
                                users.add(user);
                            }
                        }

                        userListAdapter = new UserListAdapter(getContext(), users, "chats");
                        chatsRecyclerView.setAdapter(userListAdapter);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                    showToast("NO USERS.");
                    userListAdapter = new UserListAdapter(getContext(), users, "chats");
                    chatsRecyclerView.setAdapter(userListAdapter);
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}