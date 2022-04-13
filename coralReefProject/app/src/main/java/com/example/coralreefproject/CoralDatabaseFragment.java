package com.example.coralreefproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
    EntryAdapter.CListener cListener;
    SearchView searchView;

    public CoralDatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coral_database, container, false);
        setHasOptionsMenu(true);
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
        cListener = (EntryAdapter.CListener) ((MainActivity) getActivity());
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
                    String locationAccuracy = (String) entryInfo.get("locationAccuracy");
                    ArrayList<String> images = (ArrayList<String>) entryInfo.get("images");
                    String waveHeight = (String) entryInfo.get("waveHeight");
                    String windDirection = (String) entryInfo.get("windDirection");
                    String windSpeed = (String) entryInfo.get("windSpeed");
                    String humidity = (String) entryInfo.get("humidity");
                    String userId = (String) entryInfo.get("userId");

                    CoralEntry coralEntry = new CoralEntry(reefName, coralName, latitude, longitude, airTemp, waterTemp,
                            salinity, cloudCov, waterTurbidity, userName, images, documentSnapshot.getId(), date, locationAccuracy,
                            waveHeight, windDirection, windSpeed, humidity, userId);

                    entries.add(coralEntry);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0328F3"));
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity) getActivity()).setSupportTitle("Coral Entries");

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Entries");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
        }
        return super.onOptionsItemSelected(item);
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
            void goBackToHomeFragment();
        }
    }
}