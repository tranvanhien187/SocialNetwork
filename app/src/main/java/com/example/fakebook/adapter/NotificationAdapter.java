package com.example.fakebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.model.FriendRequest;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Notification> mNotificationList;

    public NotificationAdapter(Context mContext, ArrayList<Notification> mNotificationList) {
        this.mContext = mContext;
        this.mNotificationList = mNotificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_notification,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(mNotificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public void remove(Notification object)
    {
        for (int i=0; i<mNotificationList.size();i++) {
            if(mNotificationList.get(i).getFilePath().equals(object.getFilePath())){
                mNotificationList.remove(i);
                this.notifyDataSetChanged();
                return;
            }
        }
    }
    public void modify(Notification object){
        for (int i=0; i<mNotificationList.size();i++) {
            if(mNotificationList.get(i).getFilePath().equals(object.getFilePath())){
                mNotificationList.remove(i);
                mNotificationList.add(i,object);
            }
        }
        this.notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvatar;
        private TextView txtContent,txtTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_row_item_notification_avatar);
            txtContent=(TextView) itemView.findViewById(R.id.txt_row_item_notification_content);
            txtTime=(TextView) itemView.findViewById(R.id.txt_row_item_notification_time);
        }

        public void binding(final Notification notification){
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(notification.getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user=documentSnapshot.toObject(User.class);
                    Glide.with(mContext)
                            .load(user.getAvatar())
                            .placeholder(R.drawable.logo_sn)
                            .into(imgAvatar);

                    txtTime.setText(notification.getTime().toString().replace("GMT+07:00",""));
                    txtContent.setText(user.getName()+notification.getContent());
                }
            });
        }
    }
}
