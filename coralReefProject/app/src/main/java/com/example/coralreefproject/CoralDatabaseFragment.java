package com.example.coralreefproject;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CoralDatabaseFragment extends Fragment {

    ArrayList<CoralEntry> entries;
    RecyclerView.LayoutManager layoutManager;
    EntryAdapter adapter;
    RecyclerView recyclerView;

    public CoralDatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coral_database, container, false);

        entries = new ArrayList<>();
        getData();
//        entries.add(new CoralEntry("Looe Key", "Staghorn", "80.90", "31.99",
//                "78 F", "60 F", "Saline", "0%", "10", "Trevor", null));
//        entries.add(new CoralEntry("Coffins Patch", "Elkhorn", "45.08", "67.89",
//                "61 F", "73 F", "Fresh", "50%", "25", "Joel", null));
//        entries.add(new CoralEntry("Pickles Reef", "Staghorn", "60.90", "71.99",
//                "95 F", "80 F", "Brine", "90%", "250", "Jason", null));

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new EntryAdapter(entries, (MainActivity)getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entries").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Map<String, Object> entryInfo = documentSnapshot.getData();
                    String userName = (String) entryInfo.get("userName");
                    String date = (String) entryInfo.get("date");
                    String reefName = (String) entryInfo.get("reefName");
                    String coralName = (String) entryInfo.get("coralName");
                    String latitude = (String) entryInfo.get("latitude");
                    String longitude = (String) entryInfo.get("longitude");
                    String airTemp = (String) entryInfo.get("airTemp");
                    String waterTemp = (String) entryInfo.get("waterTemp");
                    String cloudCov = (String) entryInfo.get("cloudCoverage");
                    String salinity = (String) entryInfo.get("salinity");
                    String waterTurbidity = (String) entryInfo.get("waterTurbidity");

                    CoralEntry coralEntry = new CoralEntry(reefName, coralName, latitude, longitude, airTemp, waterTemp,
                            salinity, cloudCov, waterTurbidity, userName, null, documentSnapshot.getId(), date);

                    entries.add(coralEntry);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    static class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.CoralEntryViewHolder> {
        ArrayList<CoralEntry> entries;
        CListener cListener;

        public EntryAdapter(ArrayList<CoralEntry> entries, CListener cListener) {
            this.entries = entries;
            this.cListener = cListener;
        }

        @NonNull
        @Override
        public CoralEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coral_entry_layout, parent, false);
            return new CoralEntryViewHolder(view, cListener);
        }

        @Override
        public void onBindViewHolder(@NonNull CoralEntryViewHolder holder, int position) {
            CoralEntry coralEntry = entries.get(position);
            holder.coralEntry = coralEntry;
            holder.reefName.setText(coralEntry.getReefName());
            holder.coralName.setText(coralEntry.getCoralName());
            String coordinates = "Lat: " + coralEntry.getLatitude() + "\nLon: " + coralEntry.getLongitude();
            holder.coordinates.setText(coordinates);
            holder.userName.setText(coralEntry.getUserName());
            holder.date.setText(coralEntry.getDate());

        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        static class CoralEntryViewHolder extends RecyclerView.ViewHolder {
            TextView reefName;
            TextView coralName;
            TextView coordinates;
            TextView userName;
            TextView date;
            CoralEntry coralEntry;

            public CoralEntryViewHolder(@NonNull View itemView, CListener cListener) {
                super(itemView);
                reefName = itemView.findViewById(R.id.textViewShowReefName);
                coralName = itemView.findViewById(R.id.textViewShowCoralName);
                coordinates = itemView.findViewById(R.id.textViewShowCoords);
                userName = itemView.findViewById(R.id.textViewShowUserName);
                date = itemView.findViewById(R.id.textViewShowDate);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cListener.goToViewDataFragment(coralEntry);

                    }
                });
            }
        }

        public interface CListener {
            void goToViewDataFragment(CoralEntry entry);
        }
    }
}