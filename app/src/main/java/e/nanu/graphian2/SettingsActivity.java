package e.nanu.graphian2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;


    //Android Layout

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;


    private static final int GALLERY_PICK = 1;

    // Storage Firebase to store images and other stuffs to database storage section
    private StorageReference mImageStorage;
    private Uri mCropImageUri;
    public Uri imageUri;
    private ProgressDialog mProgressDialog;
    String downloadurl=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mStatus = (TextView) findViewById(R.id.settings_status);

        mStatusBtn = (Button) findViewById(R.id.settings_status_btn);
        mImageBtn = (Button) findViewById(R.id.settings_image_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        // this line is for offline support to load data even when not connected to the internet
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // retrieving data from firebase database
                String name = dataSnapshot.child("name").getValue().toString();

                // here we are getting the URL of the image and keeping it in a string
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                // updating name , status  according to the database
                mName.setText(name);
                mStatus.setText(status);

                // method below will work if a user has posted its DP otherwise default avatar sign will be shown
                if (!thumb_image.equals("default")) {

                    // ---------------- load image from offline feature and if error occur in that then load image from Database
                    Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // on clicking change status button we will send current status to Status Activity
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);

            }
        });


        // on clicking change image button
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // using CropImage library of hdodenhof
// start picker to get image for cropping and then use the image in cropping activity


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .start(SettingsActivity.this);


            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();

        String currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(currentUserId != null)
        {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue("true");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if (resultUri != null) {
                    // Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
                    mProgressDialog = new ProgressDialog(SettingsActivity.this);
                    mProgressDialog.setTitle("Uploading Image...");
                    mProgressDialog.setMessage("Please wait while we upload and process the image.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();


                    // thumb_filepath is the thumbnail of an image which is of less size than the original image
                    File thumb_filePath = new File(resultUri.getPath());
                    String current_user_id = mCurrentUser.getUid();


                    // using compressor libarary to compress an image

                    Bitmap thumb_bitmap;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                   try{
                            thumb_bitmap = new Compressor(this)
                               .setMaxWidth(200)
                               .setMaxHeight(200)
                               .setQuality(75)
                               .compressToBitmap(thumb_filePath);
                       thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                   }

                   catch(Exception e)
                   {
                       Toast.makeText(getApplicationContext(),"compressor not working",Toast.LENGTH_SHORT);
                   }

                       // converting thumb image to byte array to upload to the firebase database
                       final byte[] thumb_byte = baos.toByteArray();


                   // creating references of firebse storage
                    final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");


                    if (filepath != null) {
                        filepath.putFile(resultUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        // getting download url of uploaded image
                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                downloadurl= uri.toString();
                                                // Toast.makeText(getApplicationContext(), downloadurl, Toast.LENGTH_SHORT).show();

                                                // storing the uploaded image to the image field of user's data base
                                                mUserDatabase.child("image").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), "saved to images", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });


                                        // uploading thumb image of profile imaage in storage -> profile_images -> thumbs
                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadThumbUrl= uri.toString();

                                                        // updating image and thumb_image field of user ' s in the firebase database
                                                        Map update_hashMap = new HashMap();
                                                        update_hashMap.put("image", downloadurl);
                                                        update_hashMap.put("thumb_image", downloadThumbUrl);

                                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    mProgressDialog.dismiss();
                                                                    Toast.makeText(SettingsActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

                                                                }

                                                            }
                                                        });
                                                    }
                                                });






                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(SettingsActivity.this, "file not uploaded", Toast.LENGTH_LONG).show();
                                    }
                                });


                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

    }

}

