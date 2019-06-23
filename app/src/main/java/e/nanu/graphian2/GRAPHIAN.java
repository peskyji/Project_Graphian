package e.nanu.graphian2;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


    /*-------------------------------------this is a application class which helps in offline support to display
                details offline--------------------------------------*/


public class GRAPHIAN extends Application {

    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // -------------Picasso offline support to load image faster from storage cache ------------------------

        Picasso.Builder builder= new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built =builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
