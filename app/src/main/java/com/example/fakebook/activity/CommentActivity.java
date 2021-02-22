package com.example.fakebook.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fakebook.R;
import com.example.fakebook.adapter.CommentAdapter;
import com.example.fakebook.model.Comment;
import com.example.fakebook.model.Notification;
import com.example.fakebook.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    private String filePath,emailPost;
    private RecyclerView recyclerViewComment;
    private CommentAdapter mAdapter;
    private ArrayList<Comment> mCommentList;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;
    private String emailCurrentUser;
    private EditText edtComment;
    private ImageView imgSend;
    private TextView txtCountLike;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        emailPost = intent.getStringExtra("emailPost");
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        emailCurrentUser = mAuth.getCurrentUser().getEmail();
        emailCurrentUser = emailCurrentUser.substring(0, emailCurrentUser.length() - "@gmail.com".length());

        initWidget();

        setUpCommentList();

        setCountLike();

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendComment();
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void initWidget(){
        edtComment=(EditText) findViewById(R.id.edt_comment);
        imgSend=(ImageView) findViewById(R.id.img_send_comment);
        txtCountLike=(TextView) findViewById(R.id.txt_count_like);
        toolbar=(Toolbar) findViewById(R.id.tool_bar);
        recyclerViewComment=(RecyclerView) findViewById(R.id.recycle_view_comment);
    }

    public void setUpCommentList(){
        mCommentList=new ArrayList<>();
        mAdapter=new CommentAdapter(this,mCommentList);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComment.setAdapter(mAdapter);

        mFirebaseFirestore.collection("Users").document(emailPost)
                .collection("Comment").document(filePath)
                .collection("Comment").orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange doc:value.getDocumentChanges()){
                            switch (doc.getType()){
                                case ADDED:
                                    mCommentList.add(doc.getDocument().toObject(Comment.class));
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }

                });
    }


    public void handleSendComment(){
        if(edtComment.getText().toString().equals("")){
            Toast.makeText(CommentActivity.this, "Bình luận không được để trống", Toast.LENGTH_SHORT).show();
        }else {
            Date time=Calendar.getInstance().getTime();
            String filePath1=edtComment.getText().toString()+time.toString()
                    .replace(":","")
                    .replace(" ","")
                    .replace("+","");;


            sendComment(filePath1,time);
        }
    }
    public void sendComment(String filePath1, final Date time){
        mFirebaseFirestore.collection("Users").document(emailPost)
                .collection("Comment").document(filePath)
                .collection("Comment").document(filePath1)
                .set(new Comment(edtComment.getText().toString(),time,emailCurrentUser)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA SendComment",edtComment.getText().toString()) ;
                edtComment.setText("");

                addNotification(time);
            }
        });
    }

    public void setCountLike(){
        mFirebaseFirestore.collection("Users").document(emailPost)
                .collection("MyPosts").document(filePath)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    txtCountLike.setText(documentSnapshot.toObject(Post.class).getListLike().size()+"");
                }
            }
        });
    }

    public void addNotification(Date time){
        mFirebaseFirestore.collection("Users").document(emailPost)
                .collection("Notification").document(filePath+"COMMENT")
                .set(new Notification(emailCurrentUser," đã bình luận về bài viết của bạn",filePath+"COMMENT",time))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA  Notification",filePath+" Comment");
                    }
                });
    }
}