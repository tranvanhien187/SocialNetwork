package com.example.fakebook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.activity.NewPostActivity;
import com.example.fakebook.activity.SettingActivity;
import com.example.fakebook.adapter.FriendAdapter;
import com.example.fakebook.adapter.PostAdapter;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalityFragment extends Fragment {

    private TextView txtName;
    private CircleImageView imgAvatar;
    private RecyclerView recyclerViewMyPost,recyclerViewFriend;
    private Button btnAddNewPost;
    private ImageButton btnSetting;
    private PostAdapter mMyPostAdapter;
    private FriendAdapter mFriendAdapter;
    private ArrayList<String> mFriendList;
    private ArrayList<AddressPostNewFeed> mAddressMyPostList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String  emailCurrentUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personality, container, false);
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

        initWidget(view);

        setUpPersonalInformation();

        setUpFriend();

        btnAddNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent=new Intent(getContext(), NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent=new Intent(getContext(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });

    }

    public void initWidget(View view){
        txtName=(TextView) view.findViewById(R.id.txt_personality_name);
        imgAvatar=(CircleImageView) view.findViewById(R.id.img_personality_avatar);
        btnAddNewPost=(Button) view.findViewById(R.id.btn_page_add_post);
        btnSetting=(ImageButton) view.findViewById(R.id.ibtn_setting);
        recyclerViewMyPost=(RecyclerView) view.findViewById(R.id.recycle_view_my_post);
        recyclerViewFriend=(RecyclerView) view.findViewById(R.id.recycle_view_friend);
    }
    public void setUpPersonalInformation(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    User user=documentSnapshot.toObject(User.class);
                    txtName.setText(user.getName());
                    Glide.with(getContext())
                            .load(user.getAvatar())
                            .centerCrop()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(imgAvatar);

                    setUpMyPostList(user.getName(),user.getAvatar());
                }
            }
        });
    }

    public void setUpFriend(){
        mFriendList=new ArrayList<>();
        mFriendAdapter=new FriendAdapter(mFriendList,getContext());
        recyclerViewFriend.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewFriend.setAdapter(mFriendAdapter);
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    User user=documentSnapshot.toObject(User.class);
                    mFriendList.addAll(user.getFriendList().subList(0,6<user.getFriendList().size() ? 6 :user.getFriendList().size()));
                    mFriendAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    public void setUpMyPostList(String name,String avatar){
        mAddressMyPostList=new ArrayList<>();
        mMyPostAdapter=new PostAdapter(getContext(),mAddressMyPostList,emailCurrentUser);
        recyclerViewMyPost.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMyPost.setAdapter(mMyPostAdapter);
        recyclerViewMyPost.setNestedScrollingEnabled(false);
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("MyPostAddress").orderBy("time", Query.Direction.DESCENDING).limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot i:queryDocumentSnapshots.getDocuments()) {
                    AddressPostNewFeed addressPostNewFeed=i.toObject(AddressPostNewFeed.class);
                    mAddressMyPostList.add(addressPostNewFeed);
                    mMyPostAdapter.notifyDataSetChanged();
                    Log.d("AAA",i.toString());
                }
            }
        });
    }
}