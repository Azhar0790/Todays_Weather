package com.Sarps.Weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.Sarps.Utility.CommonCode;
import com.Sarps.Utility.GPSTracker;
import com.Sarps.Utility.ImageLoader;
import com.Sarps.Utility.ServiceHandler;
import com.google.android.gms.maps.model.LatLng;
import com.weather.horizontal.AdapterMainList;
import com.weather.horizontal.HorizontalListView;

public class MainActivity extends Activity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_Temp_c, tv_temp_f, tv_Weather, tv_City, tv_time;
    private String str_temp_c, str_weather_s, str_img_url, str_city_name, str_time;
    private String str_wind_dir, str_wind_degrees, str_wind_mph, str_wind_kph, str_pressure_mb, str_rel_humidity;
    private TextView tv_wind_dir, tv_wind_degrees, tv_wind_mph, tv_wind_kph, tv_pressure_mb, tv_rel_humidity;

    private ImageView iv_wather, iv_search;
    private ImageLoader imageLoader;
    private EditText et_Search;
    private HorizontalListView horizontalList_main;
    private AdapterMainList mainListAdapter;
    private ArrayList<HashMap<String, String>> arrayList_weather;
    private HorizontalListView horizontalList;
    private CommonCode commonCode;
    private ProgressDialog mProgressDialog;

    private double latitude; // latitude
    private double longitude; // longitude
    GPSTracker gps;
    private String url_lat_long = "http://api.openweathermap.org/data/2.5/forecast?lat=20.0000&lon=73.7800";
    String w_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        String str = MainActivity.this.getResources().getString(R.string.test);
        if (str.equalsIgnoreCase("0"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /* all variable Declare in this method */
        init();


        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        w_url = "http://api.wunderground.com/api/bd4cad6b94606785/forecast10day/conditions/q/" + latitude + "," + longitude + ".json";

        System.out.println("w_url" + w_url);

        if (commonCode.checkInternet()) {
            // check if GPS enabled
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            new AsyncTaskParseJson().execute(w_url);
                                        }
                                    }
            );

        } else {
            Toast.makeText(MainActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
        }

        System.out.println("lat :-" + latitude);
        System.out.println("long :-" + longitude);
        // \n is for new line
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTaskParseJson().execute(w_url);
                et_Search.setText("");
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        iv_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);

                String strCity = et_Search.getText().toString().trim();
                if (strCity.length() > 2)
                    try {
                        if (commonCode.checkInternet()) {
                            if (Geocoder.isPresent()) {
                                try {
                                    String location = strCity;
                                    Geocoder gc = new Geocoder(MainActivity.this);
                                    List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                                    List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                    for (Address a : addresses) {
                                        if (a.hasLatitude() && a.hasLongitude()) {
                                            ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                            new AsyncTaskParseJson().execute("http://api.wunderground.com/api/bd4cad6b94606785/forecast10day/conditions/q/" + a.getLatitude() + "," + a.getLongitude() + ".json");
                                        }
                                    }
                                } catch (IOException e) {
                                    // handle the exception
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(MainActivity.this, "Please enter correct city name", Toast.LENGTH_SHORT).show();
            }
        });


        et_Search.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    String strCity = et_Search.getText().toString().trim();
                    if (strCity.length() > 2)
                        try {
                            if (commonCode.checkInternet()) {
                                if (Geocoder.isPresent()) {
                                    try {
                                        String location = strCity;
                                        Geocoder gc = new Geocoder(MainActivity.this);
                                        List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                                        List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                        for (Address a : addresses) {
                                            if (a.hasLatitude() && a.hasLongitude()) {
                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                                new AsyncTaskParseJson().execute("http://api.wunderground.com/api/bd4cad6b94606785/forecast10day/conditions/q/" + a.getLatitude() + "," + a.getLongitude() + ".json");
                                            }
                                        }
                                    } catch (IOException e) {
                                        // handle the exception
                                    }
                                }

//								new AsyncTaskParseJson() .execute("http://api.wunderground.com/api/bd4cad6b94606785/forecast10day/conditions/q/CA/" + URLEncoder.encode("" + strCity, "UTF-8") + ".json");
                            } else {
                                Toast.makeText(MainActivity.this, "Please check internet connection", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    else
                        Toast.makeText(MainActivity.this, "Please enter correct city name", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        imageLoader = new ImageLoader(MainActivity.this);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

	/*all variable Declaration */

    private void init() {
        tv_temp_f = (TextView) findViewById(R.id.tv_temp_f);
        tv_Temp_c = (TextView) findViewById(R.id.tv_temp_c);
        tv_Weather = (TextView) findViewById(R.id.tv_weather);
        tv_City = (TextView) findViewById(R.id.tv_city_name);
        iv_wather = (ImageView) findViewById(R.id.iv_img);
        et_Search = (EditText) findViewById(R.id.et_Search);

        tv_wind_dir = (TextView) findViewById(R.id.tv_wind_dir);
        tv_wind_degrees = (TextView) findViewById(R.id.tv_wind_degrees);
        tv_wind_mph = (TextView) findViewById(R.id.tv_wind_mph);
        tv_wind_kph = (TextView) findViewById(R.id.tv_wind_kph);
        tv_pressure_mb = (TextView) findViewById(R.id.tv_pressure_mb);
        tv_time = (TextView) findViewById(R.id.tv_time);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        horizontalList = (HorizontalListView) findViewById(R.id.customList);
        commonCode = new CommonCode(MainActivity.this);
    }


	/* AsyncTask for json parsing*/

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
        JSONObject dataJsonArr = null;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... arg0) {
            ServiceHandler sh = new ServiceHandler();
            System.out.println("URL: " + arg0[0]);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(arg0[0], ServiceHandler.GET);
            System.out.println("data" + jsonStr);

            return jsonStr;
        }

        /* Adding json data in string */
        @Override
        protected void onPostExecute(String result) {
            arrayList_weather = new ArrayList<HashMap<String, String>>();

            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    final JSONObject W_data = jsonObj.getJSONObject("current_observation");

                    str_temp_c = W_data.getString("temp_c");
                    str_rel_humidity = W_data.getString("relative_humidity");
                    str_weather_s = W_data.getString("weather");
                    str_img_url = W_data.getString("icon_url");

                    str_wind_dir = W_data.getString("wind_dir");
                    str_wind_degrees = W_data.getString("wind_degrees");
                    str_wind_mph = W_data.getString("wind_mph");
                    str_wind_kph = W_data.getString("wind_kph");
                    str_pressure_mb = W_data.getString("pressure_mb");
                    str_time = W_data.getString("local_time_rfc822");


                    JSONObject data = jsonObj.getJSONObject("current_observation").getJSONObject("display_location");
                    JSONObject data_2 = jsonObj.getJSONObject("current_observation").getJSONObject("observation_location");
                    System.out.println("new1" + data);
                    str_city_name = data_2.getString("city");

                    System.out.println("new2 " + str_city_name);

                    tv_Temp_c.setText(W_data.getString("temp_c") + "°C");
                    tv_temp_f.setText(W_data.getString("temp_f") + "°F");

                    tv_Weather.setText(str_weather_s);
                    tv_City.setText(data_2.getString("city") + ", " + data.getString("state") + ", " + data_2.getString("country"));
//					imageLoader.DisplayImage(str_img_url, iv_wather);
                    if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_partlycloudy.gif")) {
                        iv_wather.setImageResource(R.drawable.partly_cloud);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/clear.gif")) {
                        iv_wather.setImageResource(R.drawable.sun);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/partlycloudy.gif")) {
                        iv_wather.setImageResource(R.drawable.partly_cloud);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_clear.gif")) {
                        iv_wather.setImageResource(R.drawable.sun);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_mostlycloudy.gif")) {
                        iv_wather.setImageResource(R.drawable.partly_cloud);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_snow.gif")) {
                        iv_wather.setImageResource(R.drawable.partly_cloud);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/chancetstorms.gif")) {
                        iv_wather.setImageResource(R.drawable.storm);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_chancetstorms.gif")) {
                        iv_wather.setImageResource(R.drawable.storm);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/snow.gif")) {
                        iv_wather.setImageResource(R.drawable.snow);
                    } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/chancerain.gif")) {
                        iv_wather.setImageResource(R.drawable.rainy_cloud);
                    }
//                    Picasso.with(getApplicationContext()).load(str_img_url).into(iv_wather);
                    tv_wind_dir.setText(str_wind_dir);
                    tv_wind_degrees.setText(str_wind_degrees);
                    tv_wind_mph.setText(str_wind_mph);
                    tv_wind_kph.setText(str_wind_kph);
                    tv_pressure_mb.setText(str_pressure_mb);
                    tv_time.setText(str_time);


                    JSONArray jsonArray_forecast = jsonObj.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");

                    for (int i = 0; i < jsonArray_forecast.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        JSONObject jsonObject_forecastday = jsonArray_forecast.getJSONObject(i);

                        map.put("weekday", jsonObject_forecastday.getJSONObject("date").getString("weekday").substring(0, 3));
                        map.put("fahrenheit", jsonObject_forecastday.getJSONObject("high").getString("fahrenheit"));
                        map.put("celsius", jsonObject_forecastday.getJSONObject("high").getString("celsius"));
                        map.put("icon_url", jsonObject_forecastday.getString("icon_url"));

                        arrayList_weather.add(map);
                    }

                    System.out.println("Array List Size:- " + arrayList_weather.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            mainListAdapter = new AdapterMainList(MainActivity.this, arrayList_weather);
            horizontalList.setAdapter(mainListAdapter);
            swipeRefreshLayout.setRefreshing(false);


        }
    }
}



