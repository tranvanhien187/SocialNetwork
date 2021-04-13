package com.example.fakebook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.adapter.FriendAdapter;
import com.example.fakebook.adapter.PostAdapter;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.FriendRequest;
import com.example.fakebook.model.Message;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.Post;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalPageActivity extends AppCompatActivity {
    private TextView txtName,txtBirthDate,txtSex,txtEmail,txtCity,txtEducation,txtRelationship;
    private Button btnAddFriend,btnCancelAddFriend,btnFriend,btnAcceptFriend;
    private ImageButton ibtnMessage,ibtnInfor;
    private ImageView imgBack;
    private CircleImageView imgAvatar;
    private LinearLayout linearInfor;
    private RecyclerView recyclerViewMyPost,recyclerViewFriend;
    private PostAdapter mMyPostAdapter;
    private FriendAdapter mFriendAdapter;
    private ArrayList<AddressPostNewFeed> mAddressMyPostList;
    private ArrayList<String> mFriendList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String  emailPage;
    private String emailCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        Intent intent=getIntent();
        emailPage=intent.getStringExtra("Email");
        mFirebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        emailCurrentUser=mAuth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());

        initWidget();

        setUpFriend();

        setUpPersonalInformation();

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFRequest();

                btnAddFriend.setVisibility(View.GONE);
                btnCancelAddFriend.setVisibility(View.VISIBLE);
            }
        });
        btnCancelAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelAddFriend();

                btnCancelAddFriend.setVisibility(View.GONE);
                btnAddFriend.setVisibility(View.VISIBLE);
            }
        });

        btnAcceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addFriendToFriendList();

                deleteFriendRequest();

                addNotification();

                addPostToFriendNewFeed();

                addLatestMessage();

                btnAcceptFriend.setVisibility(View.GONE);
                btnFriend.setVisibility(View.VISIBLE);

            }
        });

        ibtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent=new Intent(PersonalPageActivity.this,MessageActivity.class);
                messageIntent.putExtra("emailToChat",emailPage);
                startActivity(messageIntent);
            }
        });

        ibtnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearInfor.getVisibility()==View.VISIBLE){
                    linearInfor.setVisibility(View.GONE);
                }else {
                    linearInfor.setVisibility(View.VISIBLE);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void initWidget(){
        imgAvatar=(CircleImageView) findViewById(R.id.img_personal_page_avatar);
        txtName=(TextView) findViewById(R.id.txt_personal_page_name);
        txtBirthDate=(TextView) findViewById(R.id.txt_page_birth_day);
        txtEmail=(TextView) findViewById(R.id.txt_page_email);
        txtSex=(TextView) findViewById(R.id.txt_page_sex);
        txtCity=(TextView) findViewById(R.id.txt_page_city);
        txtEducation=(TextView) findViewById(R.id.txt_page_education);
        txtRelationship=(TextView) findViewById(R.id.txt_page_relationship);
        btnAddFriend=(Button) findViewById(R.id.btn_page_add_friend);
        btnCancelAddFriend=(Button) findViewById(R.id.btn_page_cancel_add_friend);
        btnFriend=(Button) findViewById(R.id.btn_page_friend);
        btnAcceptFriend=(Button) findViewById(R.id.btn_page_accept_friend);
        ibtnMessage=(ImageButton) findViewById(R.id.ibtn_page_message);
        ibtnInfor=(ImageButton)  findViewById(R.id.ibtn_page_infor);
        linearInfor=(LinearLayout) findViewById(R.id.linear_infor);
        recyclerViewMyPost=(RecyclerView) findViewById(R.id.recycle_view_personal_page_my_post);
        recyclerViewFriend=(RecyclerView) findViewById(R.id.recycle_view_personal_page_friend);
        imgBack=(ImageView) findViewById(R.id.img_back);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpFriend(){
        mFriendList=new ArrayList<>();
        mFriendAdapter=new FriendAdapter(mFriendList,PersonalPageActivity.this);
        recyclerViewFriend.setLayoutManager(new GridLayoutManager(PersonalPageActivity.this, 3));
        recyclerViewFriend.setAdapter(mFriendAdapter);
        mFirebaseFirestore.collection("Users").document(emailPage)
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

    public void setUpPersonalInformation(){
        mFirebaseFirestore.collection("Users").document(emailPage)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    User user=documentSnapshot.toObject(User.class);
                    txtName.setText(user.getName());
                    if(user.getMale())  txtSex.setText("Nam");
                    else txtSex.setText("Nữ");
                    txtBirthDate.setText(user.getDateOfBirth());
                    txtEmail.setText(user.getEmail());
                    txtEducation.setText(user.getEducation());
                    if(user.getIsMarriage())  txtRelationship.setText("Kết hôn");
                    else txtRelationship.setText("Độc thân");
                    txtCity.setText(user.getCity());
                    Glide.with(PersonalPageActivity.this)
                            .load(user.getAvatar())
                            .fitCenter()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(imgAvatar);

                    setUpMyPostList(user.getName(),user.getAvatar());

                    setUpRelationship();
                }
            }
        });
    }

    public void setUpMyPostList(String name,String avatar){
        mAddressMyPostList=new ArrayList<>();
        mMyPostAdapter=new PostAdapter(this,mAddressMyPostList,emailCurrentUser);
        recyclerViewMyPost.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyPost.setAdapter(mMyPostAdapter);
        recyclerViewMyPost.setNestedScrollingEnabled(false);
        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("MyPostAddress").orderBy("time", Query.Direction.DESCENDING).limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Toast.makeText(PersonalPageActivity.this, queryDocumentSnapshots.size()+"", Toast.LENGTH_SHORT).show();
                for (DocumentSnapshot i:queryDocumentSnapshots.getDocuments()) {
                    AddressPostNewFeed addressPostNewFeed=i.toObject(AddressPostNewFeed.class);
                    mAddressMyPostList.add(addressPostNewFeed);
                    mMyPostAdapter.notifyDataSetChanged();
                    Log.d("AAA",i.toString());
                }
            }
        });
    }

    public void sendFRequest(){
        FriendRequest friendRequest=new FriendRequest(emailCurrentUser, Calendar.getInstance().getTime());
        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("FriendRequest").document(emailCurrentUser)
                .set(friendRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA","Add Friend");

            }
        });
    }

    public void cancelAddFriend(){
        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("FriendRequest").document(emailCurrentUser)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA","Cancel Add Friend");
            }
        });
    }

    public void setUpRelationship(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    User user=documentSnapshot.toObject(User.class);
                    ArrayList<String> friendList=user.getFriendList();
                    if(friendList.contains(emailPage)){
                        btnAddFriend.setVisibility(View.GONE);
                        btnFriend.setVisibility(View.VISIBLE);
                        btnCancelAddFriend.setVisibility(View.GONE);
                        btnAcceptFriend.setVisibility(View.GONE);
                        return;
                    }
                }
            }
        });

        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("FriendRequest").document(emailPage)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Log.d("AAA","Chấp nhận lời mời kết bạn");
                    btnAddFriend.setVisibility(View.GONE);
                    btnAcceptFriend.setVisibility(View.VISIBLE);
                    btnCancelAddFriend.setVisibility(View.GONE);
                    btnFriend.setVisibility(View.GONE);
                    return;
                }
            }
        });

        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("FriendRequest").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    Log.d("AAA","Đã gửi lời mời kết bạn");
                    btnAddFriend.setVisibility(View.GONE);
                    btnCancelAddFriend.setVisibility(View.VISIBLE);
                    btnFriend.setVisibility(View.GONE);
                    btnAcceptFriend.setVisibility(View.GONE);
                    return;
                }
            }
        });

    }

    public void addNotification(){
        Date time= Calendar.getInstance().getTime();
        Notification notification=new Notification(emailPage," đã trở thành bạn bè với bạn","",time);
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("Notification").document(emailCurrentUser+"FRIEND"+emailPage)
                .set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA AddNotification",emailPage+" đã trở thành bạn bè với bạn");
            }
        });

        notification=new Notification(emailCurrentUser," đã trở thành bạn bè với bạn","",time);
        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("Notification").document(emailPage+"FRIEND"+emailCurrentUser)
                .set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA AddNotification",emailCurrentUser+" đã trở thành bạn bè với bạn");
            }
        });

    }

    public void deleteFriendRequest(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("FriendRequest").document(emailPage)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA DeleteFRequest","Đã xoá lời mời kết bạn");
            }
        });
    }

    public void addFriendToFriendList(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                ArrayList<String> friendList=user.getFriendList();
                friendList.add(emailPage);
                mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                        .update("friendList",friendList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA AddFriend",emailCurrentUser +" đã trở thành bạn bè với "+emailPage);
                    }
                });
            }
        });
        mFirebaseFirestore.collection("Users").document(emailPage)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                ArrayList<String> friendList=user.getFriendList();
                friendList.add(emailCurrentUser);
                mFirebaseFirestore.collection("Users").document(emailPage)
                        .update("friendList",friendList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA AddFriend",emailPage +" đã trở thành bạn bè với "+emailCurrentUser);
                    }
                });
            }
        });
    }

    private void addLatestMessage() {
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("LatestMessage").document(emailPage)
                .set(new Message());
        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("LatestMessage").document(emailCurrentUser)
                .set(new Message());

    }
    public void addPostToFriendNewFeed(){
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .collection("MyPosts")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for ( DocumentSnapshot snapshot:queryDocumentSnapshots){
                    final Post post=snapshot.toObject(Post.class);
                    AddressPostNewFeed addressPostNewFeed=new AddressPostNewFeed(post.getEmailUser(),post.getFilePath(),post.getTime());
                    mFirebaseFirestore.collection("Users").document(emailPage)
                            .collection("NewFeed").document(post.getFilePath())
                            .set(addressPostNewFeed).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AAA FriendNewFeed",emailPage+"---"+post.getFilePath());
                        }
                    });
                }
            }
        });


        mFirebaseFirestore.collection("Users").document(emailPage)
                .collection("MyPosts")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for ( DocumentSnapshot snapshot:queryDocumentSnapshots){
                    final Post post=snapshot.toObject(Post.class);
                    AddressPostNewFeed addressPostNewFeed=new AddressPostNewFeed(post.getEmailUser(),post.getFilePath(),post.getTime());
                    mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                            .collection("NewFeed").document(post.getFilePath())
                            .set(addressPostNewFeed).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AAA FriendNewFeed",emailCurrentUser+"---"+post.getFilePath());
                        }
                    });
                }
            }
        });

    }
}