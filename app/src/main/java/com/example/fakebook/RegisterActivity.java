package com.example.fakebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fakebook.activity.SetUpForFirstTimeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail,edtPass,edtReTypePass;
    private Button btnRegister;

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidget();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    public void initWidget(){
        edtEmail=(EditText) findViewById(R.id.edt_register_email);
        edtPass=(EditText) findViewById(R.id.edt_register_password);
        edtReTypePass=(EditText) findViewById(R.id.edt_retype_register_password);
        btnRegister=(Button) findViewById(R.id.btn_register);
    }

    private void signUp()
    {

        String email=edtEmail.getText().toString();
        String pass=edtPass.getText().toString();
        String retypePass=edtReTypePass.getText().toString();
        if(!email.contains("@gmail.com")) Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
        else if(pass.length()<8) Toast.makeText(this, "Mật khẩu chứa ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
        else {
            if(pass.equals(retypePass)){
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    Intent setUpIntent = new Intent(RegisterActivity.this, SetUpForFirstTimeActivity.class);
                                    startActivity(setUpIntent);
                                    finish();
                                }else {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            }
        }


    }
}