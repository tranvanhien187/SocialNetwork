

package com.example.fakebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;


import android.widget.Toast;

import com.example.fakebook.activity.SearchActivity;
import com.example.fakebook.adapter.SectionPagerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FirebaseAuth mAuth;

    private int RC_SIGN_IN=1;

    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_friend,
            R.drawable.ic_person,
            R.drawable.ic_notification,
            R.drawable.ic_message
    };
    private SectionPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();

        mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());


        mViewPager.setAdapter(mSectionsPagerAdapter);


        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);


        mAuth=FirebaseAuth.getInstance();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ac_logout:
                        signOut();
                        return true;
                    case R.id.ac_search:
                        Intent searchIntent=new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(searchIntent);
                        return true;
                    case R.id.ac_setting:
                        Toast.makeText(MainActivity.this, "setting", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        Intent intent = new Intent(this, MyService.class);
        startService(intent);


    }

    private void signOut() {
        mAuth.signOut();
        Intent logInIntent=new Intent(MainActivity.this,LogInActivity.class);
        startActivity(logInIntent);
        finish();
    }

    public void initWidget() {
        toolbar= (Toolbar) findViewById(R.id.tool_bar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN&&data!=null){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account =task.getResult(ApiException.class);
            Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sign In Falled", Toast.LENGTH_SHORT).show();
        }

    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                }else {
                    Toast.makeText(MainActivity.this, "Falled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}