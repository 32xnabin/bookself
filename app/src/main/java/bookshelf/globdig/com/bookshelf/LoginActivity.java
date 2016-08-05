package bookshelf.globdig.com.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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

public class LoginActivity extends Activity {

    Button login,register;
    EditText username,password;
    TextView forgotPassword,messageText;
    String _username,_password,sinup_params;


    public void setScreenWidthDynamically(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);

        screenInches=  (double)Math.round(screenInches * 10) / 10;

        int sc=(int)screenInches;

        System.out.println("----------screenInches--------------->" + screenInches);
        System.out.println("----------sc--------------->" + sc);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        int tt=0;

        if(sc<=6){tt=4;}
        else if(sc>6&&sc>=10){tt=10;}
        else{tt=13;}

        switch (tt){

            case 4:
                layoutParams.setMargins(48, 48, 48, 48);
                break;
            case 10:

                layoutParams.setMargins(200, 200, 200, 200);
                break;
            case 13:
                layoutParams.setMargins(400, 200, 400, 200);
                break;
                default:
                    layoutParams.setMargins(400, 400, 400, 400);
                    break;


        }


        RelativeLayout rl=(RelativeLayout)findViewById(R.id.content_login);

        rl.setLayoutParams(layoutParams);


    }
    public void setScreenWidthDynamically1(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);

        screenInches=  (double)Math.round(screenInches * 10) / 10;

        int sc=(int)screenInches;

        System.out.println("----------screenInches--------------->" + screenInches);
        System.out.println("----------sc--------------->" + sc);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        int tt=0;

        if(sc<=6){tt=4;}
        else if(sc>6&&sc>=10){tt=10;}
        else{tt=13;}

        switch (tt){

            case 4:
                layoutParams.setMargins(48, 48, 48, 48);
                break;
            case 10:

                layoutParams.setMargins(400, 200, 400, 200);
                break;
            case 13:
                layoutParams.setMargins(500, 200, 500, 200);
                break;
            default:
                layoutParams.setMargins(600, 400, 600, 400);
                break;


        }


        RelativeLayout rl=(RelativeLayout)findViewById(R.id.content_login);

        rl.setLayoutParams(layoutParams);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//       orientationListener.disable();
    }

    OrientationEventListener orientationListener;


    public int getDefaultOrientaion(){

        int res=0;



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
      double  wPix = dm.widthPixels;
      double  hPix = dm.heightPixels;
        if(wPix>hPix){res=0;}
        else {res=1;}
        return  res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);

        messageText=(TextView)findViewById(R.id.message_text);
        forgotPassword=(TextView)findViewById(R.id.forgot_password);



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPswActivity.class));
            }
        });

        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUrlParametersAndRegister();

            }
        });

        register=(Button)findViewById(R.id.new_user_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));

            }
        });


        SharedPreferences sf=this.getSharedPreferences("all", 0);
         String logout=sf.getString("logout","true");

        if(logout.equals("false")){

            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }


    }



    @Override
    public void onBackPressed() {
        finish();
    }

    public void setUrlParametersAndRegister(){
        _username=username.getText().toString();
        _password=password.getText().toString();
//        _username="nabinwell@gmail.com";
//        _password="nabin";

        // _contact=contact.getText().toString();
        if(isNetworkAvailable(this)) {

            sinup_params = "?username=" + _username + "&email=" + _username + "&password=" + _password;
            LoginAyncTask loginAyncTask = new LoginAyncTask(this);
            loginAyncTask.execute("");
        }else{

            Toast.makeText(this,"No internet Connection !",Toast.LENGTH_SHORT).show();
        }
    }

    public class LoginAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public LoginAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();



            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/login";
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

                if(status.equals("error")){messageText.setTextColor(Color.RED);}
                else{{messageText.setTextColor(Color.GREEN);}


                    SharedPreferences sf=context.getSharedPreferences("all",0);
                    SharedPreferences.Editor edit =sf.edit();

                    JSONObject data=jObject.getJSONObject("data");
                    String token=data.getString("token");
                    edit.putString("token",token);
                    edit.commit();
                }


                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    messageText.setText(message);
                }catch (Exception e){


                    messageText.setText(jObject.getString("message"));
                }

                if(!status.equals("error")){
                    SharedPreferences sf=context.getSharedPreferences("all", 0);
                    SharedPreferences.Editor edit=sf.edit();
                    edit.putString("logout","false");
                    edit.commit();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
