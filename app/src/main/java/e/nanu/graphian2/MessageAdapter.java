package e.nanu.graphian2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private String currentUid;
    private Context mcon;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

       /* public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;*/

        public TextView SenderTxt;
        public  TextView ReceiverTxt;
        public CircleImageView SenderDP;
        public ImageView SenderImg;
        public ImageView ReceiverImg;

        public MessageViewHolder(View view) {
            super(view);

           /* messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);*/



            SenderDP = (CircleImageView) view.findViewById(R.id.senderImgProfile);
            SenderTxt = (TextView) view.findViewById(R.id.senderMsgText);
            SenderImg = (ImageView) view.findViewById(R.id.senderImgMsg);
            ReceiverTxt = (TextView) view.findViewById(R.id.userMsgText);
            ReceiverImg = (ImageView) view.findViewById(R.id.userImgMsg);

        }
    }




    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat ,parent, false);

        return new MessageViewHolder(v);

    }



    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

               // viewHolder.displayName.setText(name);

               if(from_user !=null && currentUid !=null)
               {
                   if(!from_user.equals(currentUid))
                   {
                       Picasso.get().load(image)
                               .placeholder(R.drawable.default_avatar).into(viewHolder.SenderDP);
                   }
                   else
                       viewHolder.SenderDP.setVisibility(View.INVISIBLE);
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            if(from_user !=null && currentUid !=null)
            {
                if(from_user.equals(currentUid))
                {
                    viewHolder.SenderTxt.setVisibility(View.INVISIBLE);
                    viewHolder.ReceiverTxt.setText(c.getMessage());
                }
                else
                {
                    viewHolder.ReceiverTxt.setVisibility(View.INVISIBLE);
                    viewHolder.SenderTxt.setText(c.getMessage());
                }
            }

           /* viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);*/


        } else {


            if(from_user !=null && currentUid !=null)
            {
                if(from_user.equals(currentUid))
                {
                    viewHolder.ReceiverImg.setVisibility(View.VISIBLE);
                    viewHolder.ReceiverTxt.setVisibility(View.INVISIBLE);
                    viewHolder.SenderTxt.setVisibility(View.INVISIBLE);
                    viewHolder.SenderDP.setVisibility(View.INVISIBLE);
                    Picasso.get().load(c.getMessage())
                            .into(viewHolder.ReceiverImg);

                }
                else
                {
                    viewHolder.SenderImg.setVisibility(View.VISIBLE);
                    viewHolder.SenderTxt.setVisibility(View.INVISIBLE);
                    viewHolder.ReceiverTxt.setVisibility(View.INVISIBLE);
                    Picasso.get().load(c.getMessage())
                            .into(viewHolder.SenderImg);
                }
            }


            /*viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);*/

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }






}

