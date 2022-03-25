package com.example.coralreefproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


        return view;
    }
}