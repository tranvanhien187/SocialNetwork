package com.example.fakebook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.model.AddressPostNewFeed;
import com.example.fakebook.model.Post;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostActivity extends AppCompatActivity {
    private CircleImageView imgAvatar;
    private ImageView imgPost;
    private TextView txtName;
    private EditText edtContent;
    private Button btnOk,btnAddImage;
    Uri mainimageURI = null;

    private FirebaseAuth mAth;
    private FirebaseFirestore mFireStore;
    private StorageReference mStorageReference;
    private String emailCurrentUser;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAth=FirebaseAuth.getInstance();
        mFireStore=FirebaseFirestore.getInstance();
        mStorageReference=FirebaseStorage.getInstance().getReference();
        emailCurrentUser=mAth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());


        initWidget();

        setUp();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(NewPostActivity.this);
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewPost();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainimageURI = result.getUri();
                imgPost.setImageURI(mainimageURI);
                imgPost.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void initWidget(){
        imgAvatar=(CircleImageView) findViewById(R.id.img_post_avatar);
        imgPost=(ImageView) findViewById(R.id.img_post);
        txtName=(TextView)findViewById(R.id.txt_post_name);
        edtContent=(EditText) findViewById(R.id.edt_post_content);
        btnOk=(Button) findViewById(R.id.btn_submit);
        toolbar=(Toolbar) findViewById(R.id.tool_bar);
        btnAddImage=(Button) findViewById(R.id.btn_post_add_img);
    }

    public void setUp(){
        mFireStore.collection("Users").document(emailCurrentUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task!=null){
                                User user=task.getResult().toObject(User.class);
                                txtName.setText(user.getName());
                                Glide.with(NewPostActivity.this)
                                        .load(user.getAvatar())
                                        .centerCrop()
                                        //.placeholder(R.drawable.loading_spinner)
                                        .into(imgAvatar);
                            }
                        }
                    }
                });
    }
    public void handleNewPost(){
        if(edtContent.getText().toString().equals("")||mainimageURI==null){
            Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show();
        }else {

            final Date time =Calendar.getInstance().getTime();
            final String fileName=emailCurrentUser+time.toString()
                    .replace(":","")
                    .replace(" ","")
                    .replace("+","");
            StorageReference filePath=mStorageReference.child("post").child(fileName+"jpg");
            filePath.putFile(mainimageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            addMyPost(uri,fileName,time);

                            addMyPostToNewFeedFriend(fileName,time);

                            finish();
                        }
                    });
                }
            });
        }
    }



    public void addMyPost(Uri uri,String fileName,Date time){
        Post post=new Post(emailCurrentUser,edtContent.getText().toString(),uri.toString(),fileName,time);
        mFireStore.collection("Users").document(emailCurrentUser)
                .collection("MyPosts").document(fileName)
                .set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewPostActivity.this, "Add Post Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        mFireStore.collection("Users").document(emailCurrentUser)
                .collection("MyPostAddress").document(fileName)
                .set(new AddressPostNewFeed(emailCurrentUser,fileName,time)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }


    public void addMyPostToNewFeedFriend(final String fileName, final Date time){
        mFireStore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    User user=documentSnapshot.toObject(User.class);
                    ArrayList<String> listFriend=user.getFriendList();
                    for (String i: listFriend) {
                        mFireStore.collection("Users").document(i)
                                .collection("NewFeed").document(fileName)
                                .set(new AddressPostNewFeed(emailCurrentUser,fileName,time)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                    mFireStore.collection("Users").document(emailCurrentUser)
                            .collection("NewFeed").document(fileName)
                            .set(new AddressPostNewFeed(emailCurrentUser,fileName,time)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }
        });
    }
}