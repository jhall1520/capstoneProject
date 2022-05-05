package com.example.coralreefproject;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
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
    // CheckBoxes for Corals
    CheckBox selectAllCorals;
    CheckBox stagHorn;
    CheckBox elkHorn;

    // CheckBoxes for Locations
    CheckBox selectAllLocations;
    CheckBox craysFort;
    CheckBox coffinsPatch;
    CheckBox grecianRocks;
    CheckBox looeKey;
    CheckBox northDryRocks;
    CheckBox picklesReef;
    CheckBox sombreroReef;

    // Database Reference
    FirebaseFirestore db;

    // Formats Dates for charts
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Will hold queried data entries
    ArrayList<CoralEntry> entries;

    // Holds all series of datapoints
    Map<String, LineGraphSeries<DataPoint>> seriesMap;

    // All charts on screen
    GraphView numCoralsLineChart;
    GraphView waterTempLineChart;
    GraphView airTempLineChart;
    GraphView humidityLineChart;
    GraphView cloudCoverageLineChart;

    public DataAnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_analysis, container, false);

        // initializes entries and seriesMap
        entries = new ArrayList<>();
        seriesMap = new HashMap<>();

        // initializes all charts
        numCoralsLineChart = (GraphView) view.findViewById(R.id.numCoralsLineChart);
        waterTempLineChart = (GraphView) view.findViewById(R.id.waterTempLineChart);
        airTempLineChart = (GraphView) view.findViewById(R.id.airTempLineChart);
        humidityLineChart = (GraphView) view.findViewById(R.id.humidityLineChart);
        cloudCoverageLineChart = (GraphView) view.findViewById(R.id.cloudCovLineChart);

        // initializes all coral checkboxes
        selectAllCorals = view.findViewById(R.id.checkBoxSelectAllCoral);
        stagHorn = view.findViewById(R.id.checkBoxStaghorn);
        elkHorn = view.findViewById(R.id.checkBoxElkhorn);

        // initializes all location checkboxes
        selectAllLocations = view.findViewById(R.id.checkBoxselectAllLoc);
        craysFort = view.findViewById(R.id.checkBoxCraysfort);
        coffinsPatch = view.findViewById(R.id.checkBoxCoffins);
        grecianRocks = view.findViewById(R.id.checkBoxGrecian);
        looeKey = view.findViewById(R.id.checkBoxLooe);
        northDryRocks = view.findViewById(R.id.checkBoxNorthDry);
        picklesReef = view.findViewById(R.id.checkBoxPickles);
        sombreroReef = view.findViewById(R.id.checkBoxSombrero);

        // gets database reference
        db = FirebaseFirestore.getInstance();

        // puts a listener on the submit button
        view.findViewById(R.id.buttonSubmitChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coralName = "ALL";
                // if none of the coral checkboxes are checked
                if (!stagHorn.isChecked() && !elkHorn.isChecked()) {
                    Toast.makeText(getContext(), "Select Coral(s)", Toast.LENGTH_SHORT).show();
                    return;
                    // if none of the location checkboxes are checked
                } else if (!craysFort.isChecked() && !coffinsPatch.isChecked() && !grecianRocks.isChecked() &&
                        !looeKey.isChecked() && !northDryRocks.isChecked() && !picklesReef.isChecked() && !sombreroReef.isChecked()) {
                    Toast.makeText(getContext(), "Select Reef(s)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // reset all graphs
                numCoralsLineChart.removeAllSeries();
                waterTempLineChart.removeAllSeries();
                airTempLineChart.removeAllSeries();
                humidityLineChart.removeAllSeries();
                cloudCoverageLineChart.removeAllSeries();

                // if staghorn is checked and elkhorn is not, then we are querying only for staghorn
                if (stagHorn.isChecked() && !elkHorn.isChecked())
                    coralName = "Staghorn";
                    // if elkhorn is checked and staghorn is not, then we are querying only for elkhorn
                else if (!stagHorn.isChecked() && elkHorn.isChecked())
                    coralName = "Elkhorn";

                // if a location is checked, get its data from the database
                if (craysFort.isChecked())
                    getData(craysFort.getText().toString(), coralName, Color.BLUE);
                if (coffinsPatch.isChecked())
                    getData(coffinsPatch.getText().toString(), coralName, Color.BLACK);
                if (grecianRocks.isChecked())
                    getData(grecianRocks.getText().toString(), coralName, Color.RED);
                if (looeKey.isChecked())
                    getData(looeKey.getText().toString(), coralName, Color.GREEN);
                if (northDryRocks.isChecked())
                    getData(northDryRocks.getText().toString(), coralName, Color.CYAN);
                if (picklesReef.isChecked())
                    getData(picklesReef.getText().toString(), coralName, Color.YELLOW);
                if (sombreroReef.isChecked())
                    getData(sombreroReef.getText().toString(), coralName, Color.MAGENTA);
            }
        });

        // gives the selectAll checkbox functionality for corals
        selectAllCorals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (selectAllCorals.isChecked()) {
                    stagHorn.setChecked(true);
                    elkHorn.setChecked(true);
                } else {
                    stagHorn.setChecked(false);
                    elkHorn.setChecked(false);
                }
            }
        });

        // gives the selectAll checkbox functionality for locations
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sets up Title bar at the top of the screen
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0328F3"));
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity) getActivity()).setSupportTitle("Data Analysis");
    }

    public void getData(String reefName, String coralName, int color) {
        // if all coral names are selected then just query for the reef name
        if (coralName.equals("ALL")) {
            db.collection("entries")
                    .whereEqualTo("reefName", reefName)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // clear all previous queried entries
                        entries.clear();
                        // set up arrays that hold datapoints for each chart
                        DataPoint[] dataPointsNumCorals = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsWaterTemp = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsAirTemp = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsHumidity = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsCloudCov = new DataPoint[task.getResult().size()];
                        int count = 0;
                        // Each documentSnapshot contains all the data for one CoralEntry
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            // Get all data from documentSnap
                            Map<String, Object> data = documentSnapshot.getData();
                            CoralEntry entry = new CoralEntry();
                            entry.setCoralName(data.get("coralName").toString());
                            entry.setReefName(data.get("reefName").toString());
                            entry.setAirTemp(data.get("airTemp").toString().split("\u00B0")[0]);
                            entry.setWaterTemp(data.get("waterTemp").toString().split("\u00B0")[0]);
                            entry.setCloudCover(data.get("cloudCoverage").toString().split("%")[0]);
                            entry.setDocumentId(documentSnapshot.getId());
                            entry.setHumidity(data.get("humidity").toString().split("%")[0]);
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
                            // add the object into the arrayList
                            entries.add(entry);

                        }
                        // sort the arraylist by date and time in ascending order
                        Collections.sort(entries, new Comparator<CoralEntry>() {
                            @Override
                            public int compare(CoralEntry entry1, CoralEntry entry2) {
                                String date1 = entry1.getDate() + " " + entry1.getTime();
                                String date2 = entry2.getDate() + " " + entry2.getTime();
                                return date1.compareTo(date2);
                            }
                        });

                        // iterate through arraylist
                        for (CoralEntry coralEntry : entries) {
                            Date date = null;
                            try {
                                // this parses the date and time to be able to graph it on the chart
                                date = sdf.parse(coralEntry.getDate() + " " + coralEntry.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // add new datapoint and its Y value to the corresponding array
                            dataPointsNumCorals[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getNumCorals()));
                            if (!coralEntry.getWaterTemp().equals("N/A"))
                                dataPointsWaterTemp[count] = new DataPoint(date.getTime(), Double.parseDouble(coralEntry.getWaterTemp()));
                            dataPointsAirTemp[count] = new DataPoint(date.getTime(), Double.parseDouble(coralEntry.getAirTemp()));
                            dataPointsHumidity[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getHumidity()));
                            dataPointsCloudCov[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getCloudCover()));

                            count++;
                        }

                        // Initialize Series for number of Corals
                        LineGraphSeries<DataPoint> seriesNumCorals = new LineGraphSeries<>(dataPointsNumCorals);
                        seriesNumCorals.setTitle(reefName);
                        seriesNumCorals.setDrawDataPoints(true);
                        seriesNumCorals.setDataPointsRadius(10);
                        seriesNumCorals.setColor(color);
                        seriesNumCorals.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesNumCorals.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add num Corals series to the seriesMap
                        seriesMap.put(reefName, seriesNumCorals);
                        // set Y bounds
                        numCoralsLineChart.getViewport().setYAxisBoundsManual(true);
                        numCoralsLineChart.getViewport().setMinY(0);
                        numCoralsLineChart.getViewport().setMaxY(30);

                        // Initialize Series for water temp
                        LineGraphSeries<DataPoint> seriesWaterTemp = new LineGraphSeries<>(dataPointsWaterTemp);
                        seriesWaterTemp.setTitle(reefName);
                        seriesWaterTemp.setDrawDataPoints(true);
                        seriesWaterTemp.setDataPointsRadius(10);
                        seriesWaterTemp.setColor(color);
                        seriesWaterTemp.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesWaterTemp.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add water temp series to the seriesMap
                        seriesMap.put(reefName, seriesWaterTemp);
                        // set Y bounds
                        waterTempLineChart.getViewport().setYAxisBoundsManual(true);
                        waterTempLineChart.getViewport().setMinY(0);
                        waterTempLineChart.getViewport().setMaxY(100);

                        // Initializes SEries for Air Temp
                        LineGraphSeries<DataPoint> seriesAirTemp = new LineGraphSeries<>(dataPointsAirTemp);
                        seriesAirTemp.setTitle(reefName);
                        seriesAirTemp.setDrawDataPoints(true);
                        seriesAirTemp.setDataPointsRadius(10);
                        seriesAirTemp.setColor(color);
                        seriesAirTemp.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesAirTemp.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        // add air temp series to the Series map
                        seriesMap.put(reefName, seriesAirTemp);
                        // sets Y bounds
                        airTempLineChart.getViewport().setYAxisBoundsManual(true);
                        airTempLineChart.getViewport().setMinY(0);
                        airTempLineChart.getViewport().setMaxY(100);

                        // Initialize Series for humidity
                        LineGraphSeries<DataPoint> seriesHumidity = new LineGraphSeries<>(dataPointsHumidity);
                        seriesHumidity.setTitle(reefName);
                        seriesHumidity.setDrawDataPoints(true);
                        seriesHumidity.setDataPointsRadius(10);
                        seriesHumidity.setColor(color);
                        seriesHumidity.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesHumidity.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add humidity series to the seriesMap
                        seriesMap.put(reefName, seriesHumidity);
                        // set Y bounds
                        humidityLineChart.getViewport().setYAxisBoundsManual(true);
                        humidityLineChart.getViewport().setMinY(0);
                        humidityLineChart.getViewport().setMaxY(100);

                        // Initialize Series for cloud coverage
                        LineGraphSeries<DataPoint> seriesCloudCoverage = new LineGraphSeries<>(dataPointsCloudCov);
                        seriesCloudCoverage.setTitle(reefName);
                        seriesCloudCoverage.setDrawDataPoints(true);
                        seriesCloudCoverage.setDataPointsRadius(10);
                        seriesCloudCoverage.setColor(color);
                        seriesCloudCoverage.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesCloudCoverage.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add cloud coverage series to the seriesMap
                        seriesMap.put(reefName, seriesCloudCoverage);
                        // set Y bounds
                        cloudCoverageLineChart.getViewport().setYAxisBoundsManual(true);
                        cloudCoverageLineChart.getViewport().setMinY(0);
                        cloudCoverageLineChart.getViewport().setMaxY(100);

                        // get Min and Max date to set X bounds
                        if (entries.size() > 0) {
                            try {
                                Date minDate = sdf.parse(entries.get(0).getDate() + " " + entries.get(0).getTime());
                                Date maxDate = sdf.parse(entries.get(entries.size() - 1).getDate() + " "
                                        + entries.get(entries.size() - 1).getTime());

                                // enable setting manual bounds on graphs
                                numCoralsLineChart.getViewport().setXAxisBoundsManual(true);
                                waterTempLineChart.getViewport().setXAxisBoundsManual(true);
                                airTempLineChart.getViewport().setXAxisBoundsManual(true);
                                humidityLineChart.getViewport().setXAxisBoundsManual(true);
                                cloudCoverageLineChart.getViewport().setXAxisBoundsManual(true);

                                // if the bounds has not been set or the new date
                                if (numCoralsLineChart.getViewport().getMinX(true) == 0.0 ||
                                        numCoralsLineChart.getViewport().getMinX(true) > minDate.getTime()) {
                                    // set minDate boundary
                                    numCoralsLineChart.getViewport().setMinX(minDate.getTime());
                                    waterTempLineChart.getViewport().setMinX(minDate.getTime());
                                    airTempLineChart.getViewport().setMinX(minDate.getTime());
                                    humidityLineChart.getViewport().setMinX(minDate.getTime());
                                    cloudCoverageLineChart.getViewport().setMinX(minDate.getTime());
                                }

                                if (numCoralsLineChart.getViewport().getMaxX(true) == 0.0 ||
                                        numCoralsLineChart.getViewport().getMaxX(true) < maxDate.getTime()) {
                                    // set maxDate boundary
                                    numCoralsLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    waterTempLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    airTempLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    humidityLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    cloudCoverageLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // add numCorals series to the NumCorals chart
                            numCoralsLineChart.addSeries(seriesNumCorals);
                            // formats the date for the X axis
                            numCoralsLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up the chart
                            numCoralsLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            numCoralsLineChart.getLegendRenderer().setVisible(true);
                            numCoralsLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            numCoralsLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            numCoralsLineChart.getGridLabelRenderer().setVerticalAxisTitle("Num Corals");
                            numCoralsLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            numCoralsLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            numCoralsLineChart.getLegendRenderer().setFixedPosition(12, 30);

                            // add waterTemp series to the waterTemp chart
                            waterTempLineChart.addSeries(seriesWaterTemp);
                            // formats the date for the X axis
                            waterTempLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up the chart
                            waterTempLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            waterTempLineChart.getLegendRenderer().setVisible(true);
                            waterTempLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            waterTempLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            waterTempLineChart.getGridLabelRenderer().setVerticalAxisTitle("Num Corals");
                            waterTempLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            waterTempLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            waterTempLineChart.getLegendRenderer().setFixedPosition(12, 500);

                            // add airTemp series to airTemp chart
                            airTempLineChart.addSeries(seriesAirTemp);
                            // formats the dates for the X axis
                            airTempLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            airTempLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            airTempLineChart.getLegendRenderer().setVisible(true);
                            airTempLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            airTempLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            airTempLineChart.getGridLabelRenderer().setVerticalAxisTitle("Degrees Fahrenheit");
                            airTempLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            airTempLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            airTempLineChart.getLegendRenderer().setFixedPosition(12, 500);

                            // add humidity series to the humidity chart
                            humidityLineChart.addSeries(seriesHumidity);
                            // formats the dates for the X axis
                            humidityLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            humidityLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            humidityLineChart.getLegendRenderer().setVisible(true);
                            humidityLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            humidityLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            humidityLineChart.getGridLabelRenderer().setVerticalAxisTitle("Percentage");
                            humidityLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            humidityLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            humidityLineChart.getLegendRenderer().setFixedPosition(12, 560);


                            // add cloud coverage series to the cloud coverage chart
                            cloudCoverageLineChart.addSeries(seriesCloudCoverage);
                            // formats the dates for the X axis
                            cloudCoverageLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            cloudCoverageLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            cloudCoverageLineChart.getLegendRenderer().setVisible(true);
                            cloudCoverageLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            cloudCoverageLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            cloudCoverageLineChart.getGridLabelRenderer().setVerticalAxisTitle("Percentage");
                            cloudCoverageLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            cloudCoverageLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                        }
                    }
                }
            });
        } else {
            db.collection("entries")
                    .whereEqualTo("reefName", reefName)
                    .whereEqualTo("coralName", coralName)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // clear all previous queried entries
                        entries.clear();
                        // set up arrays that hold datapoints for each chart
                        DataPoint[] dataPointsNumCorals = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsWaterTemp = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsAirTemp = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsHumidity = new DataPoint[task.getResult().size()];
                        DataPoint[] dataPointsCloudCov = new DataPoint[task.getResult().size()];
                        int count = 0;
                        // Each documentSnapshot contains all the data for one CoralEntry
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            // Get all data from documentSnap
                            Map<String, Object> data = documentSnapshot.getData();
                            CoralEntry entry = new CoralEntry();
                            entry.setCoralName(data.get("coralName").toString());
                            entry.setReefName(data.get("reefName").toString());
                            entry.setAirTemp(data.get("airTemp").toString().split("\u00B0")[0]);
                            entry.setWaterTemp(data.get("waterTemp").toString().split("\u00B0")[0]);
                            entry.setCloudCover(data.get("cloudCoverage").toString().split("%")[0]);
                            entry.setDocumentId(documentSnapshot.getId());
                            entry.setHumidity(data.get("humidity").toString().split("%")[0]);
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
                            // add the object into the arrayList
                            entries.add(entry);

                        }

                        // sort the arraylist by date and time in ascending order
                        Collections.sort(entries, new Comparator<CoralEntry>() {
                            @Override
                            public int compare(CoralEntry entry1, CoralEntry entry2) {
                                String date1 = entry1.getDate() + " " + entry1.getTime();
                                String date2 = entry2.getDate() + " " + entry2.getTime();
                                return date1.compareTo(date2);
                            }
                        });

                        // iterate through arraylist
                        for (CoralEntry coralEntry : entries) {
                            Date date = null;
                            try {
                                // this parses the date and time to be able to graph it on the chart
                                date = sdf.parse(coralEntry.getDate() + " " + coralEntry.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // add new datapoint and its Y value to the corresponding array
                            dataPointsNumCorals[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getNumCorals()));
                            if (!coralEntry.getWaterTemp().equals("N/A"))
                                dataPointsWaterTemp[count] = new DataPoint(date.getTime(), Double.parseDouble(coralEntry.getWaterTemp()));
                            dataPointsAirTemp[count] = new DataPoint(date.getTime(), Double.parseDouble(coralEntry.getAirTemp()));
                            dataPointsHumidity[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getHumidity()));
                            dataPointsCloudCov[count] = new DataPoint(date.getTime(), Integer.parseInt(coralEntry.getCloudCover()));

                            count++;
                        }

                        // Initialize Series for number of Corals
                        LineGraphSeries<DataPoint> seriesNumCorals = new LineGraphSeries<>(dataPointsNumCorals);
                        seriesNumCorals.setTitle(reefName);
                        seriesNumCorals.setDrawDataPoints(true);
                        seriesNumCorals.setDataPointsRadius(10);
                        seriesNumCorals.setColor(color);
                        seriesNumCorals.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesNumCorals.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add num Corals series to the seriesMap
                        seriesMap.put(reefName, seriesNumCorals);
                        // set Y bounds
                        numCoralsLineChart.getViewport().setYAxisBoundsManual(true);
                        numCoralsLineChart.getViewport().setMinY(0);
                        numCoralsLineChart.getViewport().setMaxY(30);

                        // Initialize Series for water temp
                        LineGraphSeries<DataPoint> seriesWaterTemp = new LineGraphSeries<>(dataPointsWaterTemp);
                        seriesWaterTemp.setTitle(reefName);
                        seriesWaterTemp.setDrawDataPoints(true);
                        seriesWaterTemp.setDataPointsRadius(10);
                        seriesWaterTemp.setColor(color);
                        seriesWaterTemp.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesWaterTemp.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add water temp series to the seriesMap
                        seriesMap.put(reefName, seriesWaterTemp);
                        // set Y bounds
                        waterTempLineChart.getViewport().setYAxisBoundsManual(true);
                        waterTempLineChart.getViewport().setMinY(0);
                        waterTempLineChart.getViewport().setMaxY(100);

                        // Initializes SEries for Air Temp
                        LineGraphSeries<DataPoint> seriesAirTemp = new LineGraphSeries<>(dataPointsAirTemp);
                        seriesAirTemp.setTitle(reefName);
                        seriesAirTemp.setDrawDataPoints(true);
                        seriesAirTemp.setDataPointsRadius(10);
                        seriesAirTemp.setColor(color);
                        seriesAirTemp.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesAirTemp.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        // add air temp series to the Series map
                        seriesMap.put(reefName, seriesAirTemp);
                        // sets Y bounds
                        airTempLineChart.getViewport().setYAxisBoundsManual(true);
                        airTempLineChart.getViewport().setMinY(0);
                        airTempLineChart.getViewport().setMaxY(100);

                        // Initialize Series for humidity
                        LineGraphSeries<DataPoint> seriesHumidity = new LineGraphSeries<>(dataPointsHumidity);
                        seriesHumidity.setTitle(reefName);
                        seriesHumidity.setDrawDataPoints(true);
                        seriesHumidity.setDataPointsRadius(10);
                        seriesHumidity.setColor(color);
                        seriesHumidity.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesHumidity.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add humidity series to the seriesMap
                        seriesMap.put(reefName, seriesHumidity);
                        // set Y bounds
                        humidityLineChart.getViewport().setYAxisBoundsManual(true);
                        humidityLineChart.getViewport().setMinY(0);
                        humidityLineChart.getViewport().setMaxY(100);

                        // Initialize Series for cloud coverage
                        LineGraphSeries<DataPoint> seriesCloudCoverage = new LineGraphSeries<>(dataPointsCloudCov);
                        seriesCloudCoverage.setTitle(reefName);
                        seriesCloudCoverage.setDrawDataPoints(true);
                        seriesCloudCoverage.setDataPointsRadius(10);
                        seriesCloudCoverage.setColor(color);
                        seriesCloudCoverage.setThickness(8);

                        // This will let the user be able to get info from each dataPoint on the graph
                        seriesCloudCoverage.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(DataAnalysisFragment.this.getActivity(), "X: " + new Date((long) dataPoint.getX()).toString() + "\nY: " +
                                        dataPoint.getY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // add cloud coverage series to the seriesMap
                        seriesMap.put(reefName, seriesCloudCoverage);
                        // set Y bounds
                        humidityLineChart.getViewport().setYAxisBoundsManual(true);
                        humidityLineChart.getViewport().setMinY(0);
                        humidityLineChart.getViewport().setMaxY(100);

                        // get Min and Max date to set X bounds
                        if (entries.size() > 0) {
                            try {
                                Date minDate = sdf.parse(entries.get(0).getDate() + " " + entries.get(0).getTime());
                                Date maxDate = sdf.parse(entries.get(entries.size() - 1).getDate() + " "
                                        + entries.get(entries.size() - 1).getTime());

                                // enable setting manual bounds on graphs
                                numCoralsLineChart.getViewport().setXAxisBoundsManual(true);
                                waterTempLineChart.getViewport().setXAxisBoundsManual(true);
                                airTempLineChart.getViewport().setXAxisBoundsManual(true);
                                humidityLineChart.getViewport().setXAxisBoundsManual(true);
                                cloudCoverageLineChart.getViewport().setXAxisBoundsManual(true);

                                // if the bounds has not been set or the new date
                                if (numCoralsLineChart.getViewport().getMinX(true) == 0.0 ||
                                        numCoralsLineChart.getViewport().getMinX(true) > minDate.getTime()) {
                                    // set minDate boundary
                                    numCoralsLineChart.getViewport().setMinX(minDate.getTime());
                                    waterTempLineChart.getViewport().setMinX(minDate.getTime());
                                    airTempLineChart.getViewport().setMinX(minDate.getTime());
                                    humidityLineChart.getViewport().setMinX(minDate.getTime());
                                    cloudCoverageLineChart.getViewport().setMinX(minDate.getTime());
                                }

                                if (numCoralsLineChart.getViewport().getMaxX(true) == 0.0 ||
                                        numCoralsLineChart.getViewport().getMaxX(true) < maxDate.getTime()) {
                                    // set maxDate boundary
                                    numCoralsLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    waterTempLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    airTempLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    humidityLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);
                                    cloudCoverageLineChart.getViewport().setMaxX(maxDate.getTime() + 86400000 * 5);

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // add numCorals series to the NumCorals chart
                            numCoralsLineChart.addSeries(seriesNumCorals);
                            // formats the date for the X axis
                            numCoralsLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up the chart
                            numCoralsLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            numCoralsLineChart.getLegendRenderer().setVisible(true);
                            numCoralsLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            numCoralsLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            numCoralsLineChart.getGridLabelRenderer().setVerticalAxisTitle("Num Corals");
                            numCoralsLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            numCoralsLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            numCoralsLineChart.getLegendRenderer().setFixedPosition(12, 30);

                            // add waterTemp series to the waterTemp chart
                            waterTempLineChart.addSeries(seriesWaterTemp);
                            // formats the date for the X axis
                            waterTempLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up the chart
                            waterTempLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            waterTempLineChart.getLegendRenderer().setVisible(true);
                            waterTempLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            waterTempLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            waterTempLineChart.getGridLabelRenderer().setVerticalAxisTitle("Num Corals");
                            waterTempLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            waterTempLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            waterTempLineChart.getLegendRenderer().setFixedPosition(12, 360);

                            // add airTemp series to airTemp chart
                            airTempLineChart.addSeries(seriesAirTemp);
                            // formats the dates for the X axis
                            airTempLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            airTempLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            airTempLineChart.getLegendRenderer().setVisible(true);
                            airTempLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            airTempLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            airTempLineChart.getGridLabelRenderer().setVerticalAxisTitle("Degrees Fahrenheit");
                            airTempLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            airTempLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            airTempLineChart.getLegendRenderer().setFixedPosition(12, 580);

                            // add humidity series to the humidity chart
                            humidityLineChart.addSeries(seriesHumidity);
                            // formats the dates for the X axis
                            humidityLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            humidityLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            humidityLineChart.getLegendRenderer().setVisible(true);
                            humidityLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            humidityLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            humidityLineChart.getGridLabelRenderer().setVerticalAxisTitle("Degrees Fahrenheit");
                            humidityLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            humidityLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            humidityLineChart.getLegendRenderer().setFixedPosition(12, 580);


                            // add cloud coverage series to the cloud coverage chart
                            cloudCoverageLineChart.addSeries(seriesCloudCoverage);
                            // formats the dates for the X axis
                            cloudCoverageLineChart.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    if (isValueX) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        return sdf.format(new Date((long) value));
                                    } else
                                        return super.formatLabel(value, isValueX);
                                }
                            });
                            // sets up chart
                            cloudCoverageLineChart.getGridLabelRenderer().setNumHorizontalLabels(3);
                            cloudCoverageLineChart.getLegendRenderer().setVisible(true);
                            cloudCoverageLineChart.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                            cloudCoverageLineChart.getGridLabelRenderer().setHorizontalAxisTitle("Dates");
                            cloudCoverageLineChart.getGridLabelRenderer().setVerticalAxisTitle("Degrees Fahrenheit");
                            cloudCoverageLineChart.getGridLabelRenderer().setLabelHorizontalHeight(140);
                            cloudCoverageLineChart.getGridLabelRenderer().setHorizontalLabelsAngle(35);
                            cloudCoverageLineChart.getLegendRenderer().setFixedPosition(12, 580);
                        }
                    }
                }
            });
        }
    }
}