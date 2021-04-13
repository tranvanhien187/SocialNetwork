package com.example.fakebook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fakebook.R;
import com.example.fakebook.adapter.MessageAdapter;
import com.example.fakebook.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {
    String emailFriendToChat;
    RecyclerView recyclerView;
    MessageAdapter adapter;
    ArrayList<Message> messageArrayList = new ArrayList<>();
    EditText edtTypeMessage;
    ImageButton ibtnSendMessage;
    private Toolbar toolbar;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String emailTmp = mAuth.getCurrentUser().getEmail();
    String emailCurrentUser = emailTmp.substring(0, emailTmp.length() - "@gmail.com".length());

    boolean isFirst = true;

    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        initWidget();
        Intent intent = getIntent();

        emailFriendToChat = intent.getStringExtra("emailToChat");

        adapter = new MessageAdapter(messageArrayList, MessageActivity.this, emailCurrentUser, emailFriendToChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
        linearLayoutManager.setStackFromEnd(true);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        // cập nhật tin nhắn
        loadMessageForFirstTime();


        ibtnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTypeMessage.getText().toString().equals("")) {
                    Toast.makeText(MessageActivity.this, "Type something", Toast.LENGTH_SHORT).show();
                } else {

                    addMessageToConversation();

                    updateLatestMessage();

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void addMessageToConversation() {
        String documentName = Calendar.getInstance().getTime().toString()
                .replace(":", "")
                .replace(" ", "")
                .replace("+", "");
        firebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("Chat").document(emailFriendToChat)
                .collection("Chat").document(documentName)
                .set(new Message(emailCurrentUser, emailFriendToChat, edtTypeMessage.getText().toString(),emailFriendToChat, Calendar.getInstance().getTime()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edtTypeMessage.setText("");
                    }
                });
        firebaseFirestore.collection("Users").document(emailFriendToChat)
                .collection("Chat").document(emailCurrentUser)
                .collection("Chat").document(documentName)
                .set(new Message(emailCurrentUser, emailFriendToChat, edtTypeMessage.getText().toString(),emailCurrentUser, Calendar.getInstance().getTime()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edtTypeMessage.setText("");
                    }
                });
    }

    public void updateLatestMessage() {
        firebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("LatestMessage").document(emailFriendToChat)
                .set(new Message(emailCurrentUser, emailFriendToChat, edtTypeMessage.getText().toString(),emailFriendToChat, Calendar.getInstance().getTime()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        firebaseFirestore.collection("Users").document(emailFriendToChat)
                .collection("LatestMessage").document(emailCurrentUser)
                .set(new Message(emailCurrentUser, emailFriendToChat, edtTypeMessage.getText().toString(),emailCurrentUser, Calendar.getInstance().getTime()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    public void loadMessageForFirstTime() {
        listenerRegistration=firebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("Chat").document(emailFriendToChat)
                .collection("Chat")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                if (isFirst) {
                                    messageArrayList.add(0, (Message) doc.getDocument().toObject(Message.class));
                                    adapter.notifyDataSetChanged();
                                } else {
                                    messageArrayList.add((Message) doc.getDocument().toObject(Message.class));
                                    adapter.notifyDataSetChanged();
                                }

                            } else if (doc.getType() == DocumentChange.Type.MODIFIED) {
                                messageArrayList.remove(messageArrayList.size() - 1);
                                messageArrayList.add((Message) doc.getDocument().toObject(Message.class));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        isFirst = false;

                        setSeenLatestMessage();
                    }
                });
    }

    private void initWidget() {

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view_message);
        edtTypeMessage = (EditText) findViewById(R.id.edt_type_message);
        ibtnSendMessage = (ImageButton) findViewById(R.id.ibtn_send_message);
        toolbar=(Toolbar) findViewById(R.id.tool_bar);
    }
    public void setSeenLatestMessage(){
        firebaseFirestore.collection("Users").document(emailFriendToChat)
                .collection("LatestMessage").document(emailCurrentUser)
                .update("seen",true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    //@Override
//    protected void onResume() {
////        super.onResume();
////        firebaseFirestore.collection("Users").document(emailCurrentUser)
////                .get()
////                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                        List<String> friendList=new ArrayList<>();
////                        friendList.addAll((Collection<? extends String>) task.getResult().get("friendList"));
////                        for (int i=0;i<friendList.size();i++)
////                        {
////                            firebaseFirestore.collection("Users").document(friendList.get(i))
////                                    .collection("Online").document(emailCurrentUser)
////                                    .set(new Name(emailCurrentUser)).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////                                }
////                            });
////                        }
////                    }
////                });
////        isResume=true;
////        firebaseFirestore.collection("Users").document(emailFriendToChat)
////                .collection("Chat").document(emailCurrentUser)
////                .collection("Chat")
////                .orderBy("date", Query.Direction.DESCENDING).limit(1)
////                .addSnapshotListener(new EventListener<QuerySnapshot>() {
////                    @Override
////                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
////                        if(isResume){
////                            for (DocumentChange doc : value.getDocumentChanges()) {
////                                if (doc.getType() == DocumentChange.Type.ADDED) {
////                                    Message message = doc.getDocument().toObject(Message.class);
////                                    message.setSeen(true);
////                                    Log.d("AAA","emailFriendToChat "+emailFriendToChat);
////                                    Log.d("AAA","emailCurrentUser "+emailCurrentUser);
////                                    Log.d("AAA","message "+message.getMessage());
////                                    Log.d("AAA","time "+message.getDate());
////                                    Log.d("AAA","resume");
////                                    Log.d("AAA",isResume+"");
////                                    firebaseFirestore.collection("Users").document(emailFriendToChat)
////                                            .collection("Chat").document(emailCurrentUser)
////                                            .collection("Chat").document(message.getDate().toString()
////                                            .replace(":", "")
////                                            .replace(" ", "")
////                                            .replace("+", ""))
////                                            .set(message);
////                                }
////                            }
////                        }
////
////                    }});
////
////
////    }

//    @Override
//    public void onBackPressed() {
//        Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();
//        isResume = false;
//        finish();
//        super.onBackPressed();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        firebaseFirestore.collection("Users").document(emailCurrentUser)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        List<String> friendList = new ArrayList<>();
//                        friendList.addAll((Collection<? extends String>) task.getResult().get("friendList"));
//                        for (int i = 0; i < friendList.size(); i++) {
//                            firebaseFirestore.collection("Users").document(friendList.get(i))
//                                    .collection("Online").document(emailCurrentUser)
//                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                }
//                            });
//                        }
//                    }
//                });
//    }
}