package e.nanu.graphian2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mCurrentUserDatabase;

    private LinearLayoutManager mLayoutManager;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String onlineStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // database reference from where data is to be retrived
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mCurrentUserDatabase =mUsersDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // this line is for offline support to load data even when not connected to the internet
        mUsersDatabase.keepSynced(true);
        mLayoutManager = new LinearLayoutManager(this);

        // recyclerView
        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);


        Query query=mUsersDatabase;
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                (options)
        {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setDisplayName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });
            }

        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);

        //----------------------------- retrieving current user online status----------------------------------
        mCurrentUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onlineStatus = dataSnapshot.child("online").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-----------------------------------------------------------------------------------------------------

    }


    // users list will be visible due to the method below listning wala
    @Override
    protected void onStart() {
        super.onStart();
       firebaseRecyclerAdapter .startListening();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online").setValue("UsersActivity");

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            if(onlineStatus != null)
            {
                if("UsersActivity".equals(onlineStatus))
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("online").setValue(ServerValue.TIMESTAMP);
            }
        }
    }


    // ViewHolder subclass
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }

        public void setUserImage(final String thumb_image){

            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
           // Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
            if (!"default".equals(thumb_image)) {

               // final String temp=thumb_image;

                // ---------------- load image from offline feature and if error occur in that then load image from Database
                Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(userImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

                    }
                });

            }

        }


    }

}

