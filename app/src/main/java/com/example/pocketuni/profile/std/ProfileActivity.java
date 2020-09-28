package com.example.pocketuni.profile.std;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.pocketuni.R;
import com.example.pocketuni.model.CurrentUser;
import com.example.pocketuni.model.Reminder;
import com.example.pocketuni.organizer.std.ReminderBroadcastReceiver;
import com.example.pocketuni.profile.admin.AdminProfileActivity;
import com.example.pocketuni.profile.common.EditProfileActivity;
import com.example.pocketuni.security.SigninActivity;
import com.example.pocketuni.timeline.std.MainActivity;
import com.example.pocketuni.util.StdBottomNavigationHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton editProfileFloatingActionButton;
    private Context context = ProfileActivity.this;
    private static final int ACTIVITY_NUMBER = 4;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private int reminderCounter = 0;
    private String TAG = "SPA_REMDEL_COUNT";
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private EditText editTextName, editTextEmail, editTextCourse, editTextBatch;
    private TextView inboxCount, reminderCount, postCount;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //redirect if already logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(ProfileActivity.this, SigninActivity.class));
            finish();
        }

        getUserInfo();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        StdBottomNavigationHelper.enableNavigation(context, bottomNavigationView, ACTIVITY_NUMBER);

        editTextName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCourse = findViewById(R.id.editTextCourse);
        editTextBatch = findViewById(R.id.editTextBatch);
        inboxCount = findViewById(R.id.inboxCount);
        reminderCount = findViewById(R.id.reminderCount);
        postCount = findViewById(R.id.postCount);
        profileImage = findViewById(R.id.profileImage);
        editProfileFloatingActionButton = findViewById(R.id.editProfileFloatingActionButton);

        editTextName.setText(CurrentUser.getName());
        editTextEmail.setText(CurrentUser.getEmail());
        editTextCourse.setText(CurrentUser.getCourse());
        editTextBatch.setText(CurrentUser.getBatch());
        inboxCount.setText("EMPTY");

        if(CurrentUser.isIsRemindersOn() == true) {
            reminderCount.setText("ON");
        } else {
            reminderCount.setText("OFF");
        }
        postCount.setText("NONE");

        storageReference = firebaseStorage.getReference("uploads");

        if(CurrentUser.getDp().equalsIgnoreCase("default") || CurrentUser.getDp() == null){
            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefaultcir));
        } else {
            Glide.with(ProfileActivity.this).load(CurrentUser.getDp()).into(profileImage);
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
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    public void signout() {
        //siging out takes place after the reminders are deleted (OnComplete)
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this,SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void disableRemindersAndSignOut(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        updateUserOnlineStatus("offline");

        CollectionReference collectionReferenceForDelete = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("reminders");
        collectionReferenceForDelete.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        reminderCounter = task.getResult().size();
                        Log.i(TAG, "Total number of reminders found: " + reminderCounter);

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Reminder remdel = queryDocumentSnapshot.toObject(Reminder.class);
                            Log.i(TAG, "local reminder for " + remdel.getReminderItemId() + " (ID: " + reminderCounter + ") has been deleted");

                            Intent reminderIntent = new Intent(ProfileActivity.this, ReminderBroadcastReceiver.class);
                            pendingIntent = PendingIntent.getBroadcast(ProfileActivity.this, reminderCounter, reminderIntent, PendingIntent.FLAG_NO_CREATE);
                            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            if (pendingIntent!=null) {
                                alarmManager.cancel(pendingIntent);
                                Reminder rem = queryDocumentSnapshot.toObject(Reminder.class);
                                Log.i(TAG,  "(PA) Reminder deleted for "+ rem.getReminderItemId());
                            }
                            reminderCounter--; //unique request code for each alarm in descending order of the number of reminders
                        }

                        signout();
                    }
                    else{
                        signout();
                    }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && data != null && data.getData()!= null){
            System.out.println("::::::::::RUNS::::::::");
            imageUri = data.getData();
            uploadProfilePicture();
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadProfilePicture() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Uploading...");
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
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getUserInfo(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    CurrentUser.setEmail((String) task.getResult().get("email"));
                    CurrentUser.setName((String) task.getResult().get("name"));
                    CurrentUser.setBatch((String) task.getResult().get("batch"));
                    CurrentUser.setCourse((String) task.getResult().get("course"));
                    CurrentUser.setDp((String) task.getResult().get("dp"));
                    CurrentUser.setSemester((String) task.getResult().get("semester"));
                    CurrentUser.setYear((String) task.getResult().get("academic_year"));
                    CurrentUser.setUserType((String) task.getResult().get("userType"));
                    CurrentUser.setUserId((String) task.getResult().get("userId"));
                    CurrentUser.setIsRemindersOn((Boolean) task.getResult().get("isRemindersOn"));
                    CurrentUser.setRemainderMinutes(((Long) task.getResult().get("remainderMinutes")).intValue());

                    editTextName.setText(CurrentUser.getName());
                    editTextEmail.setText(CurrentUser.getEmail());
                    editTextCourse.setText(CurrentUser.getCourse());
                    editTextBatch.setText(CurrentUser.getBatch());
                    inboxCount.setText("EMPTY");
                    if(CurrentUser.isIsRemindersOn() == true) {
                        reminderCount.setText("ON");
                    } else {
                        reminderCount.setText("OFF");
                    }
                    postCount.setText("NONE");

                    if(CurrentUser.getDp().equalsIgnoreCase("default") || CurrentUser.getDp() == null){
                        profileImage.setImageDrawable(getResources().getDrawable(R.drawable.userchatdefaultcir));
                    } else {
                        Glide.with(ProfileActivity.this).load(CurrentUser.getDp()).into(profileImage);
                    }

                }
                startListeningToUserChanges();
            }
        });
    }

    private void startListeningToUserChanges(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "User: Listen failed. (LN)");
                    return;
                }
                if (documentSnapshot.exists()){
                    CurrentUser.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    CurrentUser.setName((String) documentSnapshot.get("name"));
                    CurrentUser.setBatch((String) documentSnapshot.get("batch"));
                    CurrentUser.setCourse((String) documentSnapshot.get("course"));
                    CurrentUser.setDp((String) documentSnapshot.get("dp"));
                    CurrentUser.setSemester((String) documentSnapshot.get("semester"));
                    CurrentUser.setYear((String) documentSnapshot.get("academic_year"));
                    CurrentUser.setUserType((String) documentSnapshot.get("userType"));
                    CurrentUser.setUserId((String) firebaseAuth.getCurrentUser().getUid());
                    CurrentUser.setIsRemindersOn((Boolean) documentSnapshot.get("isRemindersOn"));
                    CurrentUser.setEmail((String) documentSnapshot.get("email"));
                    CurrentUser.setRemainderMinutes(((Long) documentSnapshot.get("remainderMinutes")).intValue());
                    CurrentUser.setUserId((String) documentSnapshot.get("userId"));
                    Log.w(TAG, "User updated. (LN) : " + CurrentUser.getName());

                    editTextName.setText(CurrentUser.getName());
                    editTextEmail.setText(CurrentUser.getEmail());
                    editTextCourse.setText(CurrentUser.getCourse());
                    editTextBatch.setText(CurrentUser.getBatch());
                    inboxCount.setText("EMPTY");
                    if(CurrentUser.isIsRemindersOn() == true) {
                        reminderCount.setText("ON");
                    } else {
                        reminderCount.setText("OFF");
                    }
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