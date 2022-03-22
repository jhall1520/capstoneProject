package com.example.coralreefproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CoralDatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoralDatabaseFragment extends Fragment {

    ArrayList<CoralEntry> entries;
    RecyclerView.LayoutManager layoutManager;
    EntryAdapter adapter;
    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CoralDatabaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CoralDatabaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoralDatabaseFragment newInstance(String param1, String param2) {
        CoralDatabaseFragment fragment = new CoralDatabaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coral_database, container, false);

        entries = new ArrayList<>();
        entries.add(new CoralEntry("Looe Key", "Staghorn", "80.90", "31.99",
                "78 F", "60 F", "Saline", "0%", "10", "Trevor", null));
        entries.add(new CoralEntry("Coffins Patch", "Elkhorn", "45.08", "67.89",
                "61 F", "73 F", "Fresh", "50%", "25", "Joel", null));
        entries.add(new CoralEntry("Pickles Reef", "Staghorn", "60.90", "71.99",
                "95 F", "80 F", "Brine", "90%", "250", "Jason", null));

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new EntryAdapter(entries);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        return view;
    }

    class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.CoralEntryViewHolder> {
        ArrayList<CoralEntry> entries;

        public EntryAdapter(ArrayList<CoralEntry> entries) {
            this.entries = entries;
        }

        @NonNull
        @Override
        public CoralEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coral_entry_layout, parent, false);
            return new CoralEntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CoralEntryViewHolder holder, int position) {
            CoralEntry coralEntry = entries.get(position);
            holder.reefName.setText(coralEntry.getReefName());
            holder.coralName.setText(coralEntry.getCoralName());
            holder.coordinates.setText("Lat: " + coralEntry.getLatitude() + "\nLon: " + coralEntry.getLongitude());
            holder.userName.setText(coralEntry.getUserName());
            if (coralEntry.getImages() == null)
                holder.numImages.setText("0");
            else
                holder.numImages.setText(coralEntry.getImages().length);
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        class CoralEntryViewHolder extends RecyclerView.ViewHolder {
            TextView reefName;
            TextView coralName;
            TextView coordinates;
            TextView userName;
            TextView numImages;

            public CoralEntryViewHolder(@NonNull View itemView) {
                super(itemView);
                reefName = itemView.findViewById(R.id.textViewShowReefName);
                coralName = itemView.findViewById(R.id.textViewShowCoralName);
                coordinates = itemView.findViewById(R.id.textViewShowCoords);
                userName = itemView.findViewById(R.id.textViewShowUserName);
                numImages = itemView.findViewById(R.id.textViewShowNumImages);

            }
        }
    }
}