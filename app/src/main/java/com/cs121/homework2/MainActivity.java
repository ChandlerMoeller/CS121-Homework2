package com.cs121.homework2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainActivity extends AppCompatActivity {

    private Integer windGustMph;
    private Double tempF;
    private ObservationLocation observationLocation;
    private Double tempC;
    private String relativeHumidity;
    private String weather;
    private Integer dewpointC;
    private String windchillC;
    public String pressureMb;
    private String windchillF;
    private Integer dewpointF;
    private Double windMph;

    private String city;
    private String full;
    private String elevation;
    private String country;
    private String longitude;
    private String state;
    private String countryIso3166;
    private String latitude;

    public static String LOG_TAG = "MyApplication";

    String[] listitems = new String[15];

    ListView scrollview;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listitems);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://luca-teaching.appspot.com/weather/")
                .addConverterFactory(GsonConverterFactory.create())    //parse Gson string
                .client(httpClient)    //add logging
                .build();

        //Get data on startup
        contentRefresh(retrofit);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Refresh Content
                contentRefresh(retrofit);
            }
        });
    }

    public void contentRefresh(Retrofit retrofit) {
        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherResponse> queryResponseCall =
                service.getWeather(windGustMph, tempF, observationLocation, tempC, relativeHumidity,
                        weather, dewpointC, windchillC, pressureMb, windchillF, dewpointF, windMph,
                        city, full, elevation, country, longitude, state, countryIso3166, latitude);

        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Response<WeatherResponse> response) {
                View parentView = findViewById(R.id.mainrelativelayout);
                if (response.code() == 500) {
                    //Snackbar for 500 ERROR
                    makeErrorSnackbar(parentView, "Error Code 500");
                    return;
                }
                if (response.body().response.conditions == null) {
                    //Snackbar for Server Error
                    makeErrorSnackbar(parentView, "Server Error");
                    return;
                }
                if (response.body().response.result.equals("ok")) {
                    //Snackbar for on success
                    Snackbar.make(parentView, "Content Refreshed", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    //Update variables with new data
                    windGustMph = response.body().response.conditions.windGustMph;
                    tempF = response.body().response.conditions.tempF;
                    observationLocation = response.body().response.conditions.observationLocation;
                    tempC = response.body().response.conditions.tempC;
                    relativeHumidity = response.body().response.conditions.relativeHumidity;
                    weather = response.body().response.conditions.weather;
                    dewpointC = response.body().response.conditions.dewpointC;
                    windchillC = response.body().response.conditions.windchillC;
                    pressureMb = response.body().response.conditions.pressureMb;
                    windchillF = response.body().response.conditions.windchillF;
                    dewpointF = response.body().response.conditions.dewpointF;
                    windMph = response.body().response.conditions.windMph;
                    city = response.body().response.conditions.observationLocation.city;
                    full = response.body().response.conditions.observationLocation.full;
                    elevation = response.body().response.conditions.observationLocation.elevation;
                    country = response.body().response.conditions.observationLocation.country;
                    longitude = response.body().response.conditions.observationLocation.longitude;
                    state = response.body().response.conditions.observationLocation.state;
                    countryIso3166 = response.body().response.conditions.observationLocation.countryIso3166;
                    latitude = response.body().response.conditions.observationLocation.latitude;
                    //

                    //Make the list
                    listitems[0] = "The weather is " + weather;
                    listitems[1] = "Temp (Fahrenheit): " + tempF;
                    listitems[2] = "Temp (Celcius): " + tempC;
                    listitems[3] = "Wind gust (MPH): " + windGustMph;
                    listitems[4] = "Average wind speed (MPH): " + windMph;
                    listitems[5] = "Relative humidity: " + relativeHumidity;
                    listitems[6] = "Pressure (mb): " + pressureMb;
                    listitems[7] = "dewpoint (Fahrenheit): " + dewpointF;
                    listitems[8] = "dewpoint (Celcius): " + dewpointC;
                    listitems[9] = "Windchill (Fahrenheit): " + windchillF;
                    listitems[10] = "Windchill (Celcius): " + windchillC;
                    listitems[11] = "Your elevation: " + elevation;
                    listitems[12] = "Your longitude: " + longitude;
                    listitems[13] = "Your latitude: " + latitude;
                    listitems[14] = "Your elevation: " + elevation;
                    //

                    scrollview = (ListView) findViewById(R.id.scrollview);
                    scrollview.setAdapter(adapter);

                    TextView textview = (TextView) findViewById(R.id.toptextview);
                    String forecasttext = "Forecast for " + full;
                    textview.setText(forecasttext);
                } else {
                    //Snackbar for Other Error
                    makeErrorSnackbar(parentView, "Other Error");
                    return;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                Log.d("Error", "onFailure");
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void makeErrorSnackbar(View parentView, final String ErrorToast) {
        Snackbar.make(parentView, "ERROR", Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction("See Details", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(
                                MainActivity.this,
                                ErrorToast,
                                Toast.LENGTH_LONG).show();
                    }
                }).show();
    }


    public interface WeatherService {
        @GET("default/get_weather/")
        Call<WeatherResponse> getWeather(@Query("windGustMph") Integer windGustMph,
                                         @Query("temp_f") Double tempF,
                                         @Query("observation_location") ObservationLocation observationLocation,
                                         @Query("temp_c") Double tempC,
                                         @Query("relative_humidity") String relativeHumidity,
                                         @Query("weather") String weather,
                                         @Query("dewpoint_c") Integer dewpointC,
                                         @Query("windchill_c") String windchillC,
                                         @Query("pressure_mb") String pressureMb,
                                         @Query("windchill_f") String windchillF,
                                         @Query("dewpoint_f") Integer dewpointF,
                                         @Query("wind_mph") Double windMph,

                                         @Query("city") String city,
                                         @Query("full") String full,
                                         @Query("elevation") String elevation,
                                         @Query("country") String country,
                                         @Query("longitude") String longitude,
                                         @Query("state") String state,
                                         @Query("country_iso3166") String countryIso3166,
                                         @Query("latitude") String latitude
        );
    }
}


