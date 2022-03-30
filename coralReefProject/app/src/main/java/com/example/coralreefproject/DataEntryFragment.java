package com.example.coralreefproject;

import android.app.AlertDialog;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataEntryFragment extends Fragment {

    private static final String ARG_LOCATION = "location";

    private OkHttpClient client;
    private Location location;

    public DataEntryFragment() {
        // Required empty public constructor
    }

    public static DataEntryFragment newInstance(Location location) {
        DataEntryFragment fragment = new DataEntryFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = (Location) getArguments().getParcelable(ARG_LOCATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_entry, container, false);

        // textView on the screen that will change when data is received
        TextView waterTemp = view.findViewById(R.id.textViewShowWaterTemperature);
        TextView airTemp = view.findViewById(R.id.textViewShowAirTemp);
        TextView humidity = view.findViewById(R.id.textViewShowHumidity);
        TextView salinity = view.findViewById(R.id.textViewShowSalinity);
        TextView cloudCover = view.findViewById(R.id.textViewShowCloudCoverage);
        TextView windSpeed = view.findViewById(R.id.textViewShowWS);
        TextView windDirection = view.findViewById(R.id.textViewShowWD);
        TextView waveHeight = view.findViewById(R.id.textViewShowWaveHeight);
        TextView coordinates = view.findViewById(R.id.textViewShowCoordinates);


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();

        // Grabbing date and time in the correct format for API request
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = dtf.format(now);
        String validTime = date + "T" + time + "Z";

        // air temp, water temp, humidity, cloud coverage, wind speed, wind direction, salinity
        String params = "t_2m:F,t_sea_sfc:F,relative_humidity_2m:p,medium_cloud_cover:p," +
                "wind_speed_2m:mph,wind_dir_2m:d,salinity:psu,significant_wave_height:m";
        client = new OkHttpClient();
        // url call
        // For testing over water:
        // lat = 25.2175
        // lon = -80.214722
        String url = "https://api.meteomatics.com/" +
                validTime + "/" + params + "/" + latitude + "," + longitude + "/json";
        // encodes username and password for authentication
        Base64.Encoder encoder = Base64.getEncoder();
        // creates a request to API
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + encoder.encodeToString("univeristyofnorthcarolinaatcharlotte_hall:Co1AMR4mp3".getBytes()))
                .build();
        // call the request and puts the work in a new thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // if request fails, let user know
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // parse json data
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        // Get the current signed in user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        CoralEntry coralEntry = new CoralEntry();
                        coralEntry.setUserName(user.getDisplayName());
                        coralEntry.setDate(date + " " + time);
                        coralEntry.setLatitude(String.format("%.2f", latitude));
                        coralEntry.setLongitude(String.format("%.2f", longitude));

                        // parse data further
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject curObject = jsonArray.getJSONObject(i);
                            JSONArray param = curObject.getJSONArray("coordinates");
                            JSONObject coordinates = param.getJSONObject(0);
                            JSONArray dates = coordinates.getJSONArray("dates");
                            JSONObject valueObj = dates.getJSONObject(0);
                            String value = valueObj.getString("value");

                            // set values in object
                            switch (i) {
                                case 0:
                                    coralEntry.setAirTemp(value + " F");
                                    break;
                                case 1:
                                    if (value.equals("-666")) {
                                        coralEntry.setWaterTemp("N/A");
                                    } else {
                                        coralEntry.setWaterTemp(value + " F");
                                    }
                                    break;
                                case 2:
                                    coralEntry.setHumidity(value + "%");
                                    break;
                                case 3:
                                    coralEntry.setCloudCover(value + "%");
                                    break;
                                case 4:
                                    coralEntry.setWindSpeed(value + " mph");
                                    break;
                                case 5:
                                    coralEntry.setWindDirection(value + "\ndegrees");
                                    break;
                                case 6:
                                    if (value.equals("-666")) {
                                        coralEntry.setSalinity("N/A");
                                    } else {
                                        coralEntry.setSalinity(value + " psu");
                                    }
                                    break;
                                case 7:
                                    if (value.equals("-666")) {
                                        coralEntry.setWaveHeight("N/A");
                                    } else {
                                        coralEntry.setWaveHeight(value + " m");
                                    }
                            }
                        }

                        // update textViews on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waterTemp.setText(coralEntry.getWaterTemp());
                                airTemp.setText(coralEntry.getAirTemp());
                                humidity.setText(coralEntry.getHumidity());
                                cloudCover.setText(coralEntry.getCloudCover());
                                salinity.setText(coralEntry.getSalinity());
                                windDirection.setText(coralEntry.getWindDirection());
                                windSpeed.setText(coralEntry.getWindSpeed());
                                waveHeight.setText(coralEntry.getWaveHeight());
                                coordinates.setText("Lat: " + coralEntry.getLatitude() +
                                        "\nLon: " + coralEntry.getLongitude());
                            }
                        });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }
}