package com.example.fakebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.activity.PersonalPageActivity;
import com.example.fakebook.model.FriendRequest;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<User> userList;
    private String emailCurrentUser;


    public SearchAdapter(Context mContext, ArrayList<User> userList, String emailCurrentUser) {
        this.mContext = mContext;
        this.userList = userList;
        this.emailCurrentUser = emailCurrentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_search_user,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgAvatar;
        private TextView txtName;
        private ImageView imgAddFriend,imgSendMessage,imgCancelAddFriend;

        private LinearLayout linearClick;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_row_item_search_avatar);
            txtName=(TextView) itemView.findViewById(R.id.txt_row_item_search_name);
            imgAddFriend=(ImageView) itemView.findViewById(R.id.img_row_item_search_status_add_friend);
            imgSendMessage=(ImageView) itemView.findViewById(R.id.img_row_item_search_status_send_message);
            imgCancelAddFriend=(ImageView) itemView.findViewById(R.id.img_row_item_search_status_cancel_add_friend); 
            linearClick=(LinearLayout) itemView.findViewById(R.id.linear_row_item_search);
        }
        public void binding(final User user){
            Glide.with(mContext)
                    .load(user.getAvatar())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(imgAvatar);
            txtName.setText(user.getName());

            if(user.getFriendList().contains(emailCurrentUser)) {
                imgAddFriend.setVisibility(View.GONE);
                imgSendMessage.setVisibility(View.VISIBLE);
            }else {
                imgAddFriend.setVisibility(View.VISIBLE);
                imgSendMessage.setVisibility(View.GONE);
            }

            linearClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent=new Intent(mContext, PersonalPageActivity.class);
                    profileIntent.putExtra("Email",user.getEmail());
                    mContext.startActivity(profileIntent);
                }
            });
            
            imgAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFRequest(user.getEmail());
                    
                    imgAddFriend.setVisibility(View.GONE);
                    
                    imgCancelAddFriend.setVisibility(View.VISIBLE);
                    
                }
            });
            
            imgCancelAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelAddFriend(user.getEmail());
                    
                    imgCancelAddFriend.setVisibility(View.GONE);
                    
                    imgAddFriend.setVisibility(View.VISIBLE);
                    
                }
            });
            
            imgSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Message", Toast.LENGTH_SHORT).show();
                }
            });

        }
        public void sendFRequest(String emailPage){
            FriendRequest friendRequest=new FriendRequest(emailCurrentUser, Calendar.getInstance().getTime());
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(emailPage)
                    .collection("FriendRequest").document(emailCurrentUser)
                    .set(friendRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA","Add Friend");

                }
            });
        }

        public void cancelAddFriend(String emailPage){
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(emailPage)
                    .collection("FriendRequest").document(emailCurrentUser)
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA","Cancel Add Friend");
                }
            });
        }

    }
}
