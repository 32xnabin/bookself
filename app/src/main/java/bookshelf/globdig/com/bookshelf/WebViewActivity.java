package bookshelf.globdig.com.bookshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class WebViewActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener{

    WebView webView;
    TextView title;
    ImageView back;
    TextView backText;

    private ProgressDialog progress;



    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationListener mLocationListener;
    String lat="";
    String longi="";
    int currTime=0;
    String stat_id;
    String release_id, geo_location, device;

    public void setOrientationManually(){

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int mScreenWidth = size.x;
        int height = size.y;
        //setRealDeviceSizeInPixels();
        System.out.println("-width----------KKKKKKKKKKKKKKKKK--actualllllll--------->>>>>>>>>>>" + mScreenWidth);

        if(mScreenWidth<=720) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView=(WebView)findViewById(R.id.release);

        setOrientationManually();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backText=(TextView)findViewById(R.id.backText);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

//        progress.setMax(100);
        progress=new ProgressDialog(this);
        progress.setMessage("Loading...");


        String id=getIntent().getStringExtra("magagine_id");


        release_id = id;



        String page_no=getIntent().getStringExtra("page_no");
        String o_f=getIntent().getStringExtra("o_f");

        if(o_f.equals("o")){

            SharedPreferences sf=this.getSharedPreferences("all",0);
            String url=sf.getString(id+"_link","");

            if(page_no.equals("0")) {
               url=url;
            }
            else{
                url=url+"?page-no="+page_no;
            }
         //   System.out.println("urllllllllllllllllllllllllllllllllllllllllll-->" +url);
            webView.loadUrl(url);

        }
        else{

            if(page_no.equals("0")) {
                webView.loadUrl("file:///mnt/sdcard/Download/.hidden/travelnews/goodfile" + id + "/index.html");
            }
            else{
                webView.loadUrl("file:///mnt/sdcard/Download/.hidden/travelnews/goodfile" + id + "/index.html?page-no="+page_no);
              //  System.out.println("urllllllllllllllllllllllllllllllllllllllllll-->" + "file:///mnt/sdcard/Download/.hidden/travelnews/goodfile" + id + "/index.html?page-no="+page_no);
            }
        }

//        if(page_no.equals("0")) {
//            webView.loadUrl("file:///mnt/sdcard/Download/goodfile" + id + "/index.html");
//        }
//        else if(page_no.equals("A")) {
//
//            SharedPreferences sf=this.getSharedPreferences("all",0);
//            String url=sf.getString(id+"_link","");
//
//            webView.loadUrl(url);
//        }
//        else{
//            webView.loadUrl("file:///mnt/sdcard/Download/goodfile" + id + "/index.html?page-no="+page_no);
//
//        }

        webView.setWebViewClient(new MyWebViewClient());


        ///////////////----------------for statics------------------------////////////////////////////////////////////////////
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        }

        Calendar c = Calendar.getInstance();
        currTime = (int) System.currentTimeMillis();

        //  Toast.makeText(this, "currTime connected..."+currTime, Toast.LENGTH_LONG).show();
        SharedPreferences sf = this.getSharedPreferences("all", 0);
        SharedPreferences.Editor edit = sf.edit();
        edit.putInt("curTime", currTime);
        edit.commit();
        forLocation();
        ///////////////----------------for statics------------------------////////////////////////////////////////////////////

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.hide();
            WebViewActivity.this.progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.show();
            WebViewActivity.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }


    /////////////------------------------related to statstics------------------------//////////////////////////////////////////////////

    public void forLocation(){


        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    WebViewActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        else{

            ReadStatAyncTask readStatAyncTask=new ReadStatAyncTask(this);
            readStatAyncTask.execute();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();

    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    @Override
    public void onConnected(Bundle arg0) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            System.out.println("Latitude--------------------------------->: "+ String.valueOf(mLastLocation.getLatitude())+"Longitude: "+
                    String.valueOf(mLastLocation.getLongitude()));

            lat=String.valueOf(mLastLocation.getLatitude());
            longi=String.valueOf(mLastLocation.getLongitude());

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    public class ReadStatAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public  ReadStatAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);


        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~ledernytt/api#registration
            device=getDeviceName()+":android";
            geo_location=lat+","+longi;
            release_id=
                  //  this.url = "http://95.211.75.201/~ledernytt/api/read-statistics?token="+token+"&release_id="+release_id+"&geo_location="+geo_location+"&device="+device;
            this.url = "http://95.211.75.201/~travelnewsdm/api/read-statistics?token="+token+"&release_id="+release_id+"&geo_location="+geo_location+"&device="+device;



            String newurl=this.url.replaceAll(" ", "%20");
            System.out.println("-----Reeeeeeeeeeeeeeeead---------->"+newurl);

            try {
                // String query = URLEncoder.encode(url, "utf-8");
                URL obj = new URL(newurl);
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
                System.out.println("--------------->"+jObject);

                String status=jObject.getString("status");

                if(status.equals("error")){}
                else{

                    JSONObject data=jObject.getJSONObject("data");

                    SharedPreferences sf=WebViewActivity.this.getSharedPreferences("all", 0);
                    SharedPreferences.Editor edit=sf.edit();
                    edit.putString("read_id",data.getString("statistics_id"));

                    edit.commit();

                }


                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    //  messageText.setText(message);
                }catch (Exception e){


                    // messageText.setText(jObject.getString("message"));
                }






            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
    public class UpdateStatAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public  UpdateStatAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);


        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all", 0);
            String token=sf.getString("token","token");
            stat_id=sf.getString("read_id","0");

            //http://95.211.75.201/~ledernytt/api#registration
            device=getDeviceName()+":android";
            geo_location=lat+","+longi;

            Calendar c = Calendar.getInstance();
            int  currTime1 =(int)System.currentTimeMillis();
            int total_time=(currTime1-currTime)/1000;
            release_id=
                    this.url = "http://95.211.75.201/~travelnewsdm/api/read-update-time-statistics?token="+token+"&statistics_id="+stat_id+"&total_time="+total_time;
            String newurl=this.url.replaceAll(" ", "%20");
            System.out.println("-----updateeeeeeeeeeeeeeeeeeeeeeeeee---------->"+newurl);



            try {
                // String query = URLEncoder.encode(url, "utf-8");
                URL obj = new URL(newurl);
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
                System.out.println("--------------->"+jObject);

                String status=jObject.getString("status");

                if(status.equals("error")){}
                else{



                }


                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    //  messageText.setText(message);
                }catch (Exception e){


                    // messageText.setText(jObject.getString("message"));
                }






            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
    @Override
    protected void onDestroy() {
        UpdateStatAyncTask updateStatAyncTask=new UpdateStatAyncTask(this);
        updateStatAyncTask.execute();
        super.onDestroy();
    }




}
