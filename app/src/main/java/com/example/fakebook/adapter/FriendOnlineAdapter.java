package com.example.fakebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.activity.MessageActivity;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendOnlineAdapter extends RecyclerView.Adapter<FriendOnlineAdapter.MyViewHolder> {
    ArrayList<String> list;
    Context context;

    public FriendOnlineAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendOnlineAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_item_user_online, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendOnlineAdapter.MyViewHolder holder, int position) {
        holder.binding(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        CircleImageView imgAvatar,imgOnline;
        TextView txtName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void binding(final String email) {
            imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar_user_online);
            txtName = (TextView) view.findViewById(R.id.txt_name_user_online);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users")
                    .document(email)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        txtName.setText(user.getName());
                        Glide.with(context)
                                .load(user.getAvatar())
                                .fitCenter()
                                .into(imgAvatar);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context, MessageActivity.class);
                    profileIntent.putExtra("email", email);
                    context.startActivity(profileIntent);
                }
            });
        }
    }
}
