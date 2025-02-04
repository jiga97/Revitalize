package com.example.revitalize;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMmessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRefDB;

    public MessageAdapter (List<Messages> userMmessagesList)
    {
        this.userMmessagesList = userMmessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView senderMessageText, recieverMessageText;
        public CircleImageView recieverProfileImage;


        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            senderMessageText = (TextView)itemView.findViewById(R.id.sender_message_text);
            recieverMessageText = (TextView)itemView.findViewById(R.id.reciever_message_text);
            recieverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMmessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRefDB = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRefDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("image"))
                {
                    String recieverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(recieverImage).placeholder(R.drawable.profile_image).into(holder.recieverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fromMessageType.equals("text")){
            holder.recieverMessageText.setVisibility(View.INVISIBLE);
            holder.recieverProfileImage.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(messageSenderID))
            {

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);

                //change text color
                holder.senderMessageText.setTextColor(Color.BLACK);

                holder.senderMessageText.setText(messages.getMessage());
            }
            else
            {




                holder.recieverMessageText.setVisibility(View.VISIBLE);
                holder.recieverProfileImage.setVisibility(View.VISIBLE);

                holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
                //change text color
                holder.recieverMessageText.setTextColor(Color.BLACK);

                holder.recieverMessageText.setText(messages.getMessage());
            }

        }
        
    }


    @Override
    public int getItemCount()
    {
        return  userMmessagesList.size();
    }




}
