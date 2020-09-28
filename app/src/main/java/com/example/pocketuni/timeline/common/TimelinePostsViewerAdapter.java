package com.example.pocketuni.timeline.common;

import android.content.Context;
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
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.NoticeItem;
import com.example.pocketuni.timeline.std.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class TimelinePostsViewerAdapter extends RecyclerView.Adapter<TimelinePostsViewerAdapter.TimelinePostsViewerViewHolder>  {
    private static final int ADMIN = 0;
    private static final int STDNT = 1;
    private static final String TAG = "TLP_PVW_ADP";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private List<NoticeItem> notices = new ArrayList<NoticeItem>();
    private Context context;
    private int viewerType = -1;

    public TimelinePostsViewerAdapter(Context context, List<NoticeItem> notices, int viewerType){
        this.context = context;
        this.notices = notices;
        this.viewerType = viewerType;
    }

    @NonNull
    @Override
    public TimelinePostsViewerAdapter.TimelinePostsViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_timeline_post, parent, false);
        return new TimelinePostsViewerAdapter.TimelinePostsViewerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimelinePostsViewerAdapter.TimelinePostsViewerViewHolder holder, final int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        NoticeItem notice = notices.get(position);
        final String[] adminDp = new String[1];
        System.out.println(notices.get(position).getAdminId());

        if(notices.size()>0) {
            DocumentReference admin = firebaseFirestore.collection("users").document(notices.get(position).getAdminId());
            admin.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().get("userId") != null) {
                        adminDp[0] = (String) task.getResult().get("dp");
                    } else {
                        adminDp[0] = "default";
                    }

                    if (adminDp[0].equalsIgnoreCase("default")) {
                        holder.profilePictureImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.userchatdefault));
                    } else {
                        Glide.with(context).load(adminDp[0]).into(holder.profilePictureImageView);
                    }
                }
            });
        }

        holder.userNameTextView.setText(notices.get(position).getAdminName());
        holder.emailTextView.setText(notices.get(position).getAdminEmail());

        Date postedDate = notices.get(position).getNoticeDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '@' hh:mm aaa");
        String postedDateString = dateFormat.format(postedDate);
        holder.postedTimeTextView.setText(postedDateString);

        if(notices.get(position).getNoticeTitle()!=null) {
            holder.postTextView.setText(notices.get(position).getNoticeTitle() + "\n" + notices.get(position).getNoticeContent());
        } else {
            holder.postTextView.setText(notices.get(position).getNoticeContent());
        }

        if(this.viewerType == STDNT){
            holder.closeButtonCardView.setVisibility(View.GONE);
            holder.interestedButtonCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CollectionReference collectionReference = firebaseFirestore.collection("timelineposts").document(notices.get(position).getNoticeId()).collection("interestedusers");
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                collectionReference.document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().get("userid")!=null){
                                            if(task.getResult().get("userid").toString().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid())) {
                                                task.getResult().getReference().delete();
                                                showToast("MARKED AS NOT INTERESTED.");
                                                holder.interestedButtonImageView.setBackgroundColor(context.getResources().getColor(R.color.color_post_not_interested));
                                            }
                                        } else {
                                            HashMap<String, Object> newInteraction = new HashMap<String, Object>();
                                            newInteraction.put("userid", firebaseAuth.getCurrentUser().getUid());

                                            collectionReference.document(firebaseAuth.getCurrentUser().getUid()).set(newInteraction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    showToast("MARKED AS INTERESTED.");
                                                    holder.interestedButtonImageView.setBackgroundColor(context.getResources().getColor(R.color.color_post_interested));
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                showToast("YOU CANNOT INTERACT WITH THIS POST.");
                            }
                        }
                    });
                }
            });
        } else {
            holder.interestedButtonImageView.setBackgroundColor(context.getResources().getColor(R.color.color_post_not_interested));
            holder.closeButtonCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DocumentReference documentReference = firebaseFirestore.collection("timelineposts").document(notices.get(position).getNoticeId());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                task.getResult().getReference().delete();
                                showToast("POST DELETED.");
                            } else {
                                showToast("FAILED TO DELETE THE POST.");
                            }
                        }
                    });
                }
            });
        }

        holder.interestedCounterTextView.setText("");
        CollectionReference interactions = firebaseFirestore.collection("timelineposts").document(notices.get(position).getNoticeId()).collection("interestedusers");
        interactions.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }

                if(queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0){
                    holder.interestedCounterTextView.setText(queryDocumentSnapshots.size()+"");
                } else {
                    holder.interestedCounterTextView.setText("");
                }
            }
        });

        if(this.viewerType == STDNT) {
            DocumentReference getMyInteraction = firebaseFirestore.collection("timelineposts").document(notices.get(position).getNoticeId()).collection("interestedusers").document(firebaseAuth.getCurrentUser().getUid());
            getMyInteraction.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().get("userid") != null) {
                        holder.interestedButtonImageView.setBackgroundColor(context.getResources().getColor(R.color.color_post_interested));
                    } else {
                        holder.interestedButtonImageView.setBackgroundColor(context.getResources().getColor(R.color.color_post_not_interested));
                    }
                }
            });
        }

        CollectionReference userInteractions = firebaseFirestore.collection("timelineposts").document(notices.get(position).getNoticeId()).collection("interestedusers");
        userInteractions.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }

                if(queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0){
                    holder.interestedCounterTextView.setText(queryDocumentSnapshots.size()+"");
                } else {
                    holder.interestedCounterTextView.setText("0");
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public class TimelinePostsViewerViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, emailTextView, postedTimeTextView, interestedCounterTextView, postTextView;
        CardView closeButtonCardView, interestedButtonCardView;
        ImageView profilePictureImageView, closeButtonImageView, interestedButtonImageView;

        public TimelinePostsViewerViewHolder(@NonNull View postsView) {
            super(postsView);

            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            postedTimeTextView = itemView.findViewById(R.id.postedTimeTextView);
            interestedCounterTextView = itemView.findViewById(R.id.interestedCountTextView);
            postTextView = itemView.findViewById(R.id.postTextView);
            closeButtonCardView = itemView.findViewById(R.id.closeButtonContainer);
            interestedButtonCardView = itemView.findViewById(R.id.interestedButtonContainer);
            profilePictureImageView = itemView.findViewById(R.id.profilePictureImageView);
            closeButtonImageView = itemView.findViewById(R.id.closeButton);
            interestedButtonImageView = itemView.findViewById(R.id.interestedButton);
        }
    }

    private void showToast (String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
