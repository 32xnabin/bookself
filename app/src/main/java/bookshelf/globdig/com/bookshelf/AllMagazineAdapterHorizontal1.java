package bookshelf.globdig.com.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Navin on 1/7/2016.
 */
public class AllMagazineAdapterHorizontal1 extends ArrayAdapter<AllMagazineFilterObject> {
    Context context;

    public AllMagazineAdapterHorizontal1(Context context, ArrayList<AllMagazineFilterObject> users) {
        super(context, R.layout.all_magazine_list_item_hor1, users);
        this.context=context;
    }



    static class ViewHolder {
        ImageView img1;
        TextView issue_date;
        TextView article;
        Button open;
        Button download;
        Button read;
        Button emailUs;
        ImageView star;
        LinearLayout btns;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AllMagazineFilterObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.all_magazine_list_item_hor1, parent,
                    false);

            ViewHolder holder = new ViewHolder();
            holder.img1=(ImageView)convertView.findViewById(R.id.mag_image);
            holder.issue_date=(TextView)convertView.findViewById(R.id.mag_issue_date);
            holder.article=(TextView)convertView.findViewById(R.id.mag_article);
            holder.open=(Button)convertView.findViewById(R.id.open_magazine);
            holder.read=(Button)convertView.findViewById(R.id.open_magazine_online);
            holder.download=(Button)convertView.findViewById(R.id.download_magazine);
            holder.emailUs=(Button)convertView.findViewById(R.id.email_us_list);
            holder.star=(ImageView)convertView.findViewById(R.id.favorite_star);
            holder.btns=(LinearLayout)convertView.findViewById(R.id.buttonsDown);
            testFunction(holder.btns);
            if(obj.getSubscribed().equals("true")||obj.getIsFree().equals("1")){
                holder.download.setVisibility(View.VISIBLE);
                holder.read.setVisibility(View.VISIBLE);
                holder.emailUs.setVisibility(View.GONE);
            }
            else{

                holder.download.setVisibility(View.GONE);
                holder.read.setVisibility(View.GONE);
                holder.emailUs.setVisibility(View.VISIBLE);
            }
            final   AllMagazineFilterObject obj1=obj;
            SharedPreferences sf=context.getSharedPreferences("all",0);
            if(sf.getString(obj1.getId(),"no").equals(obj1.getId()+"")){
                holder.download.setVisibility(View.GONE);
                holder.open.setVisibility(View.VISIBLE);

            }
            else{
                if(obj.getSubscribed().equals("true")||obj.getIsFree().equals("1")) {
                    holder.download.setVisibility(View.VISIBLE);
                }
                holder.open.setVisibility(View.GONE);
            }

            ///fav
            String favString=sf.getString("favlist", ",");

            String[]favArray=favString.split(",");

            if(Arrays.asList(favArray).contains(obj.getId())){

                holder.star.setImageResource(R.drawable.ic_star_full);
            }
            else{
                holder.star.setImageResource(R.drawable.ic_star_blank);
            }

            final ViewHolder ho=holder;
            holder.star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    markAsFavorite(obj1, ho.star);
                }
            });


            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("magagine_id", obj1.getId());
                    intent.putExtra("page_no", "0");
                    intent.putExtra("o_f", "f");
                    context.startActivity(intent);
                }
            });
            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("magagine_id", obj1.getId());
                    intent.putExtra("o_f", "o");
                    intent.putExtra("page_no", "0");
                    context.startActivity(intent);
                }
            });
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sf = context.getSharedPreferences("all", 0);
                    //   Toast.makeText(context, obj1.getId(), Toast.LENGTH_LONG).show();

                    if (sf.getString(obj1.getId(), "no").equals("no")) {

                        ((HomeActivity) context).startDownload(obj1);
                    }

                }
            });

            holder.emailUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"havard.heggen@mediadigital.no"});

                    try {
                        context.startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            convertView.setTag(holder);


        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();







         holder.issue_date.setText(obj.getIssue_date());

//        holder.article.setText("Some Introduction " +
//                "sdfsfsdfj fjsdlfjsd fjsdlkf ");

        try {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.with(context).load(obj.getImage_url()).resize(200, 400).fetch();
            picasso.with(context)

                    .load(obj.getImage_url())
                    .placeholder(R.drawable.loading)
                    .into(holder.img1);



        }catch (Exception e){


        }
        //   testFunction();


        return convertView;
    }

    public void markAsFavorite(AllMagazineFilterObject obj,ImageView str){



        SharedPreferences sf=context.getSharedPreferences("all", 0);
        SharedPreferences.Editor edit=sf.edit();
        String favString=sf.getString("favlist", ",");
        String[]favArray=favString.split(",");
        if(!Arrays.asList(favArray).contains(obj.getId())){
            favString=favString+","+obj.getId();
            edit.putString("favlist",favString);
            edit.commit();
            str.setImageResource(R.drawable.ic_star_full);
        }
        else{
            String result=",";
            for(int i=0;i<favArray.length;i++){
                if(!favArray[i].equals(obj.getId())) {
                    result = result + "," + favArray[i];
                }
            }
            edit.putString("favlist",result);
            edit.commit();
            str.setImageResource(R.drawable.ic_star_blank);
        }


    }

    public void testFunction(LinearLayout ll){

        //buttonsDown

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int mScreenWidth = size.x;
        int height = size.y;


        if(mScreenWidth<=720){
            ll.setOrientation(LinearLayout.VERTICAL);

        }
        else{
            ll.setOrientation(LinearLayout.HORIZONTAL);
        }


    }

    private void setRealDeviceSizeInPixels() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        int mWidthPixels = displayMetrics.widthPixels;
        int mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }

    }

}
