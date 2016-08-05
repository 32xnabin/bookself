package bookshelf.globdig.com.bookshelf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    ListView allMagazinesList;
    LinearLayout profileFields;
    LinearLayout profileButtons;
    LinearLayout about;
    String first_name;
    String last_name;
    String email;
    String phone;

    LinearLayout settings;
    LinearLayout email_us;
    LinearLayout profile_us;
    LinearLayout change_password;
    LinearLayout logout;


    EditText emailField,fNameField,lNameField,phoneField;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AllMagazineFilterObject currentObj;

    Button cancel_profile,update_profile;

    int state=0;
    Toolbar toolbar;
    private ProgressDialog progress;
    EditText search;
    String searchText="";
    ArrayList<AllMagazineFilterObject> allItemList;

    AllMagazineAdapterHorizontal1 horAdapter;
    TextView listEmpty;
    DownloadFileAsync downloadMagazine;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setLogo(R.drawable.nav_icon_logo);
        toolbar.setTitle("Alle utgivelser");
        toolbar.setBackgroundResource(R.drawable.navigationbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayUseLogoEnabled(false);
        listEmpty=(TextView)findViewById(R.id.list_empty);

        search=(EditText)findViewById(R.id.searchField);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                searchText = text;
                if (!text.isEmpty()) {

                    callDatabase(text);
                } else {
                    state = 0;
                    callDatabaseInitial();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        progress=new ProgressDialog(this);
       // progress.setTitle("Loading...");
        progress.setMessage("Loading...");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
      //  navigationView.setItemTextColor(Color.parseColor("FFFF2A00"));

        allMagazinesList=(ListView)findViewById(R.id.allMagazinesList);
        allMagazinesList.requestFocus();
//        allMagazinesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentObj= (AllMagazineFilterObject)horAdapter.getItem(position);
//                System.out.println("-------------------iddddddddddddddddddddddddddddddddddddd----------->"+currentObj.getId());
//                startDownload();
//
//            }
//        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {


            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

         profileFields=(LinearLayout)findViewById(R.id.profile_fields);
         profileButtons=(LinearLayout)findViewById(R.id.profile_buttons);
         about=(LinearLayout)findViewById(R.id.about);

         update_profile=(Button)findViewById(R.id.profile_update);



         emailField=(EditText)findViewById(R.id.profile_email);
         fNameField=(EditText)findViewById(R.id.profile_faname);
         lNameField=(EditText)findViewById(R.id.profile_laname);
         phoneField=(EditText)findViewById(R.id.profile_phone);

         settings=(LinearLayout)findViewById(R.id.settings);;
         email_us=(LinearLayout)findViewById(R.id.email_us);
         profile_us=(LinearLayout)findViewById(R.id.profile_us);
         change_password=(LinearLayout)findViewById(R.id.change_password);
         logout=(LinearLayout)findViewById(R.id.logout);

        settingFunctions();

        SharedPreferences sf=this.getSharedPreferences("all", 0);
        String version=sf.getString("version","0");
        if(isNetworkAvailable(this)){

            firstExecution();

        }
        else{
            callDatabaseInitial();
        }

       // if(state==0) {

        //}

        forLocation();
        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else{
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        }


    }



    public void addNewDataToDatabase(ArrayList<AllMagazineFilterObject> ls,String version){

        SharedPreferences sf=this.getSharedPreferences("all", 0);
        SharedPreferences.Editor edit=sf.edit();
        edit.putString("version", version);
        edit.commit();
        DBHelper1 dbHelper=new DBHelper1(this);
        try {
            dbHelper.deleteDetails();
        }catch (Exception e){
            e.printStackTrace();
        }
        for(AllMagazineFilterObject obj:ls){
            try{
                dbHelper.insertAllDetails(obj.getId(),obj.getName(),obj.getIntro(),obj.getArticle(),obj.getIssue_date(),obj.getImgByte(),obj.getImage_url(),obj.getContent(),obj.getZip_file(),obj.getSubscribed(),obj.getIsFree());
            }catch (Exception e){e.printStackTrace();}
        }


    }

    public void callDatabaseInitial(){
        openMagazinePage();


        DBHelper1 helper=new DBHelper1(this);
        try {


            allItemList=helper.getAll(state);
            horAdapter=new AllMagazineAdapterHorizontal1(this,allItemList);
            allMagazinesList.setAdapter(horAdapter);
            listEmpty.setVisibility((!horAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
            allMagazinesList.setEmptyView(findViewById(R.id.list_empty));









        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callDatabase(String searchT){


        DBHelper1 helper=new DBHelper1(this);
        try {

            searchT=searchT.replaceAll("ø", "&Oslash;");
            searchT=searchT.replace("å", "&aelig;");
            searchT=searchT.replace("æ","&aelig;");

            ArrayList<AllMagazineFilterObject> ls=helper.search(searchT);
          //  Toast.makeText(this,"sizeeee--------------------->"+ls.size(),Toast.LENGTH_LONG).show();
            AllMagazineFilterAdapter allMagazineFilterAdapter=new AllMagazineFilterAdapter(this,ls,searchT);
            allMagazinesList.setAdapter(allMagazineFilterAdapter);
            listEmpty.setVisibility((!horAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
            allMagazinesList.setEmptyView(findViewById(R.id.list_empty));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendEmail(){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"nabinwell@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void settingFunctions() {
        email_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isNetworkAvailable(HomeActivity.this)) {
                    startActivity(new Intent(HomeActivity.this, EmailActivity.class));
                }else{
                    Toast.makeText(HomeActivity.this,"No internet connection !",Toast.LENGTH_SHORT).show();
                }

//                if(isNetworkAvailable(HomeActivity.this)) {
//                    sendEmail();
//                }else{
//                    Toast.makeText(HomeActivity.this,"No internet connection !",Toast.LENGTH_SHORT).show();
//                }


            }
        });

        profile_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(HomeActivity.this)) {
                    startActivity(new Intent(HomeActivity.this, ChangeProfileActivity.class));
                }else{
                    Toast.makeText(HomeActivity.this,"No internet connection !",Toast.LENGTH_SHORT).show();
                }


            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(HomeActivity.this)) {
                    startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
                }else{
                    Toast.makeText(HomeActivity.this,"No internet connection !",Toast.LENGTH_SHORT).show();
                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sf = HomeActivity.this.getSharedPreferences("all", 0);
                SharedPreferences.Editor edit = sf.edit();
                edit.putString("logout", "true");
                edit.commit();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    public void refreshContent(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                firstExecution();
            }
        }, 3000l);
    }

    private boolean listIsAtTop()   {
        if(allMagazinesList.getChildCount() == 0) return true;
        return allMagazinesList.getChildAt(0).getTop() == 0;
    }

    public void loadAllMagazines(){
        openMagazinePage();

//        GetMagazineAyncTask getMagazineAyncTask=new GetMagazineAyncTask(this,state);
//        getMagazineAyncTask.execute("");
    }
    public void firstExecution(){
        openMagazinePage();

        if(isNetworkAvailable(this)) {
            GetMagazineAyncTask getMagazineAyncTask = new GetMagazineAyncTask(this, state);
            getMagazineAyncTask.execute("");

//            GetMagazineAyncTaskFirstTime getMagazineAyncTaskFirstTime = new GetMagazineAyncTaskFirstTime(this, 0);
//            getMagazineAyncTaskFirstTime.execute("");
        }else{Toast.makeText(this,"No internet connection !",Toast.LENGTH_SHORT).show();}
    }

    public void openProfilePage() {

        settings.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        profileFields.setVisibility(View.VISIBLE);
        profileButtons.setVisibility(View.VISIBLE);



    }

    public void openMagazinePage(){

        search.setVisibility(View.VISIBLE);
        settings.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//        about.setVisibility(View.GONE);
//        profileFields.setVisibility(View.GONE);
//        profileButtons.setVisibility(View.GONE);
    }

    public void openAboutPage(){
       // getSupportActionBar().setDisplayUseLogoEnabled(false);
        search.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);

    }
    public void openSettingsPage(){
     //   getSupportActionBar().setDisplayUseLogoEnabled(false);
        search.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        listEmpty.setVisibility(View.GONE);
//        about.setVisibility(View.GONE);
//        profileFields.setVisibility(View.GONE);
//        profileButtons.setVisibility(View.GONE);
    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettingsPage();
            toolbar.setTitle("SETTINGS");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(HomeActivity.this, AboutActivity.class));
          //  toolbar.setTitle("ABOUT US");
            // Handle the camera action
        }
        else if (id == R.id.nav_magazines) {
            //   Toast.makeText(this,"Downloaded==",Toast.LENGTH_LONG).show();
            //getSupportActionBar().setDisplayUseLogoEnabled(true);
           // toolbar.setLogo(R.drawable.nav_icon_logo);
            state=0;
            callDatabaseInitial();
            toolbar.setTitle("Alle utgivelser");


        }


        else if (id == R.id.nav_downloaded_magazines) {
          //  getSupportActionBar().setDisplayUseLogoEnabled(false);
         //   Toast.makeText(this,"Downloaded==",Toast.LENGTH_LONG).show();

            state=1;
            callDatabaseInitial();
            toolbar.setTitle("LASTET NED NYHETER");


        } else if (id == R.id.nav_favorite_magazines) {
           // getSupportActionBar().setDisplayUseLogoEnabled(false);
            state=2;
            callDatabaseInitial();
            toolbar.setTitle("FAVORITT NYHETER");

        } else if (id == R.id.nav_my_profile) {
           // getSupportActionBar().setDisplayUseLogoEnabled(false);
          //  if(isNetworkAvailable(this)) {
                startActivity(new Intent(HomeActivity.this, ChangeProfileActivity.class));
//            }else{
//                Toast.makeText(this,"No internet connection !",Toast.LENGTH_SHORT).show();
//            }

        } else if (id == R.id.nav_setting) {
          //  getSupportActionBar().setDisplayUseLogoEnabled(false);
            openSettingsPage();
            toolbar.setTitle("INNSTILLINGER");


        }
//     else if (id == R.id.nav_subscribe) {
//        //  getSupportActionBar().setDisplayUseLogoEnabled(false);
//            startActivity(new Intent(HomeActivity.this, SubscriptionActivity.class));
//
//
//    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class GetMagazineAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;
        int state;



        public GetMagazineAyncTask(Context context,int state) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            this.state=state;
            progress=new ProgressDialog(context);
            progress.setMessage("Legge utgivelser");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {

            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/get-all-releases-by-magazine-id?token="+token;

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
           // progress.hide();
            // TODO Auto-generated method stub
            progress.hide();
            super.onPostExecute(result);
            try {

                JSONObject jObject = new JSONObject(result);
                System.out.println("--------------->"+jObject);

                String status=jObject.getString("status");

                if(status.equals("error")){

                    String message=jObject.getString("message");
                    if(message.equals("Token not found")){
                        SharedPreferences sf = HomeActivity.this.getSharedPreferences("all", 0);
                        SharedPreferences.Editor edit = sf.edit();
                        edit.putString("logout", "true");
                        edit.commit();
                        Toast.makeText(HomeActivity.this,"Your Session has expired ! Please login",Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context,LoginActivity.class));

                    }
                }
                else{
                    SharedPreferences sf=HomeActivity.this.getSharedPreferences("all", 0);

                    String last_upadte=sf.getString("lastupdate", "0");

                    String last_updatehere=jObject.getString("lastupdate");


                    if(!last_upadte.equals(last_updatehere)||last_upadte.equals("0")) {

                        ArrayList<AllMagazineFilterObject> ls = new ArrayList<>();
                        JSONArray data = jObject.getJSONArray("data");

                        System.out.println("-data size-----------------------kkkkkkkkkkkkkkkkkkkkkkkkk--------->"+data.length());

                        for (int i = 0; i < data.length(); i++) {
                            AllMagazineFilterObject obj = new AllMagazineFilterObject();
                            JSONObject jo = data.getJSONObject(i);
                            obj.setId(jo.getString("id"));
                            obj.setName(jo.getString("name"));
                            /// convert url to byte
                            String uri = jo.getString("image");

                            SharedPreferences.Editor edit=sf.edit();
                            edit.putString(obj.getId() + "_link", jo.getString("extracted_file"));
                            edit.commit();

                            obj.setImage_url(uri);
                            obj.setContent(jo.getString("content"));
                            obj.setArticle(jo.getString("article"));
                            obj.setIntro(jo.getString("introduction"));

                            obj.setIssue_date(jo.getString("issued_date"));
                            obj.setZip_file(jo.getString("zip_file"));


                            String subscribed=jo.getString("subscribed");
                            String isfree=jo.getString("is_free");

                            obj.setSubscribed(subscribed);
                            obj.setIsFree(isfree);

                            System.out.println("------------subscribed-----999999--------------"+subscribed);
                            System.out.println("--------isfree---------999999--------------"+isfree);

                            ls.add(obj);


                        }

                        System.out.println("insertion--ls.size()---->" + ls.size());



                        //    ArrayList<AllMagazineFilterObject> fLs=new ArrayList<>();
                        horAdapter=new AllMagazineAdapterHorizontal1(HomeActivity.this,ls);
                        allMagazinesList.setAdapter(horAdapter);
                        listEmpty.setVisibility((!horAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
                        allMagazinesList.setEmptyView(findViewById(R.id.list_empty));

                        addNewDataToDatabase(ls, "1");
                        SharedPreferences.Editor edit=sf.edit();
                        edit.putString("lastupdate",last_updatehere);
                        edit.commit();
                    }

                    else{
                        callDatabaseInitial();
                    }





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


    public class GetMagazineAyncTaskFirstTime extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;
        int state;



        public GetMagazineAyncTaskFirstTime(Context context,int state) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            this.state=state;
            progress.setMessage("Loading...");

            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {

            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");


            this.url = "http://95.211.75.201/~travelnewsdm/api/get-all-releases?token="+token;

            System.out.println("-----this.url---------->"+this.url);



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
            progress.hide();
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                 JSONObject jObject=new JSONObject(result);


                System.out.println("--------------->"+jObject);

                String status=jObject.getString("status");

                if(status.equals("error")){}
                else{



                    JSONArray data=jObject.getJSONArray("data");
                    ArrayList<AllMagazineFilterObject> fLs=new ArrayList<>();
                    for(int i=0;i<data.length();i++) {
                        AllMagazineFilterObject obj = new AllMagazineFilterObject();
                        JSONObject jo = data.getJSONObject(i);
                        obj.setId(jo.getString("id"));
                        obj.setName(jo.getString("name"));
                        String uri = jo.getString("image");
                        obj.setImage_url(uri);
                        obj.setContent(jo.getString("content"));
                        obj.setArticle(jo.getString("article"));
                        obj.setIntro(jo.getString("introduction"));
                        obj.setIssue_date(jo.getString("issued_date"));
                        obj.setZip_file(jo.getString("zip_file"));
                        SharedPreferences sf=context.getSharedPreferences("all",0);
                        SharedPreferences.Editor edit=sf.edit();
                        edit.putString(obj.getId()+"_link",jo.getString("extracted_file"));
                        edit.commit();

                        fLs.add(obj);




                    }
                    //    ArrayList<AllMagazineFilterObject> fLs=new ArrayList<>();
                    horAdapter=new AllMagazineAdapterHorizontal1(HomeActivity.this,fLs);
                    allMagazinesList.setAdapter(horAdapter);
                    listEmpty.setVisibility((!horAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
                    allMagazinesList.setEmptyView(findViewById(R.id.list_empty));





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
    ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    String id,token,name,image,zip_file,extracted_file,introduction,article;

    public void startDownload(AllMagazineFilterObject obj) {
        currentObj=obj;
        release_id=currentObj.getId();

//        Toast.makeText(this,currentObj.getZip_file(),Toast.LENGTH_SHORT).show();
//        System.out.println("------------------------------------------>"+obj.getId());
        downloadMagazine=    new DownloadFileAsync();
        downloadMagazine.execute(obj.getZip_file());
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //   Toast.makeText(HomeActivity.this,"Canceled",Toast.LENGTH_LONG).show();
                        try {
                            downloadMagazine.cancel(true);
                        }
                        catch (Exception e){}
                    }
                });
                mProgressDialog.setMessage("Downloading Magazine.." + "\r\n"+" Tap to cancel");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
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
                System.out.println("-----------------------aurl[0]------------------->" + aurl[0]);
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());


                String directoryName="/sdcard/Download/.hidden/travelnews";
                new File(directoryName).mkdirs();

             OutputStream output = new FileOutputStream(directoryName+"/goodfile"+currentObj.getId()+".zip");

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
                    String directoryName="/sdcard/Download/.hidden/travelnews";
                    unzip(new File(directoryName +"/goodfile"+currentObj.getId()+".zip"), new File("/sdcard/Download/.hidden/travelnews/goodfile"+currentObj.getId()+"/"));
                }catch (Exception e){}
                SharedPreferences sf=HomeActivity.this.getSharedPreferences("all", 0);
                SharedPreferences.Editor edit=sf.edit();
                edit.putString(currentObj.getId(), currentObj.getId());
                edit.commit();
                horAdapter.notifyDataSetChanged();
                DownlaodStatAyncTask downlaodStatAyncTask=new DownlaodStatAyncTask(HomeActivity.this);
                downlaodStatAyncTask.execute();


            }
        }

        @Override
        protected void onPostExecute(String unused) {
            callDatabaseInitial();
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




    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }



    //////////////////////////////============for download statstatics================================================///////////////////////////////////
/////////////////////////////////////////-----------download stat-------------/////////////////////////////////////////////////////
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

    String lat="";
    String longi="";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
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
                    HomeActivity.this.startActivity(myIntent);
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


        }
    }

    public class DownlaodStatAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public DownlaodStatAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);


        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~ledernytt/api#registration

            String  geo_location=lat+","+longi;
            String device=getDeviceName();

            this.url = "http://95.211.75.201/~travelnewsdm/api/download-statistics?token="+token+"&release_id="+release_id+"&geo_location="+geo_location+"&device="+device+":android";



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

//                    JSONObject data=jObject.getJSONObject("data");
//
//                    SharedPreferences sf=HomeActivity.this.getSharedPreferences("all", 0);
//                    SharedPreferences.Editor edit=sf.edit();
//                    edit.putString("read_id",data.getString("statistics_id"));
//
//                    edit.commit();

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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    String release_id="";


}
