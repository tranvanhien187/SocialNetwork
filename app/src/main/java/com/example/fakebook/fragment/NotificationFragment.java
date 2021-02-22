package com.example.fakebook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fakebook.R;
import com.example.fakebook.adapter.NotificationAdapter;
import com.example.fakebook.model.Notification;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class NotificationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String emailCurrentUser;
    private RecyclerView recyclerViewNotification;
    private ArrayList<Notification> mNotificationList;
    private NotificationAdapter mAdapter;

    private boolean isFirst;

    public static ListenerRegistration listenerRegistration=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(HomeFragment.listenerRegistration!=null){
            HomeFragment.listenerRegistration.remove();
        }

        mAuth=FirebaseAuth.getInstance();
        emailCurrentUser=mAuth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());
        mFirebaseFirestore=FirebaseFirestore.getInstance();


        isFirst=true;

        initWidget(view);


        setUpNotification();
    }

    public void initWidget(View view){
        recyclerViewNotification=(RecyclerView) view.findViewById(R.id.recycle_vieư_notification);
    }
    public void setUpNotification(){

        mNotificationList=new ArrayList<>();
        mNotificationList.clear();
        mAdapter=new NotificationAdapter(getContext(),mNotificationList);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNotification.setAdapter(mAdapter);


        listenerRegistration=mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("Notification").orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange documentChange:value.getDocumentChanges()){
                            switch (documentChange.getType()){
                                case ADDED:
                                    if(isFirst){
                                        mNotificationList.add(documentChange.getDocument().toObject(Notification.class));
                                        mAdapter.notifyDataSetChanged();
                                    }else {
                                        mNotificationList.add(0,documentChange.getDocument().toObject(Notification.class));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case REMOVED:
                                    mAdapter.remove(documentChange.getDocument().toObject(Notification.class));
                                    break;
                                case MODIFIED:
                                    mAdapter.modify(documentChange.getDocument().toObject(Notification.class));
                                    break;
                            }
                        }
                        isFirst=false;
                    }});
    }
}
