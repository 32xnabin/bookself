package bookshelf.globdig.com.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends Activity{

    String sinup_params;

    EditText fname,lname,email,contact;
    Button register,cancel;
    String _fname,_lname,_email;
    TextView messageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();




    }


    public void findViews(){

        messageText=(TextView)findViewById(R.id.messageText);

        fname=(EditText)findViewById(R.id.fname);
        lname=(EditText)findViewById(R.id.lname);
        email=(EditText)findViewById(R.id.email);
        contact=(EditText)findViewById(R.id.contact);
        register=(Button)findViewById(R.id.sinup_register);
        cancel=(Button)findViewById(R.id.sinup_cancel);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUrlParametersAndRegister();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });

    }



    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
       // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void setUrlParametersAndRegister(){
        _fname=fname.getText().toString();
        _lname=lname.getText().toString();
        _email=email.getText().toString();
       // _contact=contact.getText().toString();

        if(_email.isEmpty()){

            Toast.makeText(this,"Email cannot be empty !",Toast.LENGTH_LONG).show();
        }else {

            sinup_params = "?first_name=" + _fname + "&last_name=" + _lname + "&email=" + _email;
            SinupAyncTask sinupAyncTask = new SinupAyncTask(this);
            sinupAyncTask.execute("");
        }
    }

    public class SinupAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public SinupAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();



                //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/register";
            //"http://95.211.75.201/~travelnewsdm/api/register?first_name="+_fname+"&last_name="+_lname+"&email="+_email"

            this.url=this.url+sinup_params;


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
                String status=jObject.getString("status");

                if(status.equals("error")){


                    messageText.setTextColor(Color.RED);}
                else{
                    {messageText.setTextColor(Color.GREEN);}

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }, 500);

                }
                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    messageText.setText(message);

                }catch (Exception e){
                    String msg=jObject.getString("message");
                    if(msg.contains("already")){
                        messageText.setText("Your email is already registered,if you forgot the password, press CANCEL and go to forgot password page");
                    }
                    else {
                        messageText.setText(jObject.getString("message"));
                    }
                }
          } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
}
