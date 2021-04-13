package com.example.fakebook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fakebook.MainActivity;
import com.example.fakebook.R;
import com.example.fakebook.model.ListEmailUser;
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

public class SetUpForFirstTimeActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private EditText edtName,edtDay,edtMonth,edtYear,edtHomeTown;
    private RadioGroup radioGroupSex,radioGroupRelationship;
    private Button btnSubmit;
    private RadioButton radMale, radFemale,radMarriage,radioSingle;
    private Spinner spinnerEducation;
    private boolean isMale=true;
    private boolean isMarriage=false;
    private ArrayAdapter<String> mAdapter;
    Uri mainimageURI = null;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    String emailCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_for_first_time);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        initWidget();


        mAdapter=new ArrayAdapter<String>(SetUpForFirstTimeActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.education));

        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEducation.setAdapter(mAdapter);

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SetUpForFirstTimeActivity.this);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInforUserAtFirstTime();
            }
        });
        radMale.setOnCheckedChangeListener(listenerRadioSex);
        radFemale.setOnCheckedChangeListener(listenerRadioSex);

        radioSingle.setOnCheckedChangeListener(listenerRadioRelationship);
        radMarriage.setOnCheckedChangeListener(listenerRadioSex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainimageURI = result.getUri();
                setupImage.setImageURI(mainimageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void addInforUserAtFirstTime(){
        final String name=edtName.getText().toString();
        final String day=edtDay.getText().toString();
        final String month=edtMonth.getText().toString();
        final String year=edtYear.getText().toString();
        final String homeTown=edtHomeTown.getText().toString();
        final String education=spinnerEducation.getSelectedItem().toString();

        if(TextUtils.isEmpty(name)
                ||TextUtils.isEmpty(day)
                ||TextUtils.isEmpty(month)
                ||TextUtils.isEmpty(year)
                ||TextUtils.isEmpty(homeTown)
                ||TextUtils.isEmpty(education)
                ||mainimageURI==null){
            Toast.makeText(SetUpForFirstTimeActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        else{
            final String email=mAuth.getCurrentUser().getEmail();
            emailCurrentUser=email.substring(0,email.length()-"@gmail.com".length());
            final Date time = Calendar.getInstance().getTime();
            final String fileName=time.toString()
                    .replace(":","")
                    .replace(" ","")
                    .replace("+","");

            StorageReference filePath= storageReference.child(emailCurrentUser).child("my_avatar").child(fileName+".jpg");
            filePath.putFile(mainimageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            User cur=new User(uri.toString(),name,emailCurrentUser,day+"/"+month+"/"+year,homeTown,education,isMarriage,isMale,uri.toString());
                            firebaseFirestore.collection("Users").document(emailCurrentUser)
                                    .set(cur)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent mainIntent=new Intent(SetUpForFirstTimeActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    });
                            addUserToAllUser(email);
                        }
                    });
                }
            });

        }
    }

    public void addUserToAllUser(final String email){
        firebaseFirestore.collection("User").document("AllUser")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    ListEmailUser listEmailUser=task.getResult().toObject(ListEmailUser.class);
                    ArrayList<String> listUser=listEmailUser.getEmail();
                    listUser.add(email);
                    listEmailUser=new ListEmailUser(listUser);
                    firebaseFirestore.collection("User").document("AllUser")
                            .set(listEmailUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SetUpForFirstTimeActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    ArrayList<String> listUser=new ArrayList<>();
                    listUser.add(email);
                    ListEmailUser listEmailUser=new ListEmailUser(listUser);
                    firebaseFirestore.collection("User").document("AllUser")
                            .set(listEmailUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

            }
        });
    }
    public void initWidget(){
        edtName=(EditText) findViewById(R.id.edt_set_up_name);
        edtDay=(EditText) findViewById(R.id.edt_set_up_day);
        edtMonth=(EditText) findViewById(R.id.edt_set_up_month);
        edtYear=(EditText) findViewById(R.id.edt_set_up_year);
        edtHomeTown=(EditText) findViewById(R.id.edt_set_up_home_town);
        radioGroupSex=(RadioGroup) findViewById(R.id.rad_group_set_up_sex);
        radioGroupRelationship=(RadioGroup) findViewById(R.id.rad_group_set_up_group_relationship);
        radFemale =(RadioButton) findViewById(R.id.rad_set_up_female);
        radMale =(RadioButton) findViewById(R.id.rad_set_up_male);
        radMarriage=(RadioButton) findViewById(R.id.rad_set_up_marriage);
        radioSingle=(RadioButton) findViewById(R.id.rad_set_up_signle);
        btnSubmit=(Button) findViewById(R.id.btn_submit);
        setupImage =(CircleImageView) findViewById(R.id.img_avatar);
        spinnerEducation =(Spinner) findViewById(R.id.spinner_set_up_education);
    }
    CompoundButton.OnCheckedChangeListener listenerRadioSex=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Toast.makeText(SetUpForFirstTimeActivity.this,buttonView.getText().toString(), Toast.LENGTH_SHORT).show();
                if(buttonView.getText().toString().equals("Nam")){
                    isMale=true;
                }
                else{
                    isMale=false;
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
                }
                else{
                    isMarriage=false;
                }
            }
        }
    };

}