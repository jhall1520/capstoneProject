package com.example.coralreefproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Internal;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataAnalysisFragment extends Fragment {

    CheckBox selectAllCorals;
    CheckBox stagHorn;
    CheckBox elkHorn;

    CheckBox selectAllLocations;
    CheckBox craysFort;
    CheckBox coffinsPatch;
    CheckBox grecianRocks;
    CheckBox looeKey;
    CheckBox northDryRocks;
    CheckBox picklesReef;
    CheckBox sombreroReef;

    ArrayList<CoralEntry> craysFortEntries;

    Map<String, LineGraphSeries<DataPoint>> seriesMap;
    boolean isStaghorn = false;
    boolean isElkhorn = false;

    GraphView numCoralsLineChart;

    public DataAnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_analysis, container, false);

        craysFortEntries = new ArrayList<>();
        seriesMap = new HashMap<>();
        numCoralsLineChart = (GraphView) view.findViewById(R.id.numCoralsLineChart);


        selectAllCorals = view.findViewById(R.id.checkBoxSelectAllCoral);
        stagHorn = view.findViewById(R.id.checkBoxStaghorn);
        elkHorn = view.findViewById(R.id.checkBoxElkhorn);

        selectAllLocations = view.findViewById(R.id.checkBoxselectAllLoc);
        craysFort = view.findViewById(R.id.checkBoxCraysfort);
        coffinsPatch = view.findViewById(R.id.checkBoxCoffins);
        grecianRocks = view.findViewById(R.id.checkBoxGrecian);
        looeKey = view.findViewById(R.id.checkBoxLooe);
        northDryRocks = view.findViewById(R.id.checkBoxNorthDry);
        picklesReef = view.findViewById(R.id.checkBoxPickles);
        sombreroReef = view.findViewById(R.id.checkBoxSombrero);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        selectAllCorals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (selectAllCorals.isChecked()) {
                    stagHorn.setChecked(true);
                    elkHorn.setChecked(true);
                } else {
                    numCoralsLineChart.removeAllSeries();
                    stagHorn.setChecked(false);
                    elkHorn.setChecked(false);
                }
            }
        });

        selectAllLocations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (selectAllLocations.isChecked()) {
                    craysFort.setChecked(true);
                    coffinsPatch.setChecked(true);
                    grecianRocks.setChecked(true);
                    looeKey.setChecked(true);
                    northDryRocks.setChecked(true);
                    picklesReef.setChecked(true);
                    sombreroReef.setChecked(true);

                } else {
                    numCoralsLineChart.removeAllSeries();
                    seriesMap.clear();
                    craysFort.setChecked(false);
                    coffinsPatch.setChecked(false);
                    grecianRocks.setChecked(false);
                    looeKey.setChecked(false);
                    northDryRocks.setChecked(false);
                    picklesReef.setChecked(false);
                    sombreroReef.setChecked(false);
                }
            }
        });

        stagHorn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (stagHorn.isChecked()) {
                    isStaghorn = true;
                } else {
                    isStaghorn = false;
                }
            }
        });

        elkHorn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (elkHorn.isChecked()) {
                    isElkhorn = true;
                } else {
                    isElkhorn = false;
                }
            }
        });

        craysFort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (craysFort.isChecked()) {
                    if (isElkhorn && isStaghorn) {
                        db.collection("entries")
                                .whereEqualTo("reefName", craysFort.getText().toString())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataPoint[] dataPoints = new DataPoint[task.getResult().size()];
                                    int count = 0;
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        Map<String, Object> data = documentSnapshot.getData();CoralEntry entry = new CoralEntry();
                                        entry.setCoralName(data.get("coralName").toString());
                                        entry.setReefName(data.get("reefName").toString());
                                        entry.setAirTemp(data.get("airTemp").toString());
                                        entry.setWaterTemp(data.get("waterTemp").toString());
                                        entry.setCloudCover(data.get("cloudCoverage").toString());
                                        entry.setDocumentId(documentSnapshot.getId());
                                        entry.setHumidity(data.get("humidity").toString());
                                        entry.setLatitude(data.get("latitude").toString());
                                        entry.setLongitude(data.get("longitude").toString());
                                        entry.setLocationAccuracy(data.get("locationAccuracy").toString());
                                        entry.setSalinity(data.get("salinity").toString());
                                        entry.setUserName(data.get("userName").toString());
                                        entry.setWaterTurbidity(data.get("waterTurbidity").toString());
                                        entry.setWaveHeight(data.get("waveHeight").toString());
                                        entry.setWindDirection(data.get("windDirection").toString());
                                        entry.setWindSpeed(data.get("windSpeed").toString());
                                        entry.setDate(data.get("date").toString());
                                        entry.setImages((ArrayList<String>) data.get("images"));
                                        entry.setTime(data.get("time").toString());
                                        entry.setNumCorals(data.get("numCorals").toString());
                                        craysFortEntries.add(entry);

                                    }

                                    Collections.sort(craysFortEntries, new Comparator<CoralEntry>() {
                                        @Override
                                        public int compare(CoralEntry entry1, CoralEntry entry2) {
                                            String date1 = entry1.getDate() + " " + entry1.getTime();
                                            String date2 = entry2.getDate() + " " + entry2.getTime();
                                            return date1.compareTo(date2);
                                        }
                                    });

                                    for (CoralEntry coralEntry : craysFortEntries) {
                                        Date date = null;
                                        try {
                                            date = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(coralEntry.getDate() + " " + coralEntry.getTime());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        dataPoints[count] = new DataPoint(date, Integer.parseInt(coralEntry.getNumCorals()));
                                        count++;
                                    }
                                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                                    series.setTitle(craysFort.getText().toString());
                                    numCoralsLineChart.addSeries(series);
                                    numCoralsLineChart.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                                    numCoralsLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                                    numCoralsLineChart.getLegendRenderer().setVisible(true);
                                    numCoralsLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                                    numCoralsLineChart.getViewport().setMinX(dataPoints[0].getX());
                                    numCoralsLineChart.getViewport().setMaxX(dataPoints[dataPoints.length-1].getX());
                                    numCoralsLineChart.getViewport().setXAxisBoundsManual(true);
                                    //numCoralsLineChart.getGridLabelRenderer().setHumanRounding(false);
                                }
                            }
                        });
                    }
                } else {
                    Series series = seriesMap.get(craysFort.getText().toString());
                    seriesMap.remove(series);
                    numCoralsLineChart.removeSeries(series);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0328F3"));
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity) getActivity()).setSupportTitle("Data Analysis");
    }
}