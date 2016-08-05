package bookshelf.globdig.com.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class ForgotPswActivity extends AppCompatActivity {

    Button retrive,register;

    String forgot_params;

    EditText email;

    TextView messageText,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_psw);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        email=(EditText)findViewById(R.id.forgot_username);

        retrive=(Button)findViewById(R.id.retrivePassword);

        retrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordInEmail();
            }
        });

        register=(Button)findViewById(R.id.forgot_new_user_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPswActivity.this,MainActivity.class));

            }
        });

        messageText=(TextView)findViewById(R.id.forgot_message_text);
        login=(TextView)findViewById(R.id.forgot_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPswActivity.this,LoginActivity.class));
            }
        });
    }


    public void sendPasswordInEmail(){


        if(isNetworkAvailable(this)) {

            if(email.getText().toString().trim().isEmpty()){
                Toast.makeText(this,"Email cannot be empty !",Toast.LENGTH_LONG).show();

            }
            {

                forgot_params = "?user_name=" + email.getText();

                ForgotPasswordAyncTask forgotPasswordAyncTask = new ForgotPasswordAyncTask(this);
                forgotPasswordAyncTask.execute("");
            }
        }else{

            Toast.makeText(this,"Not internet connection !",Toast.LENGTH_LONG).show();
        }


    }

    public class ForgotPasswordAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public ForgotPasswordAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();



            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/forgot-password";
            this.url=this.url+forgot_params;
            Log.d("async date", "url==" + this.url);


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
                else{{messageText.setTextColor(Color.GREEN);}}
                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    messageText.setText(message);
                }catch (Exception e){


                    messageText.setText(jObject.getString("message"));
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
