package com.example.pocketuni.profile.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.profile.common.EditProfileActivity;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.util.AdminBottomNavigationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import javax.annotation.Nullable;

public class AdminProfileActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    private Context context = AdminProfileActivity.this;
    private FloatingActionButton editProfileFloatingActionButton;
    private static final int ACTIVITY_NUMBER = 4;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private EditText editTextName, editTextEmail;
    private TextView inboxCount, postCount;
    private ImageView profileImage;
    private static final String TAG="ADM_PROF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_admin_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            finish();
        }

        getUserInfo();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        AdminBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        editTextName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        inboxCount = findViewById(R.id.inboxCount);
        postCount = findViewById(R.id.postCount);
        profileImage = findViewById(R.id.profileImage);
        editProfileFloatingActionButton = findViewById(R.id.editProfileFloatingActionButton);

        editTextName.setText(CurrentUser.getName());
        editTextEmail.setText(CurrentUser.getEmail());
        inboxCount.setText("EMPTY");
        postCount.setText("NONE");

        storageReference = firebaseStorage.getReference("uploads");

        if(CurrentUser.getDp().equalsIgnoreCase("default") || CurrentUser.getDp() == null){
            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefaultcir));
        } else {
            Glide.with(AdminProfileActivity.this).load(CurrentUser.getDp()).into(profileImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfilePicture();
            }
        });

        editProfileFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void signout(View view) {
        HashMap<String, Object> userStatus = new HashMap<String, Object>();
        userStatus.put("status", "offline");

        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.update(userStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void getUserInfo(){
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName((String) documentSnapshot.get("name"));
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) documentSnapshot.get("userId"));

                    editTextName.setText(CurrentUser.getName());
                    editTextEmail.setText(CurrentUser.getEmail());
                    inboxCount.setText("EMPTY");
                    postCount.setText("NONE");

                    if(CurrentUser.getDp().equalsIgnoreCase("default") || CurrentUser.getDp() == null){
                        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefaultcir));
                    } else {
                        Glide.with(getApplicationContext()).load(CurrentUser.getDp()).into(profileImage);
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        startListeningToUserChanges();
    }

    private void startListeningToUserChanges(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }
                if (documentSnapshot.exists()){
                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) firebaseAuth.getCurrentUser().getUid());
                    CurrentUser.setEmail((String) documentSnapshot.get("email"));

                    editTextName.setText(CurrentUser.getName());
                    editTextEmail.setText(CurrentUser.getEmail());
                    inboxCount.setText("EMPTY");
                    postCount.setText("NONE");

                    if(CurrentUser.getDp().equalsIgnoreCase("default") || CurrentUser.getDp() == null){
                        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefaultcir));
                    } else {
                        Glide.with(getApplicationContext()).load(CurrentUser.getDp()).into(profileImage);
                    }

                } else {
                    Log.w(TAG, "User not found. (LN)");
                }
            }
        });
    }

    private void selectProfilePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && data != null && data.getData()!= null){
            System.out.println("::::::::::RUNS::::::::");
            imageUri = data.getData();
            uploadProfilePicture();
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = AdminProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadProfilePicture() {
        final ProgressDialog progressDialog = new ProgressDialog(AdminProfileActivity.this);
        progressDialog.setMessage("Uploading in progress.");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference dpReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));

            uploadTask = dpReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dpReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.i(TAG, "Image URL : "+ url);

                            HashMap<String,Object> dpHashMap = new HashMap<String, Object>();
                            dpHashMap.put("dp", url);

                            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                            documentReference.update(dpHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    showToast("IMAGE UPLOADED.");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    showToast("FAILED TO UPLOAD THE IMAGE.");
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void showToast (String message) {
        Toast.makeText(AdminProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUserOnlineStatus(String status){
        if(firebaseAuth.getCurrentUser() != null) {
            HashMap<String, Object> userStatus = new HashMap<String, Object>();
            userStatus.put("status", status);

            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.update(userStatus);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserOnlineStatus("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserOnlineStatus("online");
    }
}