package com.example.pocketuni.messenger.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UsersFragment extends Fragment {
    private RecyclerView usersRecyclerView;
    private EditText searchEditText;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter usersAdaptor;
    private View view;
    private static final String TAG = "USER_FRG";
    List<User> users = new ArrayList<User>();

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        usersRecyclerView = view.findViewById(R.id.recyclerViewUsers);
        usersRecyclerView.hasFixedSize();
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchEditText = view.findViewById(R.id.editTextSearch);

        getAllUsers();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return view;
    }

    private void filter(String searchText){
        ArrayList<User> filteredUsers = new ArrayList<User>();

        for(User user: users){
            if(user.getName().toLowerCase().contains(searchText.toLowerCase().trim())){
                filteredUsers.add(user);
            }
        }

        usersAdaptor.setUsers(filteredUsers);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getAllUsers(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //getting users
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                users.clear();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        //putting into a list of slots
                        User user = documentSnapshot.toObject(User.class);
                        if(! user.getUserId().equalsIgnoreCase(CurrentUser.getUserId())) {
                            users.add(user);
                        }
                        usersAdaptor = new UserListAdapter(getContext(), users, "users");
                        usersRecyclerView.setAdapter(usersAdaptor);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                    showToast("NO USERS.");
                    usersAdaptor = new UserListAdapter(getContext(), users, "users");
                    usersRecyclerView.setAdapter(usersAdaptor);
                }
            }
        });
    }

    private void showToast (String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}