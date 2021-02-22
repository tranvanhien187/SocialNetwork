package com.example.fakebook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebook.R;
import com.example.fakebook.adapter.FriendOnlineAdapter;
import com.example.fakebook.adapter.LatestMessageAdapter;
import com.example.fakebook.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    private RecyclerView recyclerViewLatestMessage;
    private LatestMessageAdapter latestMessageAdapter;
    private ArrayList<Message> arrayLatest;

    private RecyclerView recyclerViewFriendOnline;
    private ArrayList<String> arrayFriendOnline;
    private FriendOnlineAdapter friendOnlineAdapter;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    String emailTmp = mAuth.getCurrentUser().getEmail();
    String emailCurrentUser = emailTmp.substring(0, emailTmp.length() - "@gmail.com".length());
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        initWidget();

        arrayLatest =new ArrayList<>();
        latestMessageAdapter =new LatestMessageAdapter(arrayLatest,getContext(), emailCurrentUser);
        recyclerViewLatestMessage.setAdapter(latestMessageAdapter);
        recyclerViewLatestMessage.setLayoutManager(new LinearLayoutManager(getContext()));


        arrayFriendOnline=new ArrayList<>();
        friendOnlineAdapter = new FriendOnlineAdapter(arrayFriendOnline,getContext());
        recyclerViewFriendOnline.setAdapter(friendOnlineAdapter);
        recyclerViewFriendOnline.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("LatestMessage")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange doc:value.getDocumentChanges()){
                            if(doc.getType()==DocumentChange.Type.ADDED){
                                arrayLatest.add(0,doc.getDocument().toObject(Message.class));
                                latestMessageAdapter.notifyDataSetChanged();
                            }
                            else if (doc.getType()==DocumentChange.Type.MODIFIED){
                                for (int i=0;i<arrayLatest.size();i++) {
                                    if(doc.getDocument().toObject(Message.class).getEmailFriend().equals(arrayLatest.get(i).getEmailFriend())){
                                        arrayLatest.remove(i);
                                        arrayLatest.add(0,doc.getDocument().toObject(Message.class));
                                        latestMessageAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
//        firebaseFirestore.collection("Users").document(emailCurrentUser)
//                .collection("Online")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        for(DocumentChange doc:value.getDocumentChanges()) {
//                            if (doc.getType() == DocumentChange.Type.ADDED) {
//                                arrayFriendOnline.add(doc.getDocument().toObject(Name.class).getName());
//                                friendOnlineAdapter.notifyDataSetChanged();
//                            }
//                            else if(doc.getType()==DocumentChange.Type.REMOVED){
//                                arrayFriendOnline.remove(doc.getDocument().toObject(Name.class).getName());
//                                friendOnlineAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                });




    }
    public void initWidget()
    {
        recyclerViewLatestMessage =(RecyclerView) view.findViewById(R.id.recycle_view_latest_message);
        recyclerViewFriendOnline=(RecyclerView) view.findViewById(R.id.recycle_view_friend_online);
    }
}