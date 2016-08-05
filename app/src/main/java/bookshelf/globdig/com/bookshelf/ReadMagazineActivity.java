package bookshelf.globdig.com.bookshelf;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadMagazineActivity extends Activity {

    String id,token,name,image,zip_file,extracted_file,introduction,article;

    ImageView mainImage,star,back;
    TextView  nameT,intro,articl;
    Button openMagazine,downloadMagazine,subscribeMagazine;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;

    String release_id;


    String donload_or_subscribe="download";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_magazine);



        id=getIntent().getIntExtra("mag_id", 0)+"";
        findViews();
        SharedPreferences sf=this.getSharedPreferences("all", 0);
        token=sf.getString("token", "token");
        ReadMagazineAyncTask readMagazineAyncTask=new ReadMagazineAyncTask(this);
        readMagazineAyncTask.execute("");
        back=(ImageView)findViewById(R.id.back_read);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

      //  Toast.makeText(this,id+"-->"+sf.getString(id+"","no"),Toast.LENGTH_LONG).show();



        if(sf.getString(id+"","no").equals(id+"")){
            downloadMagazine.setVisibility(View.GONE);
            openMagazine.setVisibility(View.VISIBLE);

        }
        else{

            downloadMagazine.setVisibility(View.VISIBLE);
            openMagazine.setVisibility(View.GONE);
        }



        //startDownload();
    }

    public void findViews(){

        mainImage=(ImageView)findViewById(R.id.mainImageMagazine);
        nameT=(TextView)findViewById(R.id.name);
        intro=(TextView)findViewById(R.id.introduction);
        articl=(TextView)findViewById(R.id.article);

        star=(ImageView)findViewById(R.id.favorite_button);
        SharedPreferences sf=this.getSharedPreferences("all", 0);
        String favString=sf.getString("favlist", ",");

        String[]favArray=favString.split(",");

        if(Arrays.asList(favArray).contains(id+"".trim())){

            star.setImageResource(R.drawable.star_green);
        }
        else{
            star.setImageResource(R.drawable.star_blank);
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                markAsFavorite();
            }
        });

        openMagazine=(Button)findViewById(R.id.openMagazine);

        openMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file=new File("file:///sdcard/goodfile/index.html");
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                startActivity(intent);


                Intent intent = new Intent(ReadMagazineActivity.this, WebViewActivity.class);
                intent.putExtra("magagine_id", id);
                intent.putExtra("page_no", "0");
                startActivity(intent);

            }
        });
        downloadMagazine=(Button)findViewById(R.id.downloadMagazine);
        downloadMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sf = ReadMagazineActivity.this.getSharedPreferences("all", 0);
              //  Toast.makeText(ReadMagazineActivity.this, sf.getString(id + "", "no") + "", Toast.LENGTH_LONG).show();

                if (sf.getString(id + "", "no").equals("no")) {

                    startDownload();
                }

            }
        });
        subscribeMagazine=(Button)findViewById(R.id.subscribeMagazine);
        subscribeMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscribeMagazineAyncTask subscribeMagazineAyncTask =new SubscribeMagazineAyncTask(ReadMagazineActivity.this);
                subscribeMagazineAyncTask.execute("");
            }
        });
    }

    public void markAsFavorite(){



        SharedPreferences sf=ReadMagazineActivity.this.getSharedPreferences("all", 0);
        SharedPreferences.Editor edit=sf.edit();
        String favString=sf.getString("favlist", ",");
        String[]favArray=favString.split(",");
        if(!Arrays.asList(favArray).contains(id+"")){
            favString=favString+","+id;
            edit.putString("favlist",favString);
            edit.commit();
            star.setImageResource(R.drawable.star_green);
        }
        else{
            String result=",";
            for(int i=0;i<favArray.length;i++){
                if(!favArray[i].equals(id+"")) {
                    result = result + "," + favArray[i];
                }
            }
            edit.putString("favlist",result);
            edit.commit();
            star.setImageResource(R.drawable.star_blank);
        }


    }

    private void startDownload() {
        String url = zip_file;
        new DownloadFileAsync().execute(url);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading Magazine..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }



    public class SubscribeMagazineAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public SubscribeMagazineAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/subscribe-by-release-id?release_id="+release_id+"&token="+token;

            System.out.println("-----this.url---------->" + this.url);



            try {
                // String query = URLEncoder.encode(url, "utf-8");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                Log.d("async update", "\nSending 'GET' request to URL : " + con);
                Log.d("async update", "\nSending 'GET' request to URL : " + url);


                // add reuqest header
                con.setRequestMethod("GET");



                int responseCode = con.getResponseCode();
                Log.d("async date", "\nSending 'GET' request to URL : " + url);
                // Log.d("async date", "Post parameters : " + urlParameters);
                Log.d("async date", "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


            } catch (MalformedURLException e) {
                Log.d("async date", "MalformedUURL Exception" + e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("async date", "IO Exception" + e);
                e.printStackTrace();
            }
            Log.d("async date", "response==" + response.toString());
            return response.toString();



        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {

                JSONObject jObject = new JSONObject(result);


                //    downloadAppData();







            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    public class ReadMagazineAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public ReadMagazineAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/get-release-by-id?id="+id+"&token="+token;

            System.out.println("-----this.url---------->" + this.url);



            try {
                // String query = URLEncoder.encode(url, "utf-8");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                Log.d("async update", "\nSending 'GET' request to URL : " + con);
                Log.d("async update", "\nSending 'GET' request to URL : " + url);


                // add reuqest header
                con.setRequestMethod("GET");



                int responseCode = con.getResponseCode();
                Log.d("async date", "\nSending 'GET' request to URL : " + url);
                // Log.d("async date", "Post parameters : " + urlParameters);
                Log.d("async date", "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


            } catch (MalformedURLException e) {
                Log.d("async date", "MalformedUURL Exception" + e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("async date", "IO Exception" + e);
                e.printStackTrace();
            }
            Log.d("async date", "response==" + response.toString());
            return response.toString();



        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {

                JSONObject jObject = new JSONObject(result);

                JSONObject data=jObject.getJSONObject("data");
                release_id=data.getString("id");
                name=data.getString("name");
                image=data.getString("image");
                zip_file=data.getString("zip_file");
                extracted_file=data.getString("extracted_file");
                introduction=data.getString("introduction");
                article=data.getString("article");

                String subsc=data.getString("subscribed");
                String free=data.getString("is_free");

//                if(subsc.equals("true")||free.equals("1")){
//                    subscribeMagazine.setVisibility(View.GONE);
//                    downloadMagazine.setVisibility(View.VISIBLE);
//                }
//                else{
//                    subscribeMagazine.setVisibility(View.VISIBLE);
//                    downloadMagazine.setVisibility(View.GONE);
//                }

                try {
                    Picasso picasso = Picasso.with(context);
                    picasso.setIndicatorsEnabled(false);

                    picasso.with(context).load(image).fetch();
                    picasso.with(context)

                            .load(image)
                            .placeholder(R.drawable.loading)
                                    //    .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(mainImage);



                }catch (Exception e){


                }

                nameT.setText(name.toString());
                intro.setText(introduction.toString());
                articl.setText(article.toString());
            //    downloadAppData();







            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard/Download/goodfile"+id+".zip");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
            if(progress[0].equals("100")){mProgressDialog.dismiss();
                try {
                    unzip(new File("/sdcard/Download/goodfile"+id+".zip"), new File("/sdcard/Download/goodfile"+id+"/"));
                }catch (Exception e){}
                SharedPreferences sf=ReadMagazineActivity.this.getSharedPreferences("all", 0);
                SharedPreferences.Editor edit=sf.edit();
                edit.putString(id+"",id+"");
                edit.commit();
                downloadMagazine.setVisibility(View.GONE);
                openMagazine.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected void onPostExecute(String unused) {
           // dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }
    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }
}
