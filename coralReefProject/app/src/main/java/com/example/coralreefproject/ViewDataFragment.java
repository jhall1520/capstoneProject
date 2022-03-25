package com.example.coralreefproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewDataFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ENTRY = "entry";

    private CoralEntry entry;

    public ViewDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param coralEntry Parameter 1.
     * @return A new instance of fragment ViewDataFragment.
     */
    public static ViewDataFragment newInstance(CoralEntry coralEntry) {
        ViewDataFragment fragment = new ViewDataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, coralEntry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entry = (CoralEntry) getArguments().getSerializable(ARG_ENTRY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_data, container, false);

        TextView reefName = view.findViewById(R.id.textViewShowReef);
        TextView coralName = view.findViewById(R.id.textViewShowCoral);
        TextView date = view.findViewById(R.id.textViewDatee);
        TextView coordinates = view.findViewById(R.id.textViewShowCoord);
        TextView airTemp = view.findViewById(R.id.textViewShowTemp);
        TextView waterTemp = view.findViewById(R.id.textViewShowWaterTemp);
        TextView salinity = view.findViewById(R.id.textViewShowSal);
        TextView waterTurb = view.findViewById(R.id.textViewShowWaterTurb);
        TextView cloudCoverage = view.findViewById(R.id.textViewShowCloud);
        TextView user = view.findViewById(R.id.textViewUser);

        reefName.setText(entry.getReefName());
        coralName.setText(entry.getCoralName());
        date.setText(entry.getDate());
        String coordinatesString = "Lat: " + entry.getLatitude() + " Lon: " + entry.getLongitude();
        coordinates.setText(coordinatesString);
        airTemp.setText(entry.getAirTemp() + " F");
        waterTemp.setText(entry.getWaterTemp() + " F");
        salinity.setText(entry.getSalinity());
        waterTurb.setText(entry.getTurbidity());
        cloudCoverage.setText(entry.getCloudCover() + "%");
        user.setText(entry.getUserName());

        return view;
    }
}