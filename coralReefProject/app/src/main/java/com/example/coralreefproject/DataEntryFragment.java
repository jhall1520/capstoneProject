package com.example.coralreefproject;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private OkHttpClient client;
    private LocationManager locationManager;

    public DataEntryFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_entry, container, false);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = dtf.format(now);
        String validTime = date + "T" + time + "Z";

        // air temp, humidity, cloud coverage, wind speed, wind direction
        // water temp was returning -666
        //t_sea_sfc:F
        // salinity was returning -666
        //salinity:psu
        String params = "t_2m:F,relative_humidity_2m:p,medium_cloud_cover:p," +
                "wind_speed_2m:mph,wind_dir_2m:d";
        client = new OkHttpClient();
        String url = "https://api.meteomatics.com/" +
                validTime + "/" + params + "/52.520551,13.461804/json";
        Base64.Encoder encoder = Base64.getEncoder();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + encoder.encodeToString("univeristyofnorthcarolinaatcharlotte_hall:Co1AMR4mp3".getBytes()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        CoralEntry coralEntry = new CoralEntry();
                        coralEntry.setUserName(user.getDisplayName());
                        coralEntry.setDate(date + " " + time);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject curObject = jsonArray.getJSONObject(i);
                            JSONArray param = curObject.getJSONArray("coordinates");
                            JSONObject coordinates = param.getJSONObject(0);
                            JSONArray dates = coordinates.getJSONArray("dates");
                            JSONObject valueObj = dates.getJSONObject(0);
                            String value = valueObj.getString("value");

                            switch (i) {
                                case 0:
                                    coralEntry.setAirTemp(value);
                                    break;
                                case 1:
                                    coralEntry.setHumidity(value);
                                    break;
                                case 2:
                                    coralEntry.setCloudCover(value);
                                    break;
                                case 3:
                                    coralEntry.setWindSpeed(value);
                                    break;
                                case 4:
                                    coralEntry.setWindDirection(value);
                            }
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }
}