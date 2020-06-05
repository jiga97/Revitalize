package com.example.revitalize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String recieverUserID,Sender_user_ID, Current_State;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton;
    private DatabaseReference userRefDB, ChatRefDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userRefDB = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRefDB= FirebaseDatabase.getInstance().getReference().child("Chat Request");


        recieverUserID = getIntent().getExtras().get("visit_user_id").toString();
        Sender_user_ID = mAuth.getCurrentUser().getUid();

        userProfileImage = (CircleImageView)findViewById(R.id.visit_profile_image);
        userProfileName = (TextView)findViewById(R.id.visit_profile_name);
        userProfileStatus = (TextView)findViewById(R.id.visit_profile_status);
        sendMessageRequestButton = (Button)findViewById(R.id.send_message_request_button);
        Current_State = "new";


        RetrieveUserInfo();

    }

    private void RetrieveUserInfo()
    {
        userRefDB.child(recieverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();
                }
                else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest()
    {

        ChatRefDB.child(Sender_user_ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(recieverUserID))
                        {
                            String request_type = dataSnapshot.child(recieverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                Current_State = "request_sent";
                                sendMessageRequestButton.setText("Cancel Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        
        if(!Sender_user_ID.equals(recieverUserID))
        {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);

                    if(Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                }
            });
        }
        else
        {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void SendChatRequest()
    {
        ChatRefDB.child(Sender_user_ID).child(recieverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ChatRefDB.child(recieverUserID).child(Sender_user_ID)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                Current_State = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}