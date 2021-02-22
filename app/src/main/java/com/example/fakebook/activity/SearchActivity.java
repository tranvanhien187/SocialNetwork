package com.example.fakebook.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fakebook.MainActivity;
import com.example.fakebook.R;
import com.example.fakebook.adapter.SearchAdapter;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText edtSearch;
    private Toolbar toolbar;
    private LinearLayout linearDecord;
    private RecyclerView recyclerViewSearch;
    private SearchAdapter mSearchAdapter;
    private ArrayList<User> userList;


    private FirebaseAuth mAth;
    private FirebaseFirestore mFirebaseFirestore;
    private String emailCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAth=FirebaseAuth.getInstance();
        mFirebaseFirestore=FirebaseFirestore.getInstance();
        emailCurrentUser=mAth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());
        initWidget();

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ac_logout:
                        //signOut();
                        return true;
                    case R.id.ac_search:
                        linearDecord.setVisibility(View.VISIBLE);
                        handleSearch();
                        return true;
                    case R.id.ac_setting:
                        Toast.makeText(SearchActivity.this, "setting", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    private void initWidget() {
        edtSearch=(EditText) findViewById(R.id.edt_search);
        recyclerViewSearch=(RecyclerView) findViewById(R.id.recycle_view_search_friend);
        toolbar=(Toolbar) findViewById(R.id.tool_bar);
        linearDecord=(LinearLayout) findViewById(R.id.linear_decord);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // handle arrow click here
//        switch (item.getItemId()){
//            case R.id.ac_logout:
//                //signOut();
//                return true;
//            case R.id.ac_search:
//                linearDecord.setVisibility(View.VISIBLE);
//                handleSearch();
//                return true;
//            case R.id.ac_setting:
//                Toast.makeText(SearchActivity.this, "setting", Toast.LENGTH_SHORT).show();
//                return true;
//            case android.R.id.home:
//                finish();
//                return true;
//        }
//        return false;
//    }
    public void handleSearch(){

        userList=new ArrayList<>();
        mSearchAdapter=new SearchAdapter(SearchActivity.this,userList,emailCurrentUser);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setAdapter(mSearchAdapter);


        String name=edtSearch.getText().toString().toLowerCase().trim();
        Query query=mFirebaseFirestore.collection("Users").orderBy("lowercaseName")
                .startAt(name).endAt(name+"\uf8ff");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange doc:value.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){
                        User user=doc.getDocument().toObject(User.class);
                        userList.add(user);
                        mSearchAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }
}