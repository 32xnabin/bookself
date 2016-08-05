package bookshelf.globdig.com.bookshelf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity {

    CheckBox gift;
    LinearLayout payerLayot;
    List<String> p_code_list;
    List<String> p_name_list;
    ProgressDialog progress;

    EditText email,password,confirm_password,first_name,last_name,address,country_zip_code,city,phone,
            payer_email,payer_full_name,payer_address,payer_czipcode,payer_city,payer_phone;
    String _email,_password,_confirm_password,_first_name,_last_name,_address,_country_zip_code,_city,_phone,
            _payer_email,_payer_full_name,_payer_address,_payer_czipcode,_payer_city,_payer_phone;
    String _product_code;
    String subscribe_params;
    Button subscribeBtn;
    Spinner spinner1;
    ImageView back;
    TextView backText;
    TextView messsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
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
        messsage=(TextView)findViewById(R.id.messageSubs);



        email=(EditText)findViewById(R.id.email_sub);
        password=(EditText)findViewById(R.id.psw_sub);
        confirm_password=(EditText)findViewById(R.id.cpsw_sub);
        first_name=(EditText)findViewById(R.id.fname_sub);
        last_name=(EditText)findViewById(R.id.lname_sub);
        address=(EditText)findViewById(R.id.address_sub);
        country_zip_code=(EditText)findViewById(R.id.czipcode_sub);
        city=(EditText)findViewById(R.id.city_sub);
        phone=(EditText)findViewById(R.id.phone_sub);
        payer_email=(EditText)findViewById(R.id.email_payer);
        payer_full_name=(EditText)findViewById(R.id.fullName_payer);
        payer_address=(EditText)findViewById(R.id.address_payer);
        payer_czipcode=(EditText)findViewById(R.id.czipcode_sub);
        payer_city=(EditText)findViewById(R.id.city_payer);
        payer_phone=(EditText)findViewById(R.id.phone_payer);



        subscribeBtn=(Button)findViewById(R.id.subscribeButton);
        gift=(CheckBox)findViewById(R.id.chkgift);
        payerLayot=(LinearLayout)findViewById(R.id.papyerLayout);
        gift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                   payerLayot.setVisibility(View.VISIBLE);
                }
                else {
                    payerLayot.setVisibility(View.GONE);
                }

            }
        });

        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSubscription();
            }
        });
        addItemsOnSpinner();
        GetProfileAyncTask getProfileAyncTask=new GetProfileAyncTask(SubscriptionActivity.this);
        getProfileAyncTask.execute("");
    }

    public void addItemsOnSpinner() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        SubscribeTypeAyncTask subscribeTypeAyncTask=new SubscribeTypeAyncTask(this);
        subscribeTypeAyncTask.execute("");

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(SubscriptionActivity.this,p_code_list.get(position)+"",Toast.LENGTH_LONG).show();
                _product_code=p_code_list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void performSubscription(){

        if(isNetworkAvailable(this)){

            _email=email.getText().toString();
            _password=password.getText().toString();
            _confirm_password=confirm_password.getText().toString();
            _first_name=first_name.getText().toString();
            _last_name=last_name.getText().toString();
            _address=address.getText().toString();
            _country_zip_code=country_zip_code.getText().toString();
            _city=city.getText().toString();
            _phone=phone.getText().toString();
            _payer_email=payer_email.getText().toString();
            _payer_full_name=payer_full_name.getText().toString();
            _payer_address=payer_address.getText().toString();
            _payer_czipcode=payer_czipcode.getText().toString();
            _payer_city=payer_city.getText().toString();
            _payer_phone=payer_phone.getText().toString();

            if(_email.trim().isEmpty()||_password.trim().isEmpty()||_confirm_password.trim().isEmpty()||_first_name.trim().isEmpty()){
                messsage.setTextColor(Color.RED);
                messsage.setText("Email,Passwords,First Name,Last name,Address and zip code are compulsary !");

            }
            else if(!_password.equals(_confirm_password)){
                messsage.setTextColor(Color.RED);
                messsage.setText("Passwords do not match!");
            }
            else {

                SharedPreferences sf = this.getSharedPreferences("all", 0);
                String token = sf.getString("token", "token");

                subscribe_params = "?email=" + _email +
                        "&password=" + _first_name +
                        "&confirm-password=" + _first_name +
                        "&product_code=" + _product_code +
                        "&first_name=" + _first_name +
                        "&last_name=" + _last_name +
                        "&address=" + _address +
                        "&zip=" + _country_zip_code +
                        "&city=" + _city +
                        "&contact_number=" + _phone +
                        "&email_payer=" + _payer_email +
                        "&full_name=" + _payer_full_name +
                        "&pauyer_address=" + _payer_address +
                        "&payer_zip=" + _payer_czipcode +
                        "&payer_city=" + _payer_city +
                        "&payer_contact_number=" + _payer_phone +
                        "&token=" + token;


                SubscribeAyncTask subscribeAyncTask = new SubscribeAyncTask(SubscriptionActivity.this);
                subscribeAyncTask.execute("");
            }

        }else{

            Toast.makeText(this,"No internet connection !",Toast.LENGTH_LONG).show();
        }



    }

    public class GetProfileAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;




        public GetProfileAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            progress=new ProgressDialog(SubscriptionActivity.this);
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

                    email.setText(data.getString("email"));
                    first_name.setText(data.getString("first_name"));
                    last_name.setText(data.getString("last_name"));
                    phone.setText(data.getString("contact_number"));

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




    public class SubscribeAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public SubscribeAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);
            progress=new ProgressDialog(SubscriptionActivity.this);
            progress.setMessage("Loading...");
            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();



            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/subscribe";
            this.url=this.url+ subscribe_params;
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
            progress.hide();

            try {

                JSONObject jObject = new JSONObject(result);
                String status=jObject.getString("status");

                if(status.equals("error")){
                    messsage.setTextColor(Color.RED);
                    messsage.setText("Email already registered!");

                }

                else{{messsage.setTextColor(Color.GREEN);

                    messsage.setText("Sucessfully subscribed!");
                }}
                try {



                  //  messageText.setText(message);
                }catch (Exception e){


                 //   messageText.setText(jObject.getString("message"));
                }






            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    public class SubscribeTypeAyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String url;
        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;



        public SubscribeTypeAyncTask(Context context) {
            this.context = context;
            this.prefs = context.getSharedPreferences("DATETIME", 0);

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();



            //http://95.211.75.201/~travelnewsdm/api#registration
            this.url = "http://95.211.75.201/~travelnewsdm/api/get-product-code-list";
           // this.url=this.url+ subscribe_params;
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
                JSONArray ja=jObject.getJSONArray("data");
                 p_code_list=new ArrayList<>();
                 p_name_list=new ArrayList<>();

                for(int i=0;i<ja.length();i++){
                    JSONObject joo=ja.getJSONObject(i);
                    p_code_list.add(joo.getString("product_code"));
                    p_name_list.add(joo.getString("title"));

                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SubscriptionActivity.this,
                        android.R.layout.simple_spinner_item, p_name_list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(dataAdapter);




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
