package com.example.fakebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    final int MSG_TYPE_LEFT=0;
    final int MSG_TYPE_RIGHT=1;
    ArrayList<Message> arrayList;
    Context context;
    String emailCurrentUser;
    String linkAvatar="";
    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    String emailFriendToChat;

    private ListenerRegistration listenerRegistration;


    public MessageAdapter(ArrayList<Message> arrayList, Context context, String emailCurrentUser, String emailFriendToChat) {
        this.arrayList = arrayList;
        this.context = context;
        this.emailCurrentUser = emailCurrentUser;
        this.emailFriendToChat=emailFriendToChat;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_item_chat_left,parent,false);
            return new MyViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_item_chat_right,parent,false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(arrayList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView txtMessage,txtStatusMessage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_avatar);
            txtMessage=(TextView) itemView.findViewById(R.id.txt_chat_item);
            txtStatusMessage=(TextView) itemView.findViewById(R.id.txt_status_message);
        }
        public void binding(Message message, int position)
        {
            txtMessage.setText(message.getMessage());
            firebaseFirestore.collection("Users").document(emailFriendToChat)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    linkAvatar=task.getResult().get("avatar")+"";
                    Glide.with(context)
                            .load(linkAvatar)
                            .fitCenter()
                            .into(imgAvatar);
                }
            });
            if(position==arrayList.size()-1&&message.getEmailReceiver().equals(emailFriendToChat)){

                listenerRegistration=firebaseFirestore.collection("Users").document(emailCurrentUser)
                        .collection("LatestMessage").document(emailFriendToChat)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value.exists()){
                                    Message message1=value.toObject(Message.class);
                                    if(value.toObject(Message.class).isSeen()){
                                        txtStatusMessage.setText("Đã xem");
                                    }
                                    else {
                                        txtStatusMessage.setText("Đã gửi");
                                    }
                                    Log.d("AAA",message1.getMessage());
                                }
                            }
                        });
//                if(message.isSeen()) {
//                    txtStatusMessage.setVisibility(View.VISIBLE);
//                    txtStatusMessage.setText("Đã xem");
//                }
//                else {
//                    txtStatusMessage.setText("Đã gửi");
//                }
            }
            else {
                txtStatusMessage.setVisibility(View.GONE);
            }

            //if(listenerRegistration!=null)  listenerRegistration.remove();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position).getEmailSender().equals(emailCurrentUser))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
