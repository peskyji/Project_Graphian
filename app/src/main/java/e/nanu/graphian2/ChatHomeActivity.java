package e.nanu.graphian2;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChatHomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        Toast.makeText(getApplicationContext(),"chathome activity",Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("GRAPHIAN");

        /*if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }*/


        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        } else {

          //  mUserRef.child("online").setValue("true");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
       // mUserRef.child("online").setValue("false");
    }

    private void sendToStart() {

        Intent startIntent = new Intent(ChatHomeActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

           // mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(ChatHomeActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }


        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(ChatHomeActivity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }

        return true;
    }

}
