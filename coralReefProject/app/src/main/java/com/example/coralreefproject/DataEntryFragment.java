package com.example.coralreefproject;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

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
    private List<Uri> imagePathList;
    private FirebaseStorage storage;
    private DListener dListener;

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

        client = new OkHttpClient();
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
        TextView locationAccuracy = view.findViewById(R.id.textViewAccuracy);
        Spinner reefName = view.findViewById(R.id.spinnerReefName);
        Spinner coralName = view.findViewById(R.id.spinnerCoralNames);

        // Image Storage in Firebase
        storage = FirebaseStorage.getInstance(FirebaseApp.getInstance());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();


        Request request1 = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                        "&lon=" + longitude + "&units=imperial&appid=32ee5fcb98111e23101d37faee480ad4")
                .build();

        CoralEntry coralEntry = new CoralEntry();
        coralEntry.setWaterTurbidity("10");

        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray weather = jsonObject.getJSONArray("weather");
                    JSONObject main = jsonObject.getJSONObject("main");
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    JSONObject cloud = jsonObject.getJSONObject("clouds");

                    String airTemperature = main.getString("temp");
                    String windS = wind.getString("speed");
                    String windDegree = wind.getString("deg");
                    String cloudiness = cloud.getString("all");
                    String humid = main.getString("humidity");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            coralEntry.setAirTemp(airTemperature + " F");
                            coralEntry.setWindSpeed(windS + " mph");
                            coralEntry.setWindDirection(windDegree + "\ndegrees");
                            coralEntry.setCloudCover(cloudiness + "%");
                            coralEntry.setHumidity(humid + "%");
                        }
                    });


                    // Grabbing date and time in the correct format for API request
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDateTime now = LocalDateTime.now();
                    String date = dtf.format(now);
                    dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String time = dtf.format(now);
                    String validTime = date + "T" + time + "Z";

                    // air temp, water temp, humidity, cloud coverage, wind speed, wind direction, salinity
                    String params = "t_sea_sfc:F,salinity:psu,significant_wave_height:m";
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
                            .addHeader("Authorization", "Basic " + encoder.encodeToString("unccharlotte_hall:74DJtUYg4b".getBytes()))
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
                                    coralEntry.setUserName(user.getDisplayName());
                                    coralEntry.setDate(date + " " + time);
                                    coralEntry.setLatitude(String.format("%.2f", latitude));
                                    coralEntry.setLongitude(String.format("%.2f", longitude));
                                    coralEntry.setLocationAccuracy(String.valueOf(accuracy));

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
                                                if (value.equals("-666")) {
                                                    coralEntry.setWaterTemp("N/A");
                                                } else {
                                                    coralEntry.setWaterTemp(value + " F");
                                                }
                                                break;
                                            case 1:
                                                if (value.equals("-666")) {
                                                    coralEntry.setSalinity("N/A");
                                                } else {
                                                    coralEntry.setSalinity(value + " psu");
                                                }
                                                break;
                                            case 2:
                                                if (value.equals("-666")) {
                                                    coralEntry.setWaveHeight("N/A");
                                                } else {
                                                    coralEntry.setWaveHeight(value + " m");
                                                }
                                        }
                                    }

                                    if (getActivity() != null) {
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
                                                locationAccuracy.setText(coralEntry.getLocationAccuracy() + " m");
                                            }
                                        });
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupWaterTurb);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton10:
                        coralEntry.setWaterTurbidity("10 NTU");
                        break;
                    case R.id.radioButton25:
                        coralEntry.setWaterTurbidity("25 NTU");
                        break;
                    case R.id.radioButton50:
                        coralEntry.setWaterTurbidity("50 NTU");
                        break;
                    case R.id.radioButton100:
                        coralEntry.setWaterTurbidity("100 NTU");
                        break;
                    case R.id.radioButton250:
                        coralEntry.setWaterTurbidity("250 NTU");
                }
            }
        });

        view.findViewById(R.id.buttonUploadImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select images"),2);
            }
        });

        view.findViewById(R.id.buttonAddEntrySubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reefName.getSelectedItem() == null || reefName.getSelectedItem().toString().equals("")) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Choose a reef name")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }
                if (coralName.getSelectedItem() == null || coralName.getSelectedItem().equals("")) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Choose a coral name")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                coralEntry.setReefName(reefName.getSelectedItem().toString());
                coralEntry.setCoralName(coralName.getSelectedItem().toString());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                ArrayList<String> imagesUrl = new ArrayList<>();
                // Get a reference to the location where we'll store our photos
                StorageReference storageRef = storage.getReference("coral_images");

                if (imagePathList != null) {
                    for (int i = 0; i < imagePathList.size(); i++) {
                        StorageReference photoRef = storageRef.child(imagePathList.get(i).getLastPathSegment());
                        // Upload file to Firebase Storage
                        int count = i;
                        photoRef.putFile(imagePathList.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> downloadUrl = photoRef.getDownloadUrl();
                                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagesUrl.add(uri.toString());

                                        if (count == imagePathList.size() - 1) {
                                            coralEntry.setImages(imagesUrl);
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("userName", user.getDisplayName());
                                            data.put("userId", user.getUid());
                                            data.put("airTemp", coralEntry.getAirTemp());
                                            data.put("waterTemp", coralEntry.getWaterTemp());
                                            data.put("latitude", coralEntry.getLatitude());
                                            data.put("longitude", coralEntry.getLongitude());
                                            data.put("locationAccuracy", coralEntry.getLocationAccuracy());
                                            data.put("coralName", coralEntry.getCoralName());
                                            data.put("reefName", coralEntry.getReefName());
                                            data.put("date", coralEntry.getDate());
                                            data.put("cloudCoverage", coralEntry.getCloudCover());
                                            data.put("salinity", coralEntry.getSalinity());
                                            data.put("waveHeight", coralEntry.getWaveHeight());
                                            data.put("humidity", coralEntry.getHumidity());
                                            data.put("windDirection", coralEntry.getWindDirection());
                                            data.put("windSpeed", coralEntry.getWindSpeed());
                                            data.put("waterTurbidity", coralEntry.getWaterTurbidity());
                                            data.put("images", coralEntry.getImages());

                                            db.collection("entries")
                                                    .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Could not upload image!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userName", user.getDisplayName());
                    data.put("userId", user.getUid());
                    data.put("airTemp", coralEntry.getAirTemp());
                    data.put("waterTemp", coralEntry.getWaterTemp());
                    data.put("latitude", coralEntry.getLatitude());
                    data.put("longitude", coralEntry.getLongitude());
                    data.put("locationAccuracy", coralEntry.getLocationAccuracy());
                    data.put("coralName", coralEntry.getCoralName());
                    data.put("reefName", coralEntry.getReefName());
                    data.put("date", coralEntry.getDate());
                    data.put("cloudCoverage", coralEntry.getCloudCover());
                    data.put("salinity", coralEntry.getSalinity());
                    data.put("waveHeight", coralEntry.getWaveHeight());
                    data.put("humidity", coralEntry.getHumidity());
                    data.put("windDirection", coralEntry.getWindDirection());
                    data.put("windSpeed", coralEntry.getWindSpeed());
                    data.put("waterTurbidity", coralEntry.getWaterTurbidity());
                    data.put("images", coralEntry.getImages());

                    db.collection("entries")
                            .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                        }
                    });
                }
                dListener.goBackToHomeFragmentFromDataEntry();
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK  && data != null) {

            imagePathList = new ArrayList<>();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i=0; i<count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imagePathList.add(imageUri);
                }
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Success")
                        .setMessage("The images were successfully uploaded!")
                        .setPositiveButton("OK", null)
                        .show();
            }
            else if (data.getData() != null) {
                Uri imgUri = data.getData();
                imagePathList.add(imgUri);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Success")
                        .setMessage("The image was successfully uploaded!")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof DListener) {
            dListener = (DListener) context;
        } else {
            throw new IllegalStateException("MainActivity does not implement DListener");
        }
    }

    public interface DListener {
        void goBackToHomeFragmentFromDataEntry();
    }
}