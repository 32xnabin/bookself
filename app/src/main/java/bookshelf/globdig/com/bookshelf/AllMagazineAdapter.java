package bookshelf.globdig.com.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Navin on 1/7/2016.
 */
public class AllMagazineAdapter extends ArrayAdapter<AllMagazineObject> {
    Context context;

    public AllMagazineAdapter(Context context, ArrayList<AllMagazineObject> users) {
        super(context, R.layout.all_magazine_list_item, users);
        this.context=context;

    }
     AllMagazineObject user1;
    ImageView img1;
    ImageView img2;

    @Override
    public long getItemId(int position) {
        AllMagazineObject user = getItem(position);
        return ((long) user.getId());
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AllMagazineObject user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        user1=user;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_magazine_list_item, parent,
                    false);


            img1=(ImageView)convertView.findViewById(R.id.mag1);
            img2=(ImageView)convertView.findViewById(R.id.mag2);




        }




        try {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.with(context).load(user.getImage_url()).fetch();
            picasso.with(context)

                    .load(user.getImage_url())
                    .placeholder(R.drawable.loading)

                //    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(img1);
            picasso.with(context).load(user.getImage_url1()).fetch();
            picasso.with(context)
                    .load(user.getImage_url1())
                    .placeholder(R.drawable.loading)
                   // .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img2);


        }catch (Exception e){


        }
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("id 1 -------------------------------" + user1.getId());
                Intent i=new Intent(context,ReadMagazineActivity.class);
                i.putExtra("mag_id", user1.getId());
                i.putExtra("mag_id", user1.getId());
                context.startActivity(i);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("id 1 -------------------------------" + user1.getId1());
                Intent i=new Intent(context,ReadMagazineActivity.class);
                i.putExtra("mag_id",user1.getId1());
                context.startActivity(i);
                //Toast.makeText(context,user1.getId1(),Toast.LENGTH_LONG).show();
            }
        });

//        try {
//            Picasso.with(context)
//                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
//                    .into(img1);
//            Picasso.with(context)
//                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
//                    .into(img2);
//        }catch (Exception e){}


        // Return the completed view to render on screen
        return convertView;
    }



}
