package bookshelf.globdig.com.bookshelf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldP,newP,newP1;
    Button changeP;
    ImageView back;

    String old_password,new_password,new_password1;

    ProgressDialog progressDialog;
    TextView messageTxt;
    TextView backText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldP=(EditText)findViewById(R.id.oldPassword);
        newP=(EditText)findViewById(R.id.newPassword);
        newP1=(EditText)findViewById(R.id.confirmNewPassword);
        changeP=(Button)findViewById(R.id.changePassword);
        messageTxt=(TextView)findViewById(R.id.messageTxtCp);

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
        changeP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_password=oldP.getText().toString();
                new_password=newP.getText().toString();
                old_password=oldP.getText().toString();
                new_password1=newP1.getText().toString();

                if(old_password.trim().isEmpty()||new_password.trim().isEmpty()||new_password1.trim().isEmpty()){

                    messageTxt.setVisibility(View.VISIBLE);
                    messageTxt.setText("No values can be empty here!");
                }
                else if(!new_password.equals(new_password1)){
                    messageTxt.setVisibility(View.VISIBLE);
                    messageTxt.setText("New passwords do not match !");
                }
                else {
                    ChangePasswordAyncTask changePasswordAyncTask = new ChangePasswordAyncTask(ChangePasswordActivity.this);
                    changePasswordAyncTask.execute("");
                }
            }
        });
    }


    public class ChangePasswordAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public ChangePasswordAyncTask(Context context) {
            progressDialog=new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.setMessage("Requesting...");
            progressDialog.show();
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            SharedPreferences sf=context.getSharedPreferences("all",0);
            String token=sf.getString("token","token");

            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/change-password?token="+token+"&old-password="+old_password+"&new-password="+new_password;
          //  this.url=this.url+sinup_params;


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




                try {

                    JSONObject mObj = jObject.getJSONObject("message");
                    JSONArray ma=mObj.getJSONArray("email");

                    String message=ma.get(0)+"";

                    Toast.makeText(ChangePasswordActivity.this,"Password Changed",Toast.LENGTH_LONG).show();

                  //  messageText.setText(message);
                }catch (Exception e){


                   // messageText.setText(jObject.getString("message"));
                }

                if(!status.equals("error")){
                    SharedPreferences sf=context.getSharedPreferences("all", 0);
                    SharedPreferences.Editor edit=sf.edit();
                    edit.putString("logout","true");
                    edit.commit();
                    startActivity(new Intent(ChangePasswordActivity.this,LoginActivity.class));
                    finish();
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.hide();

        }


    }
}
