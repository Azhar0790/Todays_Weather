package com.Sarps.Weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.Sarps.Utility.GPSTracker;
import com.Sarps.Utility.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azhar-sarps on 3/29/2017.
 */
public class WidgetMainActivity extends AppWidgetProvider {

    private String str_weather_s, str_img_url, str_city_name, str_time;
    private double latitude; // latitude
    private double longitude; // longitude
    GPSTracker gps;
    Context context;
    AppWidgetManager appWidgetManager;
    int[] appWidgetIds;
    ArrayList<String> lis;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {

            gps = new GPSTracker(context);
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }


            this.context = context;
            this.appWidgetIds = appWidgetIds;
            this.appWidgetManager = appWidgetManager;
            start();
            Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();
        }
    }

    private class View {
        RemoteViews remoteViews;
        ComponentName thisWidget;
        Bitmap image;

        public View(Context context, AppWidgetManager appWidgetManager, String city, String trmp_c, String trmp_f, String str_weather_s, String str_time, String imaeUri) {

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            thisWidget = new ComponentName(context, WidgetMainActivity.class);

            remoteViews.setTextViewText(R.id.tv_city_name, city);
            remoteViews.setTextViewText(R.id.tv_temp_c, trmp_c + "°C");
            remoteViews.setTextViewText(R.id.tv_temp_f, trmp_f + "°F");
            remoteViews.setTextViewText(R.id.tv_weather, str_weather_s);
            remoteViews.setTextViewText(R.id.tv_time, str_time);


            try {
                if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_partlycloudy.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.partly_cloud);

                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/clear.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.sun);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/partlycloudy.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.partly_cloud);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_clear.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.sun);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_mostlycloudy.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.partly_cloud);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_snow.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.partly_cloud);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/chancetstorms.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.storm);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/nt_chancetstorms.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.storm);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/snow.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.snow);
                } else if (str_img_url.equals("http://icons.wxug.com/i/c/k/chancerain.gif")) {
                    remoteViews.setImageViewResource(R.id.iv_img,R.drawable.rainy_cloud);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Picasso picasso = Picasso.with(context);
//            picasso.load(imaeUri).into(remoteViews, R.id.iv_img, appWidgetIds);

            Intent intent = new Intent(context,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pending = PendingIntent.getActivity(context, 0,intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.rel_middle,pending);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }

    public void start(){
        requestData("http://api.wunderground.com/api/bd4cad6b94606785/forecast10day/conditions/q/" + latitude + "," + longitude + ".json");
    }

    private void requestData(String uri) {
        AsyncTaskParseJson task = new AsyncTaskParseJson();
        task.execute(uri);
    }

    public void Init2() {
        new View(context, appWidgetManager, lis.get(0), lis.get(1), null, lis.get(2), lis.get(3), lis.get(4));
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            ServiceHandler sh = new ServiceHandler();
            System.out.println("URL: " + arg0[0]);
            String jsonStr = sh.makeServiceCall(arg0[0], ServiceHandler.GET);
            System.out.println("data" + jsonStr);
            return jsonStr;
        }

        /* Adding json data in string */
        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    final JSONObject W_data = jsonObj.getJSONObject("current_observation");

                    str_weather_s = W_data.getString("weather");
                    str_img_url = W_data.getString("icon_url");
                    str_time = W_data.getString("local_time_rfc822");


                    JSONObject data = jsonObj.getJSONObject("current_observation").getJSONObject("display_location");
                    JSONObject data_2 = jsonObj.getJSONObject("current_observation").getJSONObject("observation_location");
                    System.out.println("new1" + data);
                    str_city_name = data_2.getString("city");

                    System.out.println("new2 " + str_city_name);

                    lis = new ArrayList<>();
                    lis.add(data_2.getString("city"));
                    lis.add(W_data.getString("temp_c"));
                    lis.add(str_weather_s);
                    lis.add(str_time);
                    lis.add(str_img_url);

                    Init2();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

        }
    }


}