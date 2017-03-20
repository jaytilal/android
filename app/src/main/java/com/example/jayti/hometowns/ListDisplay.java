package com.example.jayti.hometowns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ListDisplay extends AppCompatActivity implements TextWatcher {

    ArrayList<String> users  = new ArrayList<String>();
    ArrayList<User>dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    Spinner countrySpinner,stateSpinner;
    ArrayList<String> countries  = new ArrayList<String>();
    ArrayList<String> states =new ArrayList<String>();
    private String ACountry,AState,AYear;
    EditText year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);
        fetchUsers("Select Country","Select State");
        year=(EditText)findViewById(R.id.year_input);
        year.addTextChangedListener(this);
        fetchCountries();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        menu.findItem(R.id.displayList).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUser:
                item.setChecked(true);
                Intent add = new Intent(this, addNewUser.class);
                startActivity(add);
                return true;

            case R.id.displayList:
                item.setChecked(true);
                Intent listDisplay = new Intent(this, ListDisplay.class);
                startActivity(listDisplay);
                return true;

            case R.id.displayMap:
                item.setChecked(true);
                Intent mapdisplay =new Intent(this,MapsViewActivity.class);
                startActivity(mapdisplay);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUsers(String country,String state) {
        users.clear();
        String url;
        int set_year;
        year=(EditText)findViewById(R.id.year_input);
        String year_input=year.getText().toString();
        if (year_input.equals("")){
            set_year = 0;
        }
        else{
            set_year = Integer.valueOf(year.getText().toString());
        }
        if (country=="Select Country"){
            url = "http://bismarck.sdsu.edu/hometown/users";
            if (set_year!=0)
                url = "http://bismarck.sdsu.edu/hometown/users?year=" +set_year;
        }else {
            url = "http://bismarck.sdsu.edu/hometown/users?country=" + country;
            if (state != "Select State") {
                try {
                    String encoded= URLEncoder.encode(state,"UTF-8");
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + country + "&state=" + encoded;
                    if (set_year!=0){
                        url = "http://bismarck.sdsu.edu/hometown/users?country=" + country + "&state=" + encoded + "&year=" +set_year;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else{
                if (set_year!=0){
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + country  + "&year=" +set_year;
                }
            }
        }

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    dataModels =new ArrayList<User>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Log.i("rew", response.getString(i));
                            JSONObject user_data =response.getJSONObject(i);
                            dataModels.add(new User(user_data.getString("nickname"),user_data.getString("country"),user_data.getString("state"),user_data.getString("city"),user_data.getInt("year"),user_data.getDouble("latitude"),user_data.getDouble("longitude")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listView=(ListView)findViewById(R.id.list);
                    CustomAdapter adapter = new CustomAdapter(dataModels, getBaseContext());
                    listView.setAdapter(adapter);

                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                //Log.i("Response","nickname"+ ANickname+"pass"+APassword+"Country"+ACountryName+"State"+AState+"city"+ACity+"year"+AYear);

            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }
    public void fetchCountries() {
        String url ="http://bismarck.sdsu.edu/hometown/countries";
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                countries.add("Select Country");
                if (response != null) {
                    for (int i=0;i<response.length();i++){
                        try {
                            countries.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                countrySpinner = (Spinner) findViewById(R.id.country_input);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,countries);

                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     //   Toast.makeText(getApplicationContext(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        ACountry = parent.getSelectedItem().toString();

                        if (ACountry.equals("Select Country")) {
                            states.add("Select State");

                        } else {

                            fetchStates();
                            fetchUsers(ACountry,"Select State");
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                countrySpinner.setAdapter(dataAdapter);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    public void fetchStates() {
        states.clear();
        states.add("Select State");
        String url ="http://bismarck.sdsu.edu/hometown/states?country="+ACountry;
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    for (int i=0;i<response.length();i++){
                        try {
                            states.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                stateSpinner = (Spinner) findViewById(R.id.state_input);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,states);

                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //            Toast.makeText(getApplicationContext(),parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                        AState=parent.getSelectedItem().toString();
                        fetchUsers(ACountry,AState);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                stateSpinner.setAdapter(dataAdapter);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AYear = year.getText().toString();
        if (AYear.equals("")) {
            fetchUsers(ACountry,AState);
        }
        else{
            int yearEntered = Integer.valueOf(year.getText().toString());
            if (yearEntered < 1970 || yearEntered > 2017) {
                year.setError("Enter year between 1970 and 2017!");
            }
            else {
                fetchUsers(ACountry,AState);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


}
