package com.example.fakebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.FriendRequest;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.Post;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FRequestAdapter extends RecyclerView.Adapter<FRequestAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<FriendRequest> mFRequestList;
    private String emailCurrentUser;

    public FRequestAdapter(Context mContext, ArrayList<FriendRequest> mFRequestList, String emailCurrentUser) {
        this.mContext = mContext;
        this.mFRequestList = mFRequestList;
        this.emailCurrentUser = emailCurrentUser;
    }


    public void remove(FriendRequest object)
    {
        for (int i=0; i<mFRequestList.size();i++) {
            if(mFRequestList.get(i).getEmail().equals(object.getEmail())){
                mFRequestList.remove(i);
            }
        }
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_friend_request,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(mFRequestList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFRequestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView txtName,txtTime;
        private Button btnYes,btnNo;
        private FirebaseFirestore mFirebaseFirestore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_row_item_friend_request_avatar);
            txtName=(TextView) itemView.findViewById(R.id.txt_row_item_friend_request_name);
            txtTime=(TextView) itemView.findViewById(R.id.txt_row_item_friend_request_time);
            btnNo=(Button) itemView.findViewById(R.id.btn_row_item_friend_request_no);
            btnYes=(Button) itemView.findViewById(R.id.btn_row_item_friend_request_yes);
            mFirebaseFirestore=FirebaseFirestore.getInstance();
        }
        public void binding(final FriendRequest friendRequest){
            mFirebaseFirestore.collection("Users").document(friendRequest.getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user=documentSnapshot.toObject(User.class);
                    txtName.setText(user.getName());
                    txtTime.setText(friendRequest.getTime().toString().replace("GMT+07:00",""));
                    Glide.with(mContext)
                            .load(user.getAvatar())
                            .into(imgAvatar);
                }
            });

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addFriendToFriendList(friendRequest.getEmail());

                    deleteFriendRequest(friendRequest.getEmail());

                    addNotification(friendRequest.getEmail());

                    addPostToFriendNewFeed(friendRequest.getEmail());

                    remove(friendRequest);
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteFriendRequest(friendRequest.getEmail());

                    remove(friendRequest);
                }
            });
        }
        public void addNotification(final String emailFRequest){
            Date time= Calendar.getInstance().getTime();
            Notification notification=new Notification(emailFRequest," đã trở thành bạn bè với bạn","",time);
            mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                    .collection("Notification").document(emailCurrentUser+"FRIEND"+emailFRequest)
                    .set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA AddNotification",emailFRequest+" đã trở thành bạn bè với bạn");
                }
            });

            notification=new Notification(emailCurrentUser," đã trở thành bạn bè với bạn","",time);
            mFirebaseFirestore.collection("Users").document(emailFRequest)
                    .collection("Notification").document(emailFRequest+"FRIEND"+emailCurrentUser)
                    .set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA AddNotification",emailCurrentUser+" đã trở thành bạn bè với bạn");
                }
            });

        }
        public void deleteFriendRequest(String emailFRequest){
            mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                    .collection("FriendRequest").document(emailFRequest)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA DeleteFRequest","Đã xoá lời mời kết bạn");
                }
            });
        }
        public void addFriendToFriendList(final String emailFRequest){
            mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user=documentSnapshot.toObject(User.class);
                    ArrayList<String> friendList=user.getFriendList();
                    friendList.add(emailFRequest);
                    mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                            .update("friendList",friendList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AAA AddFriend",emailCurrentUser +" đã trở thành bạn bè với "+emailFRequest);
                        }
                    });
                }
            });
            mFirebaseFirestore.collection("Users").document(emailFRequest)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user=documentSnapshot.toObject(User.class);
                    ArrayList<String> friendList=user.getFriendList();
                    friendList.add(emailCurrentUser);
                    mFirebaseFirestore.collection("Users").document(emailFRequest)
                            .update("friendList",friendList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("AAA AddFriend",emailFRequest +" đã trở thành bạn bè với "+emailCurrentUser);
                        }
                    });
                }
            });
        }

        public void addPostToFriendNewFeed(final String emailFRequest){
            mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                    .collection("MyPosts")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for ( DocumentSnapshot snapshot:queryDocumentSnapshots){
                        final Post post=snapshot.toObject(Post.class);
                        AddressPostNewFeed addressPostNewFeed=new AddressPostNewFeed(post.getEmailUser(),post.getFilePath(),post.getTime());
                        mFirebaseFirestore.collection("Users").document(emailFRequest)
                                .collection("NewFeed").document(post.getFilePath())
                                .set(addressPostNewFeed).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AAA FriendNewFeed",emailFRequest+"---"+post.getFilePath());
                            }
                        });
                    }
                }
            });


            mFirebaseFirestore.collection("Users").document(emailFRequest)
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
}
