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
import com.example.fakebook.model.Message;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LatestMessageAdapter extends RecyclerView.Adapter<LatestMessageAdapter.MyViewHolder> {
    ArrayList<Message> list;
    Context context;
    String emailCurrentUser;

    public LatestMessageAdapter(ArrayList<Message> list, Context context, String emailCurrentUser) {
        this.list = list;
        this.context = context;
        this.emailCurrentUser=emailCurrentUser;
    }

    @NonNull
    @Override
    public LatestMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.row_item_latest_message,parent,false);
        MyViewHolder vh=new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        CircleImageView imgAvatar;
        TextView txtName,txtTimeLatestMessage,txtLatestMessage;
        String email;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            imgAvatar=(CircleImageView) view.findViewById(R.id.img_avatar_friend_to_chat);
            txtName=(TextView) view.findViewById(R.id.txt_name_friend_to_chat);
            txtLatestMessage=(TextView) view.findViewById(R.id.txt_latest_message);
            txtTimeLatestMessage=(TextView) view.findViewById(R.id.txt_time_latest_message);
        }
        public void binding(final Message message)
        {
                    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
                    if(message.getEmailSender().equals(emailCurrentUser))
                    {
                        email=message.getEmailReceiver();
                        txtLatestMessage.setText("Bạn: "+message.getMessage());
                    }else {
                        email=message.getEmailSender();
                        txtLatestMessage.setText(message.getMessage());

                    }
                    firebaseFirestore.collection("Users")
                            .document(email)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user=task.getResult().toObject(User.class);
                            if(user!=null)
                            {
                                txtName.setText(user.getName());
                                Glide.with(context)
                                        .load(user.getAvatar())
                                        .fitCenter()
                                        .into(imgAvatar);
                            }
                        }
                    });

                    txtTimeLatestMessage.setText(message.getDate().getHours()+":"+message.getDate().getMinutes());
                    
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(context, MessageActivity.class);
                    profileIntent.putExtra("emailToChat",email);
                    context.startActivity(profileIntent);
                }
            });
        }
    }
}
