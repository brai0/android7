package com.example.seventhapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HashMap<String,String>>contactList;
    ListView listView;
    Button btnFetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList= new ArrayList<>();
        listView= findViewById(R.id.List);
        btnFetch= findViewById(R.id.fetch);

        btnFetch.setOnClickListener(view -> {
            try {
                Log.d("Button CLick","btnFetch");
                String UriContact = "https://api.androidhive.info/contacts/";
                new UrlHandler().execute(UriContact);
            } catch (Exception e) {
                Log.d("BtnError","Can't resolve");
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public class  UrlHandler extends AsyncTask<String,Integer,String>{
        /**
         * @deprecated
         */
        @Override
        protected String doInBackground(String... params) {
            String json_response;
            try {
                Log.d("OnPost","Post");
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                json_response = convertStreamToString(in);
                try {
                    JSONObject jsonObj = new JSONObject(json_response);

                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);// adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    Log.e("error", "Json parsing error: " + e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Button CLick","OnPost");
            ListAdapter adapter= new SimpleAdapter(MainActivity.this,contactList,R.layout.list_item,new  String[]{"id","name","email"},new int[]{R.id.cid,R.id.cname,R.id.cemail});
            listView.setAdapter(adapter);
        }
    }

}