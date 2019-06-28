package e.nanu.graphian2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImageViewingActivity extends AppCompatActivity {


    private DatabaseReference mUserDatabase;
    String current_uid;
    private String onlineStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewing);

        mUserDatabase = FirebaseDatabase.getInstance()
                .getReference().child("Users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ImageView showImage=(ImageView) findViewById(R.id.showImage);
        Intent intent=getIntent();
        String imageUrl=intent.getStringExtra("imageKaPath");

        Toast.makeText(getApplicationContext(),"wait while image is being loaded",Toast.LENGTH_SHORT).show();
        /*View view = toast.getView();
        view.setBackgroundResource(R.color.white);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(getResources().getColor(R.color.black));
        toast.show();*/

        Picasso.get().load(imageUrl).into(showImage);
        mUserDatabase.keepSynced(true);

        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onlineStatus = dataSnapshot.child("online").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(current_uid != null)
            mUserDatabase.child("online").setValue("ImageViewingActivity");
    }



    @Override
    protected void onStop() {
        super.onStop();

        if(current_uid !=null)
        {
            if(onlineStatus != null)
            {
                if("ImageViewingActivity".equals(onlineStatus))
                    mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
            }
        }
    }
}
