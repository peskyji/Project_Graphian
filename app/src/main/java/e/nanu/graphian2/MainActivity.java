package e.nanu.graphian2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
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
        Toast.makeText(getApplicationContext(),"Visiting GEHU Dehradun Official WebSite",Toast.LENGTH_LONG).show();
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
            Intent intent=new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}
