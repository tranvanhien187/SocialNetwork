package com.example.fakebook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fakebook.activity.NewPostActivity;
import com.example.fakebook.R;
import com.example.fakebook.adapter.PostAdapter;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.User;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    private TextView txtAddPost;
    private CircleImageView imgAvatar;
    private RecyclerView recyclerViewNewFeed;
    private ArrayList<AddressPostNewFeed> mAddressPostList;
    private PostAdapter mNewFeedAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String  emailCurrentUser;
    private boolean isFirst;

    public static ListenerRegistration listenerRegistration;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        removeListenNotification();

        mAuth=FirebaseAuth.getInstance();
        emailCurrentUser=mAuth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());
        mFirebaseFirestore=FirebaseFirestore.getInstance();
        isFirst=true;

        initWidget(view);

        setUpAvatar();

        setUpNewFeed();


        txtAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });

    }


    public void initWidget(View view){

        txtAddPost=(TextView) view.findViewById(R.id.txt_add_new_post);
        imgAvatar=(CircleImageView) view.findViewById(R.id.img_avatar);
        recyclerViewNewFeed=(RecyclerView) view.findViewById(R.id.recycle_view_new_feed);
    }
    public void setUpAvatar(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    User user=documentSnapshot.toObject(User.class);
                    Glide.with(getContext())
                            .load(user.getAvatar())
                            .fitCenter()
                            .into(imgAvatar);
                }
            }
        });
    }
    public void setUpNewFeed(){
        mAddressPostList=new ArrayList<>();
        mNewFeedAdapter=new PostAdapter(getContext(),mAddressPostList,emailCurrentUser);
        recyclerViewNewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNewFeed.setAdapter(mNewFeedAdapter);
        recyclerViewNewFeed.setNestedScrollingEnabled(false);
        listenerRegistration=mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("NewFeed").orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange documentChange:value.getDocumentChanges()){
                            switch (documentChange.getType()){
                                case ADDED:
                                    if(isFirst){
                                        AddressPostNewFeed addressPostNewFeed=documentChange.getDocument().toObject(AddressPostNewFeed.class);
                                        mAddressPostList.add(addressPostNewFeed);
                                        mNewFeedAdapter.notifyDataSetChanged();
                                    }else {
                                        AddressPostNewFeed addressPostNewFeed=documentChange.getDocument().toObject(AddressPostNewFeed.class);
                                        mAddressPostList.add(0,addressPostNewFeed);
                                        mNewFeedAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                case REMOVED:
                                    break;
                                case MODIFIED:
                                    break;
                            }
                        }
                        isFirst=false;
                    }
                });

    }

    public void removeListenNotification(){
        if(NotificationFragment.listenerRegistration!=null){
            NotificationFragment.listenerRegistration.remove();
        }
    }
}