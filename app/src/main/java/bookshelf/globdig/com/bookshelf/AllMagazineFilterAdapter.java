package bookshelf.globdig.com.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Navin on 2/17/2016.
 */
public class AllMagazineFilterAdapter extends ArrayAdapter<AllMagazineFilterObject> {
    Context context;

    public AllMagazineFilterAdapter(Context context, ArrayList<AllMagazineFilterObject> users,String searchText) {
        super(context, R.layout.filter_magazine_list_item, users);
        this.context=context;
        this.searchText=searchText;

    }

    ImageView img1;
    LinearLayout dynamic;
    SharedPreferences sf;
    String searchText;
   int pos;
    static class ViewHolder {
        ImageView img1;
        LinearLayout dynamic;
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AllMagazineFilterObject obj = getItem(position);
        pos=position;
        sf=context.getSharedPreferences("all", 0);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.filter_magazine_list_item, parent,
                    false);
            ViewHolder holder = new ViewHolder();
            holder.img1=(ImageView)convertView.findViewById(R.id.magagine1);
            holder.dynamic=(LinearLayout)convertView.findViewById(R.id.dynamicContents);
            final ViewHolder ho=holder;
            setDynamicContent(ho.dynamic,obj);
//            holder.img1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println("id 1 -------------------------------" + objj.getId());
//                    Intent i = new Intent(context, ReadMagazineActivity.class);
//                    AllMagazineFilterObject objj = getItem(pos);
//                    int id = Integer.parseInt(objj.getId());
//                    i.putExtra("mag_id", id);
//                    context.startActivity(i);
//                }
//            });


            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();





        //System.out.println("id 1 ------------jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj-------------------" + obj.getImage_url());

        try {
//            Picasso.with(context)
//                    .load(obj.getImage_url())
//                    .into(img1);
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.with(context).load(obj.getImage_url()).fetch();
            picasso.with(context)
                    .load(obj.getImage_url())
                    .placeholder(R.drawable.loading)
                   // .resize(200, 200)
                   // .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.img1);

//            Picasso.with(getActivity())
//                    .load(imageUrl)
//                    .placeholder(R.drawable.placeholder)
//                    .error(R.drawable.error)
//                    .resize(screenWidth, imageHeight)
//                    .centerInside()
//                    .into(imageView);



        }catch (Exception e){}


        // Return the completed view to render on screen
        return convertView;
    }

    AllMagazineFilterObject objj;
    public void setDynamicContent(LinearLayout ll, AllMagazineFilterObject jj){

       // img.setImageBitmap(bmp);
      final  AllMagazineFilterObject jj1=jj;

        LinearLayout options_layout = ll;
        LinearLayout options=new LinearLayout(context);

       // System.out.println("content---------------->"+content);
         objj = getItem(pos);
       

       String content=jj1.getContent();
//        String coo=content;
//        int maxLogSize = 1000;
//        for(int j = 0; j <= coo.length() / maxLogSize; j++) {
//            int start = j * maxLogSize;
//            int end = (j+1) * maxLogSize;
//            end = end > coo.length() ? coo.length() : end;
//            Log.v("888888888888888888888", coo.substring(start, end));
//        }

        String[] page=content.split("\\|\\|\\|");
       // String[] page=content.split("|||");

       // System.out.println("pages length-66666666666666666666666666666666666666666-->"+page.length);
       // System.out.println("pages--->"+page[0]);


        List<PagesObject> pagesList=new ArrayList<PagesObject>();

        for(int i=0;i<page.length;i++) {
            PagesObject obj = new PagesObject();
            System.out.println("pages length-3333333333333333333333333333333333333333333333333333333333-->"+page[i]);
//            System.out.println("searchText jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj--->" + searchText);
               String pagU=page[i].toUpperCase();
               String searchU=searchText.toUpperCase();
            if(pagU.contains(searchU)) {
                String[] p=new String[2];

            try {
                p = page[i].split("!!!");
                if (p[0].contains("Add")) {

                    obj.setHeading("no");
                    obj.setContent("no");
                    pagesList.add(obj);
                    continue;
                }
                else{
                    obj.setHeading(p[0]);
                    obj.setContent(p[1]);
                    pagesList.add(obj);
                }

            } catch (Exception e) {

                e.printStackTrace();

                obj.setHeading("no");
                obj.setContent("no");
                pagesList.add(obj);
                continue;
            }



        }
            else{

                obj.setHeading("no");
                obj.setContent("no");
                pagesList.add(obj);

            }

            System.out.println("Addeddddddddddddddddddd-3333333333333333333333333333333333333333333333333333333333--heading>"+((PagesObject)pagesList.get(pagesList.size()-1)).getHeading());
            System.out.println("Addeddddddddddddddddddd-3333333333333333333333333333333333333333333333333333333333--conetnt>"+((PagesObject)pagesList.get(pagesList.size()-1)).getContent());
        }
        int count=0;
        System.out.println("pagesList size-66666666666666666666666666666666666666666-->"+pagesList.size());
        for(PagesObject obj:pagesList) {
//            System.out.println("Heading--->" + obj.getHeading());
//            System.out.println("Content--->" + obj.getContent());


            // if(count%2==0||count==0){
            if(!obj.getHeading().equals("no")){
            options = new LinearLayout(context);
            //}

            TextView text = new TextView(context);

                int pageNo=count+1;
            text.setText(pageNo + "." + obj.getHeading());
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
            llp.setMargins(8, 4, 8, 4); // llp.setMargins(left, top, right, bottom);


            text.setLayoutParams(llp);
            text.setBackgroundColor(Color.parseColor("#cccccc"));
            text.setTextColor(Color.parseColor("#000000"));
            text.setPadding(12, 8, 12, 8);
            text.setBackgroundResource(R.drawable.rounded_grey);


            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView text = (TextView) v;
                    String trimmed = text.getText().toString().substring(0, text.getText().toString().indexOf("."));
                   // Toast.makeText(context,objj.getId(),Toast.LENGTH_LONG).show();

                    if (sf.getString(objj.getId(), "no").equals(objj.getId() + "")) {
                        Intent i = new Intent(context, WebViewActivity.class);
                        //int id=Integer.parseInt(objj.getId());
                        AllMagazineFilterObject objj = getItem(pos);
                        int id=Integer.parseInt(jj1.getId());
                        i.putExtra("magagine_id", jj1.getId());
                        i.putExtra("page_no", trimmed);
                        i.putExtra("o_f", "f");


                        context.startActivity(i);

                    } else {
                        Intent i = new Intent(context, WebViewActivity.class);
                        //int id=Integer.parseInt(objj.getId());
                        i.putExtra("magagine_id", objj.getId());
                        i.putExtra("page_no", trimmed);
                        i.putExtra("o_f", "o");


                        context.startActivity(i);

                    }


                    // Toast.makeText(context, "test000--" + trimmed, Toast.LENGTH_LONG).show();
                }
            });
            if (!obj.getHeading().trim().isEmpty()) {
                options.addView(text);
            }

            // if(count%6==0){
            options_layout.addView(options);

            // }
        }

            count++;
        }










    }
}
