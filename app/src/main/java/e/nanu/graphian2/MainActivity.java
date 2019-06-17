package e.nanu.graphian2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    }
}
