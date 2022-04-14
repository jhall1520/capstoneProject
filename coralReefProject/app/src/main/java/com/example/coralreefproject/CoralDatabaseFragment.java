package com.example.coralreefproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class CoralDatabaseFragment extends Fragment {

    ArrayList<CoralEntry> entries;
    RecyclerView.LayoutManager layoutManager;
    EntryAdapter adapter;
    RecyclerView recyclerView;
    EntryAdapter.CListener cListener;
    SearchView searchView;
    String[] categories = {"Reef Name", "Coral Name", "Entered By", "Date"};
    ArrayAdapter<String> adapterCat;
    TextView noResults;

    public CoralDatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coral_database, container, false);
        adapterCat = new ArrayAdapter<String>(getActivity(), R.layout.dropdown_layout, categories);
        setHasOptionsMenu(true);

        entries = new ArrayList<>();
        getData();

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        cListener = (EntryAdapter.CListener) ((MainActivity) getActivity());
        adapter = new EntryAdapter(entries, (MainActivity)getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        noResults = view.findViewById(R.id.textViewNoResults);

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
                    String time = (String) entryInfo.get("time");
                    String numCorals = (String) entryInfo.get("numCorals");

                    CoralEntry coralEntry = new CoralEntry(reefName, coralName, latitude, longitude, airTemp, waterTemp,
                            salinity, cloudCov, waterTurbidity, userName, images, documentSnapshot.getId(), date, locationAccuracy,
                            waveHeight, windDirection, windSpeed, humidity, userId, time, numCorals);

                    if (!entries.contains(coralEntry)) {
                        entries.add(coralEntry);
                    }
                }
                Collections.sort(entries, new Comparator<CoralEntry>() {
                    @Override
                    public int compare(CoralEntry entry1, CoralEntry entry2) {
                        String date1 = entry1.getDate() + " " + entry1.getTime();
                        String date2 = entry2.getDate() + " " + entry2.getTime();
                        return (-1) * date1.compareTo(date2);
                    }
                });
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
        MenuItem dropdownItem = menu.findItem(R.id.action_dropdown);
        Spinner dropdown = (Spinner) dropdownItem.getActionView();
        dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dropdown.setGravity(Gravity.CENTER);
        dropdown.setAdapter(adapterCat);

        searchView = (SearchView) searchItem.getActionView();
        //searchView.setQueryHint("Search Entries");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String category = null;
                switch (dropdown.getSelectedItem().toString()) {
                    case "Reef Name":
                        category = "reefName";
                        break;
                    case "Coral Name":
                        category = "coralName";
                        break;
                    case "Entered By":
                        category = "userName";
                        break;
                    case "Date":
                        category = "date";
                        break;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (category != null) {
                    db.collection("entries")
                            .whereEqualTo(category, s)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    entries.clear();
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() == 0) {
                                            noResults.setVisibility(View.VISIBLE);
                                        } else {
                                            noResults.setVisibility(View.INVISIBLE);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Map<String, Object> data = document.getData();
                                                CoralEntry entry = new CoralEntry();
                                                entry.setCoralName(data.get("coralName").toString());
                                                entry.setReefName(data.get("reefName").toString());
                                                entry.setAirTemp(data.get("airTemp").toString());
                                                entry.setWaterTemp(data.get("waterTemp").toString());
                                                entry.setCloudCover(data.get("cloudCoverage").toString());
                                                entry.setDocumentId(document.getId());
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

                                                if (!entries.contains(entry)) {
                                                    entries.add(entry);
                                                }
                                            }
                                        }
                                        Collections.sort(entries, new Comparator<CoralEntry>() {
                                            @Override
                                            public int compare(CoralEntry entry1, CoralEntry entry2) {
                                                String date1 = entry1.getDate() + " " + entry1.getTime();
                                                String date2 = entry2.getDate() + " " + entry2.getTime();
                                                return (-1) *date1.compareTo(date2);
                                            }
                                        });
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                .setTitle("Error")
                                                .setMessage(task.getException().getMessage())
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }
                            });
                }
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
        return true;
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
            String coordinates = coralEntry.getLatitude() + "\n" + coralEntry.getLongitude();
            holder.coordinates.setText(coordinates);
            holder.userName.setText(coralEntry.getUserName());
            holder.date.setText(coralEntry.getDate() + " " + coralEntry.getTime());

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