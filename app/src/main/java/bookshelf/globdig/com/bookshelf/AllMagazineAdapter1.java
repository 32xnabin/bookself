package bookshelf.globdig.com.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Navin on 1/7/2016.
 */
public class AllMagazineAdapter1 extends ArrayAdapter<AllMagazineFilterObject> {
    Context context;

    public AllMagazineAdapter1(Context context, ArrayList<AllMagazineFilterObject> users) {
        super(context, R.layout.all_magazine_list_item, users);
        this.context=context;

    }
    AllMagazineFilterObject user1;
    AllMagazineFilterObject user22;
    ImageView img1;
    ImageView img2;





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AllMagazineFilterObject user = getItem(position);
        AllMagazineFilterObject user2 = getItem(1);
        // Check if an existing view is being reused, otherwise inflate the view
        user1=user;
        user22=user2;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_magazine_list_item, parent,
                    false);


            img1=(ImageView)convertView.findViewById(R.id.mag1);
            img2=(ImageView)convertView.findViewById(R.id.mag2);




        }



        try {



            Picasso.with(context)
                    .load(user.getImage_url())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img1);
            Picasso.with(context)
                    .load(user2.getImage_url())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img2);


        }catch (Exception e){


        }
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("id 1 -------------------------------"+user1.getId());
                Intent i=new Intent(context,ReadMagazineActivity.class);
                i.putExtra("mag_id", user1.getId());
                context.startActivity(i);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("id 1 -------------------------------" + user22.getId());
                Intent i=new Intent(context,ReadMagazineActivity.class);
                i.putExtra("mag_id",user22.getId());
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
