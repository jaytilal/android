package com.example.jayti.hometowns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;


public class MapsViewActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher {

    String nickname, state, sta, country, cntry, city, add,year1;
    EditText year_input;
    ArrayList<String> userdata = new ArrayList<String>();
    int year,count, i, j;
    Double latitude, lat,longitude, longi;
    Spinner countrySpinner, stateSpinner;
    ArrayList<String> countries = new ArrayList<String>();
    ArrayList<String> states = new ArrayList<String>();
    ArrayList<User> data;
    private GoogleMap myMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsview_activity);
        year_input = (EditText) findViewById(R.id.year_input);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        country = "Select Country";
        state = "Select State";
        Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
        fetchUsers("Select Country", "Select State");
        fetchCountries();
        year_input.addTextChangedListener(this);
 }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
    }

    public void clearMap() {
        if (myMap != null) {
            myMap.clear();
        } else {
            Log.d("rew", "myMap is null");
        }
    }

    public void displaymarker(Double lat, Double longi) {
        LatLng userLoc = new LatLng(lat, longi);
        myMap.addMarker(new MarkerOptions().position(userLoc).title(nickname));
    }

    public void zoomTo(String cnt) {
        Geocoder locator = new Geocoder(MapsViewActivity.this);

        try {
            List<Address> address =
                    locator.getFromLocationName(cnt, 1);
            for (Address addressLocation : address) {
                if (addressLocation.hasLatitude())
                    lat = addressLocation.getLatitude();
                if (addressLocation.hasLongitude())
                    longi = addressLocation.getLongitude();
            }
        } catch (Exception error) {
            Log.e("rew", "Error", error);
        }
        LatLng zoomhere = new LatLng(lat, longi);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(zoomhere));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        menu.findItem(R.id.displayMap).setChecked(true);
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


    public void fetchUsers(String iCountry, String iState) {

        userdata.clear();
        clearMap();

        String url;
        int yearFinal;
        String yearSubFinal = year_input.getText().toString();
        if (yearSubFinal.equals("")) {
            yearFinal = 0;
        } else {
            yearFinal = Integer.valueOf(year_input.getText().toString());
        }

        if (iCountry == "Select Country") {
            url = "http://bismarck.sdsu.edu/hometown/users";
            if (yearFinal != 0) {
                url = "http://bismarck.sdsu.edu/hometown/users?year=" + yearFinal;
            }
        } else {

            url = "http://bismarck.sdsu.edu/hometown/users?country=" + iCountry;
            if (iState != "Select State") {
                try {
                    String encoded = URLEncoder.encode(iState, "UTF-8");
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + iCountry + "&state=" + encoded;
                    if (yearFinal != 0) {
                        url = "http://bismarck.sdsu.edu/hometown/users?country=" + iCountry + "&state=" + encoded + "&year=" + yearFinal;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                if (yearFinal != 0) {
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + iCountry + "&year=" + yearFinal;
                }
            }
        }


        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {

            public void onResponse(JSONArray response) {
                if (response != null) {
                    data = new ArrayList<User>();
                    data.clear();
                    for (i = 0; i < response.length(); i++) {
                        try {
                            userdata.add(response.getString(i));
                            JSONObject object = response.getJSONObject(i);
                            nickname = object.getString("nickname");
                            sta = object.getString("state");
                            year = object.getInt("year");
                            longitude = object.getDouble("longitude");
                            latitude = object.getDouble("latitude");
                            cntry = object.getString("country");
                            city = object.getString("city");

                            if (latitude == 0.0d && longitude == 0.0d) {
                                add = "" + city + ", " + sta + ", " + cntry;
                                Geocoder locator = new Geocoder(MapsViewActivity.this);
                                try {
                                    List<Address> address =
                                            locator.getFromLocationName(add, 1);
                                    for (Address addressLocation : address) {
                                        if (addressLocation.hasLatitude())
                                            latitude = addressLocation.getLatitude();
                                        if (addressLocation.hasLongitude())
                                            longitude = addressLocation.getLongitude();
                                    }
                                } catch (Exception error) {
                                    Log.e("rew", "Error", error);
                                }
                            }
                            data.add(new User(nickname, sta, cntry, city, year, latitude, longitude));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (i = 0; i < response.length(); i++) {
                        nickname = data.get(i).getNickname();
                        year = data.get(i).getYear();
                        displaymarker(data.get(i).getLatitude(), data.get(i).getLongitude());
                    }

                }
            }


        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };


        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(this).add(getRequest);


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        year1 = year_input.getText().toString();

        if (year1.equals("")) {
            fetchUsers(country, state);
        } else {
            int yearEntered = Integer.valueOf(year_input.getText().toString());
            if (yearEntered < 1970 || yearEntered > 2017) {
                year_input.setError("Enter year between 1970 and 2017!");
            } else {
                fetchUsers(country, state);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void fetchCountries() {
        countries.add("Select Country");
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        countries.add(response.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countrySpinner = (Spinner) findViewById(R.id.country_filter);
                countrySpinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,
                        countries));
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        try {

                            country = countries.get(position);
                            if (country.equals("Select Country")) {

                            } else {
                                zoomTo(country);
                                fetchStates();
                                fetchUsers(country, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    public void fetchStates() {
        states.clear();
        if (country == "Select Country") {
            states.add("Select State");
            stateSpinner = (Spinner) findViewById(R.id.state_filter);
            stateSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    states));
        } else {
            String url = "http://bismarck.sdsu.edu/hometown/states?country=" + country;
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    try {
                        states.add("Select State");
                        for (int i = 0; i < response.length(); i++) {
                            states.add(response.getString(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stateSpinner = (Spinner) findViewById(R.id.state_filter);
                    stateSpinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,
                            states));
                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try {
                                state = states.get(position);
                                fetchUsers(country, state);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            };
            JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
            VolleyQueue.instance(this).add(getRequest);
        }
    }
}