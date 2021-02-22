package com.example.fakebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fakebook.activity.SetUpForFirstTimeActivity;
import com.example.fakebook.model.ListEmailUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class LogInActivity extends AppCompatActivity {
    private Button btnLogIn,btnLogInByGG;
    private EditText edtEmail,edtPassword;
    private TextView txtSignUp;


    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    private int RC_SIGN_IN=1;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            handleSignIn(mAuth.getCurrentUser().getEmail());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initWidget();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInByEmail();
            }
        });

        btnLogInByGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInByGoogle();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initWidget() {
        btnLogIn=(Button) findViewById(R.id.btn_log_in);
        btnLogInByGG=(Button) findViewById(R.id.btn_log_in_with_gg);
        edtEmail=(EditText) findViewById(R.id.edt_email);
        edtPassword=(EditText) findViewById(R.id.edt_password);
        txtSignUp=(TextView) findViewById(R.id.txt_sign_up);
    }
    public void getCurrentlySignedInUser()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("AAA ", "getCurrentlySignedInUser : signed in");
            //btnSignIn.setVisibility(View.GONE);
        } else {
            //btnSignOut.setVisibility(View.GONE);
        }
    }
    public void signInByEmail() {
        final String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if ( !email.isEmpty() && !password.isEmpty() ){

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(LogInActivity.this, "Đăng nhập thành công!", LENGTH_SHORT).show();
                        handleSignIn(email);

                    } else {
                        Toast.makeText(LogInActivity.this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(LogInActivity.this,"Bạn chưa nhập đầy đủ!",Toast.LENGTH_LONG).show();
        }
    }

    public void signInByGoogle(){
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // thêm thư viện là nó tự sinh ra
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN&&data!=null){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInByGoogleResult(task);
        }
    }

    private void handleSignInByGoogleResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account =task.getResult(ApiException.class);
            Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        handleSignIn(mAuth.getCurrentUser().getEmail());
                    }else {
                        Toast.makeText(LogInActivity.this, "Falled", Toast.LENGTH_SHORT).show();
                    }
                    mGoogleSignInClient.signOut();
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sign In Falled", Toast.LENGTH_SHORT).show();
        }
    }
    public void handleSignIn(final String email)
    {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("User").document("AllUser")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists())
                {
                    ListEmailUser listEmailUser=task.getResult().toObject(ListEmailUser.class);
                    ArrayList<String> listUser=new ArrayList<>();
                    listUser.addAll(listEmailUser.getEmail());
                    if(listUser.contains(email)){
                        Intent mainIntent=new Intent(LogInActivity.this,MainActivity.class);
                        startActivity(mainIntent);
                    }else {
                        Intent setUpIntent=new Intent(LogInActivity.this, SetUpForFirstTimeActivity.class);
                        startActivity(setUpIntent);
                    }
                }else {
                    Intent setUpIntent=new Intent(LogInActivity.this,SetUpForFirstTimeActivity.class);
                    startActivity(setUpIntent);
                }

            }
        });
    }
}