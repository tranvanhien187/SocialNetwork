package com.example.fakebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fakebook.R;
import com.example.fakebook.activity.CommentActivity;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.Comment;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.Post;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context context;
    ArrayList<AddressPostNewFeed> newFeedPostList;
    FirebaseFirestore firebaseFirestore;
    String emailCurrentUser;


    public PostAdapter(Context context, ArrayList<AddressPostNewFeed> postList,String emailCurrentUser) {
        this.context = context;
        this.newFeedPostList = postList;
        firebaseFirestore=FirebaseFirestore.getInstance();
        this.emailCurrentUser=emailCurrentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_post,parent,false);
        return new PostAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding((newFeedPostList.get(position)));
    }

    @Override
    public int getItemCount() {
        return newFeedPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgAvatar,imgAvatarComment;
        private ImageView imgPost,imgLike,imgUnlike;
        private TextView txtName,txtTime,txtContent,txtCountLike,txtNameComment,txtComment;
        private ProgressBar progressBarAvatar,progressBarImage,progressBarCommentAvatar;
        private LinearLayout linearLike,linearCommnet,linearReadComment;
        AddressPostNewFeed addressPostNewFeed;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar=(CircleImageView) itemView.findViewById(R.id.img_row_item_post_avatar);
            imgPost=(ImageView) itemView.findViewById(R.id.img_row_item_post);
            txtName=(TextView) itemView.findViewById(R.id.txt_row_item_post_name);
            txtTime=(TextView) itemView.findViewById(R.id.txt_row_item_post_time);
            txtContent=(TextView) itemView.findViewById(R.id.txt_row_item_post_content);
            txtCountLike=(TextView) itemView.findViewById(R.id.txt_count_like);
            imgLike=(ImageView) itemView.findViewById(R.id.img_row_item_post_like);
            imgUnlike=(ImageView) itemView.findViewById(R.id.img_row_item_post_un_like);
            txtComment=(TextView) itemView.findViewById(R.id.txt_row_item_post_comment);
            txtNameComment=(TextView) itemView.findViewById(R.id.txt_row_item_post_comment_name);
            imgAvatarComment=(CircleImageView) itemView.findViewById(R.id.img_row_item_post_comment_avatar);
            linearLike=(LinearLayout) itemView.findViewById(R.id.linear_row_item_post_like);
            linearCommnet=(LinearLayout) itemView.findViewById(R.id.linear_row_item_post_comment);
            linearReadComment=(LinearLayout) itemView.findViewById(R.id.linear_row_item_post_read_comment);
            progressBarAvatar=(ProgressBar) itemView.findViewById(R.id.progressBar_avatar);
            progressBarImage=(ProgressBar) itemView.findViewById(R.id.progressBar_img);
            progressBarCommentAvatar=(ProgressBar) itemView.findViewById(R.id.progressBar_comment_avatar);

        }

        public void binding(final AddressPostNewFeed addressPostNewFeed){
            this.addressPostNewFeed=addressPostNewFeed;
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .collection("MyPosts").document(addressPostNewFeed.getFilePath())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){

                        Post post=documentSnapshot.toObject(Post.class);

                        setUpInformation();

                        setUpPost(post);

                        setUpReadComment();


                    }
                }
            });

            linearLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(imgLike.getVisibility()==View.VISIBLE){
                        handleLikeOrUnlike(true);
                        setCountLike();
                        imgLike.setVisibility(View.GONE);
                        imgUnlike.setVisibility(View.VISIBLE);
                    }else {
                        handleLikeOrUnlike(false);
                        setCountLike();
                        imgUnlike.setVisibility(View.GONE);
                        imgLike.setVisibility(View.VISIBLE);
                    }
                }
            });
            linearCommnet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent commentIntent=new Intent(context, CommentActivity.class);
                    commentIntent.putExtra("filePath",addressPostNewFeed.getFilePath());
                    commentIntent.putExtra("emailPost",addressPostNewFeed.getEmailPost());
                    context.startActivity(commentIntent);
                }
            });
        }
        public void setCountLike(){
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .collection("MyPosts").document(addressPostNewFeed.getFilePath())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        txtCountLike.setText(documentSnapshot.toObject(Post.class).getListLike().size()+"");
                    }
                }
            });
        }
        public void setUpInformation(){
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                        User user=documentSnapshot.toObject(User.class);
                        Glide.with(context)
                                .load(user.getAvatar())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBarAvatar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(imgAvatar);
                        txtName.setText(user.getName());
                    }
                }
            });
        }

        public void setUpPost(Post post){
            txtTime.setText(post.getTime().toString().replace("GMT+07:00",""));
            txtContent.setText(post.getContent());
            Glide.with(context)
                    .load(post.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBarImage.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgPost);

            setCountLike();
            ArrayList<String> listLike=post.getListLike();
            if(listLike.contains(emailCurrentUser)){
                imgLike.setVisibility(View.GONE);
                imgUnlike.setVisibility(View.VISIBLE);
            }else {
                imgUnlike.setVisibility(View.GONE);
                imgLike.setVisibility(View.VISIBLE);
            }
        }
        public void setUpReadComment(){
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .collection("Comment").document(addressPostNewFeed.getFilePath())
                    .collection("Comment").orderBy("time", Query.Direction.DESCENDING)
                    .limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size()!=0){
                        linearReadComment.setVisibility(View.VISIBLE);
                        final Comment comment=  queryDocumentSnapshots.getDocumentChanges().get(0).getDocument().toObject(Comment.class);
                        firebaseFirestore.collection("Users").document(comment.getEmailUser())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user=documentSnapshot.toObject(User.class);
                                txtNameComment.setText(user.getName());
                                txtComment.setText(comment.getContent());
                                Glide.with(context)
                                        .load(user.getAvatar())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBarCommentAvatar.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(imgAvatarComment);
                            }
                        });
                    }else {
                        linearReadComment.setVisibility(View.GONE);
                    }
                }
            });
        }
        public void handleLikeOrUnlike(final boolean isLike){
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .collection("MyPosts").document(addressPostNewFeed.getFilePath())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        final Post post=documentSnapshot.toObject(Post.class);
                        Date time= Calendar.getInstance().getTime();
                        int likeCount=post.getListLike().size();
                        if(isLike){

                            likeCount++;
                            post.addLikeUser(emailCurrentUser);
                            handleNotificationLikeOrUnlike(post,emailCurrentUser,time);
                            setCountLike();

                        }else {

                            likeCount--;
                            post.deleteLikeUser(emailCurrentUser);
                            if(post.getListLike().size()==0){
                                deleteNotificationlike(post);
                            }else {
                                handleNotificationLikeOrUnlike(post,post.getListLike().get(post.getListLike().size()-1),time);
                            }
                            setCountLike();
                        }

                        updateLikeOrUnlike(post);
                    }
                }
            });
        }
        public void deleteNotificationlike(final Post post){
            firebaseFirestore.collection("Users").document(post.getEmailUser())
                    .collection("Notification").document(post.getFilePath()+"LikeOrUnlike")
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA Delete Notification","Count like"+post.getListLike().size());
                }
            });
        }
        public void handleNotificationLikeOrUnlike(final Post post,String email, Date time){
            if(post.getListLike().size()==1){
                firebaseFirestore.collection("Users").document(post.getEmailUser())
                        .collection("Notification").document(post.getFilePath()+"LikeOrUnlike")
                        .set(new Notification(email," đã thích bài viết của bạn",post.getFilePath(),time))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AAA Update Notification","Count like"+post.getListLike().size());
                            }
                        });
            }else {
                firebaseFirestore.collection("Users").document(post.getEmailUser())
                        .collection("Notification").document(post.getFilePath()+"LikeOrUnlike")
                        .set(new Notification(email," và "+(post.getListLike().size()-1)+" đã thích bài viết của bạn",post.getFilePath(),time))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AAA Update Notification","Count like"+post.getListLike().size());
                            }
                        });
            }

        }
        public void updateLikeOrUnlike(final Post post){
            firebaseFirestore.collection("Users").document(addressPostNewFeed.getEmailPost())
                    .collection("MyPosts").document(addressPostNewFeed.getFilePath())
                    .set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAA Handle LOUL ",+post.getListLike().size()+"");
                }
            });
        }
    }
}
