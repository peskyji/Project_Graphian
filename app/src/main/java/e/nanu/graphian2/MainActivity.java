package e.nanu.graphian2;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mUserRef;
    private  Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //------------------------------- changing theme for application -------------------------------------
        setTheme(R.style.MyTheme);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.cpd));
            getWindow().setStatusBarColor(getResources().getColor(R.color.cpd));
        }
        //----------------------------------------------------------------------------------------

        setContentView(R.layout.activity_main);


        //------------------------------  setting toolbar ------------------------------------------------
        mToolbar = (Toolbar) findViewById(R.id.chat_app_bar1);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

         actionBar.setDisplayHomeAsUpEnabled(true);     // it displays back button
         actionBar.setDisplayShowCustomEnabled(true);
         mToolbar.setBackgroundResource(R.color.cpd);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        action_bar_view.setBackgroundResource(R.color.cpd);
        actionBar.setCustomView(action_bar_view);
        //--------------------------------------------------------------------------------------------------



    }

    @Override
    public void onStart() {
        super.onStart();

        final TextView DisplayName= (TextView) findViewById(R.id.custom_bar_title);
        final TextView status = (TextView) findViewById(R.id.custom_bar_seen);
        final CircleImageView dp= (CircleImageView) findViewById(R.id.custom_bar_image);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
            mUserRef.keepSynced(true);
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String image=dataSnapshot.child("thumb_image").getValue().toString();

                    DisplayName.setText(name);
                    status.setText("logged in");
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(dp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            DisplayName.setText("Log In");
            status.setVisibility(View.INVISIBLE);
            dp.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
        }

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(currentUser == null)
               {
                   Intent intent = new Intent(MainActivity.this,StartActivity.class);
                   intent.putExtra("callFromMain","callFromMain");
                   startActivity(intent);
               }

            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();

    }


    public void visitGeu(View v)
    {
        Toast.makeText(getApplicationContext(),"Visiting GEU Official WebSite",Toast.LENGTH_LONG).show();
        String url="https://www.geu.ac.in/content/geu/en.html";
        Intent intent=new Intent(MainActivity.this,WebviewActivity.class);
        intent.putExtra("visiturl",url);
        startActivity(intent);
    }

    public void visitGehuDdn(View v)
    {
        Toast.makeText(getApplicationContext(),"Visiting GEHU Official WebSite",Toast.LENGTH_LONG).show();
        String url="https://www.gehu.ac.in/content/gehu/en.html";
        Intent intent=new Intent(MainActivity.this,WebviewActivity.class);
        intent.putExtra("visiturl",url);
        startActivity(intent);
    }

    public void visitErpGehu(View v)
    {
        Toast.makeText(getApplicationContext(),"Visiting GEHU Student ERP",Toast.LENGTH_LONG).show();
        String url="http://www.student.gehu.ac.in/";
        Intent intent=new Intent(MainActivity.this,WebviewActivity.class);
        intent.putExtra("visiturl",url);
        startActivity(intent);
    }

    public void visitErpGeu(View v)
    {
        Toast.makeText(getApplicationContext(),"Visiting GEU Student ERP",Toast.LENGTH_LONG).show();
        String url="http://www.student.geu.ac.in/";
        Intent intent=new Intent(MainActivity.this,WebviewActivity.class);
        intent.putExtra("visiturl",url);
        startActivity(intent);
    }

    public void startChat(View v)
    {

        if(currentUser == null){

            //sendToStart();
            Toast.makeText(getApplicationContext(),"start activity",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(MainActivity.this,StartActivity.class);
            startActivity(intent);

        } else {

            //mUserRef.child("online").setValue("true");
            Toast.makeText(getApplicationContext(),"welcome"+currentUser.getDisplayName(),Toast.LENGTH_LONG).show();
            Intent intent=new Intent(MainActivity.this,ChatHomeActivity.class);
            startActivity(intent);
        }
    }
}
