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
import com.example.fakebook.model.Comment;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Comment> mCommentList;

    public CommentAdapter(Context mContext, ArrayList<Comment> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_item_comment,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding(mCommentList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{
        private CircleImageView imgAvatar;
        private TextView txtName,txtComment;
        private FirebaseFirestore firebaseFirestore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_row_item_post_comment_avatar);
            txtComment=(TextView) itemView.findViewById(R.id.txt_row_item_post_comment);
            txtName=(TextView) itemView.findViewById(R.id.txt_row_item_post_comment_name);
            firebaseFirestore=FirebaseFirestore.getInstance();
        }

        public void binding(final Comment comment){
            firebaseFirestore.collection("Users").document(comment.getEmailUser())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        User user=documentSnapshot.toObject(User.class);
                        Glide.with(mContext)
                                .load(user.getAvatar())
                                .into(imgAvatar);
                        txtName.setText(user.getName());
                        txtComment.setText(comment.getContent());
                    }
                }
            });
        }
    }
}
