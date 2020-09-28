package com.example.pocketuni.messenger.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketuni.R;
import com.example.pocketuni.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageViewerAdapter extends RecyclerView.Adapter<MessageViewerAdapter.MessagesListViewHolder> {
    private List<Message> messages = new ArrayList<Message>();
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String sender, receiver, message, messageDateTime;
    private Boolean readStatus, deliveredStates;

    private static final int MSG_RIGHT = 1;
    private static final int MSG_LEFT = 0;

    public MessageViewerAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if(viewType == MSG_RIGHT){
            view = layoutInflater.inflate(R.layout.layout_message_right, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.layout_message_left, parent, false);
        }

        return new MessageViewerAdapter.MessagesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesListViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageContent.setText(message.getMessageBody());

        if(message.getSentDate() != null) {
            Date sentDate = message.getSentDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '@' hh:mm aaa");
            String sentDateString = dateFormat.format(sentDate);
            holder.messageDateTime.setText(sentDateString);
        } else {
            holder.messageDateTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(messages.get(position).getSenderId().equalsIgnoreCase(firebaseAuth.getCurrentUser().getUid())){
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    public class MessagesListViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageDateTime;

        public MessagesListViewHolder(@NonNull View msgView) {
            super(msgView);

            messageContent = itemView.findViewById(R.id.emailTextView);
            messageDateTime = itemView.findViewById(R.id.messageDateTimeTextView);
        }
    }
}
