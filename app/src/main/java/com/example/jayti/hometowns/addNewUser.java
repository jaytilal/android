package com.example.jayti.hometowns;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayti on 3/16/2017.
 */

public class addNewUser extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener {
    private static final int INTENT_PASS_STATE = 1;
    private Button done;
    Button setmap;
    Spinner countrySpinner,stateSpinner;
    ArrayList<String> countries  = new ArrayList<String>();
    ArrayList<String> states =new ArrayList<String>();
    private EditText nickname,city,latitude,longitude,password,year;
    String ACountryName,AState,ANickname,APassword,ACity;
    Long AYear;
    Double lati, longi;
    String userNickName;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.addUser).setChecked(true);
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

    @Override
    public void onFocusChange(View view, boolean Focus) {
        switch(view.getId()){
            case R.id.nickname_input:
                if(!Focus){
                    if( nickname.getText().toString().length()==0 ){
                        nickname.setError( "Please Nickname!" );
                    }
                    else{
                        userNickName=nickname.getText().toString();
                        dupCheckNickName();
                    }
                }
                break;
            case R.id.city_input:
                if (!Focus) {
                    if (city.getText().toString().length()==0) {
                        city.setError("Please enter City!");
                    }
                }
                break;
            case R.id.year_input:
                if(!Focus){
                    if(year.getText().toString().length()==0)
                        year.setError("Please enter Year!");
                    if(year.getText().toString().length()>0) {
                        int yearEntered=Integer.valueOf(year.getText().toString());
                        if (yearEntered < 1970 || yearEntered > 2017)
                            year.setError("Enter year between 1970 and 2017!");
                    }
                }
                break;

            case R.id.password_input:
                if (!Focus) {
                    if ( password.getText().toString().length() == 0) {
                        password.setError("Please enter Password!");
                    }
                    else if (password.getText().toString().length() < 3){
                        password.setError("Password should contain atleast 3 characters!");
                    }
                }
                break;



        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        Button button = (Button) findViewById(R.id.done_button);
        button.setOnClickListener(this);

        setmap = (Button)findViewById(R.id.setmap);
        setmap.setOnClickListener(this);

        nickname = (EditText)findViewById(R.id.nickname_input);
        nickname.setOnFocusChangeListener(this);
        city = (EditText)findViewById(R.id.city_input);
        city.setOnFocusChangeListener(this);
        year =(EditText)findViewById(R.id.year_input);
        year.setOnFocusChangeListener(this);
        password = (EditText)findViewById(R.id.password_input);
        password.setOnFocusChangeListener(this);
        latitude = (EditText)findViewById(R.id.latitude_display_label);
        latitude.setOnFocusChangeListener(this);
        longitude = (EditText)findViewById(R.id.longitude_display_label);
        longitude.setOnFocusChangeListener(this);
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if((viewToInt(year)<1970 && viewToInt(year)<2017)){
                    year.setError("Year in range 1970 - 2017");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

        });

        fetchCountries();
    }


    public void onClick(View button) {

        switch (button.getId()) {
            case R.id.done_button:
                    validate();

                break;

            case R.id.setmap:
                //(ACountryName!=null)||
                if((AState!=null)||(ACity!=null)) {
                    String add=""+ACity+", "+AState+", "+ACountryName;
                    Geocoder locator = new Geocoder(this);
                    try {
                        List<Address> address =
                                locator.getFromLocationName(add,1);
                        for (Address addressLocation: address) {
                            if (addressLocation.hasLatitude())
                                lati = addressLocation.getLatitude();
                            Log.i("rew", "Lat " + addressLocation.getLatitude());
                            if (addressLocation.hasLongitude())
                                longi = addressLocation.getLongitude();
    //                        Log.i("rew", "Long " + longi);
                        }
                    } catch (Exception error) {
                        Log.e("rew", "Address lookup Error", error);
                    }

                }
                else {
                    lati = 0.0d;
                    longi = 0.0d;
                    Log.i("rew", "Lat " + lati);
                    Log.i("rew", "Long " + longi);
                }

                Intent i=new Intent(this,MapsActivity.class);
                Bundle b= new Bundle();
                b.putDouble("Latitude",lati);
                b.putDouble("Longitude",longi);
                i.putExtras(b);
                startActivityForResult(i, 1);

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                lati=data.getDoubleExtra("latitude",0.00);
                lati = Double.valueOf(Math.round(lati*1000000))/1000000;
                longi=data.getDoubleExtra("longitude",0.00);
                longi = Double.valueOf(Math.round(longi*1000000))/1000000;
                latitude.setText(lati.toString());
                longitude.setText(longi.toString());

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void registerUser() throws JSONException {
        final String url = "http://bismarck.sdsu.edu/hometown/adduser";
        JSONObject data = new JSONObject();
        ANickname = nickname.getText().toString();
        ACity =city.getText().toString();
        APassword =password.getText().toString();
        AYear =Long.valueOf(year.getText().toString());

        data.put("nickname", ANickname);
        data.put("password", APassword);
        data.put("country", ACountryName);
        data.put("state",AState);
        data.put("city",ACity);
        data.put("year",AYear);



        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse (JSONObject response){
                Log.i("rew", response.toString());
                /*nickname.setText("");
                password.setText("");
                city.setText("");
                year.setText("");
                longitude.setText("");
                latitude.setText("");*/
            }

        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
 //               Log.i("Response","nickname"+ ANickname+"pass"+APassword+"Country"+ACountryName+"State"+AState+"city"+ACity+"year"+AYear);

            }
        };
        JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
        VolleyQueue.instance(this).add(postRequest);
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
    //                    Toast.makeText(getApplicationContext(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        ACountryName = parent.getSelectedItem().toString();

                        if (ACountryName.equals("Select Country")) {
                            states.add("Select State");
                            stateSpinner = (Spinner) findViewById(R.id.state_input);
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,states);

                            stateSpinner.setAdapter(dataAdapter);
                            fetchStates();

                        } else {

                            fetchStates();
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
        String url ="http://bismarck.sdsu.edu/hometown/states?country="+ACountryName;
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
      //                  Toast.makeText(getApplicationContext(),parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                        AState=parent.getSelectedItem().toString();
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

    public int getYear(String year){
        return Integer.parseInt(String.valueOf(year));
    }
    public String viewToString(View v){
        EditText view = (EditText) v;
        return view.getText().toString();
    }

    public int viewToInt(View v){
        EditText view = (EditText) v;
        return Integer.parseInt(view.getText().toString());
    }


    public void dupCheckNickName() {

        String url ="http://bismarck.sdsu.edu/hometown/nicknameexists?name="+userNickName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        if(response.startsWith("true")){
                            nickname.setError( "Nickname Already Exists!" );
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyQueue.instance(this).add(stringRequest);
    }
    public void validate(){
        if (viewToString(nickname).length() == 0) {
            nickname.requestFocus();
        }
        else if (viewToString(password).length() == 0) {
            password.requestFocus();
        } else if (viewToString(city).length() == 0) {
            city.requestFocus();
        } else if (viewToString(year).length() == 0) {
            year.requestFocus();
        } else if (countrySpinner.getSelectedItemPosition() == 0) {
            TextView i = (TextView) countrySpinner.getSelectedView();
            i.setError("Select Country");
            countrySpinner.requestFocus();
        } else if (stateSpinner.getSelectedItemPosition() == 0) {
            TextView i = (TextView) stateSpinner.getSelectedView();
            i.setError("Select State");
            stateSpinner.requestFocus();
        }else if (viewToString(year)!=null) {
            if (viewToInt(year) < 1970 || viewToInt(year) > 2017) {
                year.setError("Enter year between 1970 and 2017!");
            }
            else if(nickname.getError()== null && password.getError()== null && city.getError()== null
                    && year.getError()== null){
                try {
                    registerUser();

/*
                    nickname.setText("");
                    password.setText("");
                    city.setText("");
                    year.setText("");
                    longitude.setText("");
                    latitude
*/

                    Toast.makeText(this, "User Added!!", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Enter valid data!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}