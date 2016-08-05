package bookshelf.globdig.com.bookshelf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ChangeProfileActivity extends AppCompatActivity {

    EditText emailField,fNameField,lNameField,phoneField;
    Button changeP;
    ImageView back;

    String old_password,new_password;
    Button cancel_profile,update_profile;

    ProgressDialog progressDialog;
    String first_name;
    String last_name;
    String email;
    String phone;

    ProgressDialog progress;
    TextView backText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        emailField=(EditText)findViewById(R.id.profile_email);
        fNameField=(EditText)findViewById(R.id.profile_faname);
        lNameField=(EditText)findViewById(R.id.profile_laname);
        phoneField=(EditText)findViewById(R.id.profile_phone);
        changeP=(Button)findViewById(R.id.changePassword);

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
        cancel_profile=(Button)findViewById(R.id.profile_cancel);
        cancel_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        update_profile=(Button)findViewById(R.id.profile_update);

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_name = fNameField.getText().toString();
                last_name = lNameField.getText().toString();
                email = emailField.getText().toString();
                phone = (phoneField.getText().toString());
                if (email.trim().isEmpty()) {

                    Toast.makeText(ChangeProfileActivity.this, "email cannot be empty !", Toast.LENGTH_LONG).show();
                } else {
                    UpdateProfileAyncTask updateProfileAyncTask = new UpdateProfileAyncTask(ChangeProfileActivity.this);
                    updateProfileAyncTask.execute("");
                    GetProfileAyncTask getProfileAyncTask = new GetProfileAyncTask(ChangeProfileActivity.this);
                    getProfileAyncTask.execute("");
                }
            }
        });


    }



    public class GetProfileAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public GetProfileAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            progress=new ProgressDialog(ChangeProfileActivity.this);
            progress.setMessage("Loading...");
            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/get-user-detail-by-token?token="+token;

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
            // TODO Auto-generated method stub
            progress.hide();
            super.onPostExecute(result);
            try {

                JSONObject jObject = new JSONObject(result);
                System.out.println("--------------->"+jObject);

                String status=jObject.getString("status");

                if(status.equals("error")){}
                else{

                    JSONObject data=jObject.getJSONObject("data");

                    emailField.setText(data.getString("email"));
                    fNameField.setText(data.getString("first_name"));
                    lNameField.setText(data.getString("last_name"));
                    phoneField.setText(data.getString("contact_number"));

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


    public class UpdateProfileAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public UpdateProfileAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            progress.setMessage("Updating...");
            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all", 0);
            String token=sf.getString("token", "token");



            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/update-my-profile?first_name="+first_name+"&last_name="+last_name+"&email="+email+"&token="+token+"&contact_number="+phone;

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
            // TODO Auto-generated method stub
            progress.hide();
            super.onPostExecute(result);
            try {

                JSONObject jObject = new JSONObject(result);
                System.out.println("--------------->"+jObject);









            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
}
