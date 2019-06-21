package e.nanu.graphian2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private final int PICK_IMAGE_REQUEST = 71;

    // Storage Firebase to store images and other stuffs to database storage section
    private StorageReference mImageStorage;
      private   Uri mCropImageUri;
      public Uri imageUri;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_settings);

     mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
     mName = (TextView) findViewById(R.id.settings_name);
     mStatus = (TextView) findViewById(R.id.settings_status);

     mStatusBtn = (Button) findViewById(R.id.settings_status_btn);
     mImageBtn = (Button) findViewById(R.id.settings_image_btn);

     mImageStorage =  FirebaseStorage.getInstance().getReference();

     mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

     String current_uid = mCurrentUser.getUid();



     mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
     mUserDatabase.keepSynced(true);

     mUserDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

       // retrieving data from firebase database
       String name = dataSnapshot.child("name").getValue().toString();
       final String image = dataSnapshot.child("image").getValue().toString();
       String status = dataSnapshot.child("status").getValue().toString();
       String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

       mName.setText(name);
       mStatus.setText(status);

      /* if(!image.equals("default")) {

        //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

        Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
         @Override
         public void onSuccess() {

         }

         @Override
         public void onError() {

          Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

         }
        });

       }*/


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


      /*Intent galleryIntent = new Intent();
       galleryIntent.setType("image/*");
       galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

       startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),PICK_IMAGE_REQUEST );
*/
// start picker to get image for cropping and then use the image in cropping activity


                 CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .setMinCropWindowSize(500, 500)
                        .start(SettingsActivity.this);


      }
     });


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if(resultUri!=null)
                {
                   // Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
                    mProgressDialog = new ProgressDialog(SettingsActivity.this);
                    mProgressDialog.setTitle("Uploading Image...");
                    mProgressDialog.setMessage("Please wait while we upload and process the image.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();


                    String current_user_id = mCurrentUser.getUid();
                    StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");



                    if(filepath!=null)
                    {
                        filepath.putFile(resultUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"file uploaded",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(SettingsActivity.this,"file not uploaded",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }



                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }









/*
 public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
  if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
   if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // required permissions granted, start crop image activity
    startCropImageActivity(mCropImageUri);
   } else {
    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
   }
  }
 }


 private void startCropImageActivity(Uri imageUri) {
  CropImage.activity(imageUri)
          .start(SettingsActivity.this);
 }

*/






 /*@Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);

  if(requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){

   Uri imageUri = data.getData();

   CropImage.activity(imageUri)
           .setAspectRatio(1, 1)
           .setMinCropWindowSize(500, 500)
           .start(this);

   //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

  }


  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

   CropImage.ActivityResult result = CropImage.getActivityResult(data);

   if (resultCode == RESULT_OK) {


    mProgressDialog = new ProgressDialog(SettingsActivity.this);
    mProgressDialog.setTitle("Uploading Image...");
    mProgressDialog.setMessage("Please wait while we upload and process the image.");
    mProgressDialog.setCanceledOnTouchOutside(false);
    mProgressDialog.show();


    Uri resultUri = result.getUri();

    File thumb_filePath = new File(resultUri.getPath());

    String current_user_id = mCurrentUser.getUid();


    Bitmap thumb_bitmap = new Compressor(this)
            .setMaxWidth(200)
            .setMaxHeight(200)
            .setQuality(75)
            .compressToBitmap(thumb_filePath);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    final byte[] thumb_byte = baos.toByteArray();


    StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
    final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");



    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
     @Override
     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

      if(task.isSuccessful()){

       final String download_url = task.getResult().getDownloadUrl().toString();

       UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
       uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

         String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

         if(thumb_task.isSuccessful()){

          Map update_hashMap = new HashMap();
          update_hashMap.put("image", download_url);
          update_hashMap.put("thumb_image", thumb_downloadUrl);

          mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

            if(task.isSuccessful()){

             mProgressDialog.dismiss();
             Toast.makeText(SettingsActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();

            }

           }
          });


         } else {

          Toast.makeText(SettingsActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
          mProgressDialog.dismiss();

         }


        }
       });



      } else {

       Toast.makeText(SettingsActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
       mProgressDialog.dismiss();

      }

     }
    });



   } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

    Exception error = result.getError();

   }
  }


 }
*/

  /*  public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/


}