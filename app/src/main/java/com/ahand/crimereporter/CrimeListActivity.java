package com.ahand.crimereporter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class CrimeListActivity extends Activity{

    protected String lat;
    protected String lng;
    protected String start;
    protected String end;
    protected String apiUrl;
    protected SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimelist);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        start = prefs.getString("start", "");
        end = prefs.getString("end", "");
        apiUrl = "https://jgentes-Crime-Data-v1.p.mashape.com/crime?enddate="+urlEncode(end)+latLong()+"startdate="+urlEncode(start);

        //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_SHORT).show();

        //new getMash().execute("https://jgentes-Crime-Data-v1.p.mashape.com/crime?enddate=6%2F25%2F2016&lat=42.343060293817736&long=-83.0579091956167&startdate=6%2F19%2F2010");
        //new getMash().execute("https://jgentes-Crime-Data-v1.p.mashape.com/crime?enddate=6%2F25%2F2019&lat=42.343060293817736&long=-83.0579091956167&startdate=6%2F19%2F2010");

        new getMash().execute(apiUrl);


    }

    private String latLong(){
        GPSTracker gp = new GPSTracker(this);

        if(gp.isGPSEnabled && gp.canGetLocation) {
            lat = String.valueOf(gp.getLatitude());
            lng = String.valueOf(gp.getLongitude());
            return "&lat="+lat+"&long="+lng+"&";
        }
        else{gp.showSettingsAlert();return null;}

    }
    private String urlEncode(String apiUrl) {
        try {
            return URLEncoder.encode(apiUrl, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    private class getMash extends AsyncTask <String , Integer, String>{
        HttpURLConnection urlConnection;
        ProgressDialog dialog = new ProgressDialog(CrimeListActivity.this);
        ArrayList<String> json;
        String mash = "X-Mashape-Key";
        String mashKey = "yFY51uVfPPmsh8qLYHSSMagtn1YQp134I8IjsnMcqhu4M6f4jU";

        //start progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("--","onPreExecute");

            dialog.setMessage("Finding Criminal Activity In Your Area...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("--",String.valueOf(params[0]));
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                Log.d("--", String.valueOf(url));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty(mash, mashKey);

                Log.d("--", String.valueOf(urlConnection.getResponseCode()));


                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();

            }

            Log.d("--",response.toString());
            return response.toString();
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            json = new ArrayList<>();
            try {
                json = jsonStringToArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("--", "onPostExecute");
            if(json.isEmpty()){json.add("No Crime to report.");}
            ListView lv = (ListView) findViewById(R.id.listView);
            ListAdapter la = new ArrayAdapter<>(CrimeListActivity.this, android.R.layout.simple_list_item_1, json);
            lv.setAdapter(la);
            dialog.dismiss();

        }
    }
    private ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject ja = jsonArray.getJSONObject(i);
            //Toast.makeText(getApplicationContext(),ja.getString("description"),Toast.LENGTH_LONG).show();
            stringArray.add(ja.getString("description"));
        }

        return stringArray;
    }

}
