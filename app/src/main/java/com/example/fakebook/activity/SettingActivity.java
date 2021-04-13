package com.example.fakebook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.fakebook.R;
import com.example.fakebook.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private TextView txtBirth,txtEmail;
    private EditText edtName,edtCity;
    private RadioGroup radGroupSex,radGroupRelationship;
    private Spinner spinnerEducation;
    private CircleImageView imgAvatar;
    private Button btnSubmit;
    private ImageView imgChooseImage,imgBack;
    private RadioButton radMale,radFemale,radSingle,radMarriage;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ArrayAdapter mAdapter;
    private ArrayList<String> mEducationList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference storageReference;
    private String emailCurrentUser;
    private boolean isMale,isMarriage;
    private User user;
    Uri mainimageURI = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        emailCurrentUser=mAuth.getCurrentUser().getEmail();
        emailCurrentUser=emailCurrentUser.substring(0,emailCurrentUser.length()-"@gmail.com".length());
        mFirebaseFirestore=FirebaseFirestore.getInstance();

        initWidget();

        setUpInformation();

        txtBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        radMale.setOnCheckedChangeListener(listenerRadioSex);
        radFemale.setOnCheckedChangeListener(listenerRadioSex);

        radSingle.setOnCheckedChangeListener(listenerRadioRelationship);
        radMarriage.setOnCheckedChangeListener(listenerRadioSex);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                progressBar.setVisibility(View.VISIBLE);
                
                updateInformation();
            }
        });

        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initWidget() {
        imgAvatar=(CircleImageView) findViewById(R.id.img_setting_avatar);
        imgChooseImage=(ImageView) findViewById(R.id.img_setting);
        txtBirth=(TextView) findViewById(R.id.txt_setting_birth_day);
        txtEmail=(TextView) findViewById(R.id.txt_setting_email);
        edtName=(EditText) findViewById(R.id.edt_setting_name);
        edtCity=(EditText) findViewById(R.id.edt_setting_city);
        radGroupSex=(RadioGroup) findViewById(R.id.rad_group_setting_sex);
        radGroupRelationship=(RadioGroup) findViewById(R.id.rad_group_setting_group_relationship);
        radMale=(RadioButton) findViewById(R.id.rad_setting_male);
        radFemale=(RadioButton) findViewById(R.id.rad_setting_female);
        radSingle=(RadioButton) findViewById(R.id.rad_setting_signle);
        radMarriage=(RadioButton) findViewById(R.id.rad_setting_marriage);
        btnSubmit=(Button) findViewById(R.id.btn_setting_submit);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        spinnerEducation=(Spinner) findViewById(R.id.spinner_setting_education);
        toolbar=(Toolbar) findViewById(R.id.tool_bar);
        imgBack=(ImageView) findViewById(R.id.img_back);

    }

    public void setUpInformation(){
        mEducationList=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.education)));
        mAdapter=new ArrayAdapter<String>(SettingActivity.this,android.R.layout.simple_list_item_1,mEducationList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(mAdapter);
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    user=documentSnapshot.toObject(User.class);
                    edtName.setText(user.getName());
                    edtCity.setText(user.getCity());
                    txtBirth.setText(user.getDateOfBirth());
                    txtEmail.setText(user.getEmail());
                    mEducationList.remove(user.getEducation());
                    mEducationList.add(0,user.getEducation());
                    mAdapter.notifyDataSetChanged();
                    if(user.getMale()){
                        radGroupSex.check(R.id.rad_setting_male);
                    }else {
                        radGroupSex.check(R.id.rad_setting_female);
                    }
                    if(user.getIsMarriage()){
                        radGroupRelationship.check(R.id.rad_setting_marriage);
                    }else {
                        radGroupRelationship.check(R.id.rad_setting_signle);
                    }

                    Glide.with(SettingActivity.this)
                            .load(user.getAvatar())
                            .into(imgAvatar);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainimageURI = result.getUri();
                imgAvatar.setImageURI(mainimageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void updateInformation(){
        if(mainimageURI==null){
            handleUpdateInformation();
        }else {
             Date time = Calendar.getInstance().getTime();
             String fileName=time.toString()
                    .replace(":","")
                    .replace(" ","")
                    .replace("+","");
            storageReference.child(emailCurrentUser).child("my_avatar").child(fileName+".jpg")
                    .putFile(mainimageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.addPictureUri(uri.toString());
                            user.setAvatar(uri.toString());
                            handleUpdateInformation();
                        }
                    });
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    public void handleUpdateInformation(){
        user.setMale(isMale);
        user.setDateOfBirth(txtBirth.getText().toString());
        user.setIsMarriage(isMarriage);
        user.setName(edtName.getText().toString());
        user.setCity(edtCity.getText().toString());
        user.setEducation(spinnerEducation.getSelectedItem().toString());
        mFirebaseFirestore.collection("Users").document(emailCurrentUser)
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAA Update","Update information");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void pickDate()
    {
        Calendar calendar= Calendar.getInstance();
        int day=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog= new DatePickerDialog(SettingActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                txtBirth.setText(i2+"/"+(i1+1)+"/"+i);
                user.setDateOfBirth(i2+"/"+(i1+1)+"/"+i);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    CompoundButton.OnCheckedChangeListener listenerRadioSex=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                if(buttonView.getText().toString().equals("Nam")){
                    isMale=true;
                    user.setMale(true);
                }
                else{
                    isMale=false;
                    user.setMale(false);
                }
            }
        }
    };
    CompoundButton.OnCheckedChangeListener listenerRadioRelationship=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                if(buttonView.getText().toString().equals("Marriage")){
                    isMarriage=true;
                    user.setIsMarriage(true);
                }
                else{
                    isMarriage=false;
                    user.setIsMarriage(false);
                }
            }
        }
    };
}