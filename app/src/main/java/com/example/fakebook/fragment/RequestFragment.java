package com.example.fakebook.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fakebook.R;
import com.example.fakebook.adapter.FRequestAdapter;
import com.example.fakebook.model.FriendRequest;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class RequestFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String emailCurrentUser;
    private RecyclerView recyclerFRequest;
    private FRequestAdapter mAdapter;
    private ArrayList<FriendRequest> mFRequestList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth=FirebaseAuth.getInstance();
        emailCurrentUser=mAuth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());
        mFirebaseFirestore=FirebaseFirestore.getInstance();

        initWidget(view);

        setUpFRequestList();
    }

    public void initWidget(View view){
        recyclerFRequest=(RecyclerView) view.findViewById(R.id.recycle_vieư_friend_request);
    }

    public void setUpFRequestList(){
        mFRequestList=new ArrayList<>();
        mAdapter=new FRequestAdapter(getContext(),mFRequestList,emailCurrentUser);
        recyclerFRequest.setAdapter(mAdapter);
        recyclerFRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("FriendRequest").orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,@Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            switch (dc.getType()) {
                                case ADDED:
                                    mFRequestList.add(dc.getDocument().toObject(FriendRequest.class));
                                    mAdapter.notifyDataSetChanged();

                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    FriendRequest friendRequest=dc.getDocument().toObject(FriendRequest.class);
                                    mAdapter.remove(friendRequest);
                                    break;
                            }
                        }

                    }
                });
    }
}