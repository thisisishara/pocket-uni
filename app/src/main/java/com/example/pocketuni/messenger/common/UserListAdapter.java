package com.example.pocketuni.messenger.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pocketuni.R;
import com.example.pocketuni.model.Message;
import com.example.pocketuni.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UsersListViewHolder> {
    private List<User> users = new ArrayList<User>();
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String username, email, dp, status, type;
    private static final String USERS = "users";
    private static final String CHATS = "chats";
    private static final String  TAG = "USR_LST_ADP";

    public UserListAdapter(Context context, List<User> users, String type){
        this.context = context;
        this.users = users;
        this.type = type;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_user_item, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new UserListAdapter.UsersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, final int position) {
        if(users.get(position).getUserType().equalsIgnoreCase("ADMIN")){
            email = "Admin";
        }

        holder.username.setText(users.get(position).getName());
        holder.userItNumber.setText(users.get(position).getEmail());

        if(this.type == USERS) {
            holder.deleteButton.setVisibility(View.GONE);//context.getResources().getDrawable(R.drawable.notifgreen));
        }

        if(users.get(position).getDp().equalsIgnoreCase("default")) {
            holder.userProfilePicture.setImageDrawable(context.getResources().getDrawable(R.drawable.userchatdefault));
        } else {
            Glide.with(context).load(users.get(position).getDp()).into(holder.userProfilePicture);
        }

        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("userName", users.get(position).getName());
                intent.putExtra("userId", users.get(position).getUserId());
                intent.putExtra("userEmail", users.get(position).getEmail());
                intent.putExtra("userProfilePicture", users.get(position).getDp());
                intent.putExtra("userType", users.get(position).getUserType());
                intent.putExtra("batch", users.get(position).getBatch());
                intent.putExtra("course", users.get(position).getCourse());
                intent.putExtra("userDp", users.get(position).getDp());
                context.startActivity(intent);
            }
        });

        if (this.type == CHATS) {
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CollectionReference collectionReference = firebaseFirestore.collection("chats").document(firebaseAuth.getCurrentUser().getUid()+""+users.get(position).getUserId()).collection("messages");
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().size()>0){
                                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    Message message = queryDocumentSnapshot.toObject(Message.class);
                                    if((message.getSenderId().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid()) && message.getReceiverId().equalsIgnoreCase(users.get(position).getUserId())) || (message.getReceiverId().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid()) && (message.getSenderId().equalsIgnoreCase(users.get(position).getUserId())))) {
                                        queryDocumentSnapshot.getReference().delete();
                                        Log.w(TAG, "chat has been deleted.");
                                    }
                                }

                                deleteChatlist(users.get(position).getUserId());
                            }
                        }
                    });
                }
            });
        }
    }

    private void deleteChatlist(final String chatlistId){
        final CollectionReference collectionReference = firebaseFirestore.collection("chatlist").document(firebaseAuth.getCurrentUser().getUid()).collection("chatids");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult().size()>0){
                    for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        if(((String) queryDocumentSnapshot.get("userid")).equalsIgnoreCase(chatlistId)){
                            queryDocumentSnapshot.getReference().delete();
                            showToast("CHAT DELETED.");
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void showToast (String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public class UsersListViewHolder extends RecyclerView.ViewHolder {
        TextView username, userItNumber;
        ImageView userProfilePicture;
        CardView userCard, deleteButton;

        public UsersListViewHolder(@NonNull View userView) {
            super(userView);

            username = itemView.findViewById(R.id.userNameTextView);
            userItNumber = itemView.findViewById(R.id.emailTextView);
            userProfilePicture = itemView.findViewById(R.id.profilePictureImageView);
            userCard = itemView.findViewById(R.id.userCard);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void setUsers(ArrayList<User> filteredUsers){
        users = filteredUsers;
        notifyDataSetChanged();
    }
}
