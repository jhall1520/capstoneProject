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

    /**
     * Empty Constructor
     */
    public CoralDatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coral_database, container, false);
        // This is the adapter that holds the category values that a user can select to query through
        // the database
        adapterCat = new ArrayAdapter<String>(getActivity(), R.layout.dropdown_layout, categories);
        setHasOptionsMenu(true);

        entries = new ArrayList<>();
        // get all data that is currently in the database
        getData();

        // initializes the recyclerView to hold all cardView of each data entry that is retrieved
        // from the database
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        cListener = (EntryAdapter.CListener) ((MainActivity) getActivity());
        adapter = new EntryAdapter(entries, (MainActivity)getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        noResults = view.findViewById(R.id.textViewNoResults);

        return view;
    }

    /**
     * Gets All entries in the database and stores them in the entries ArrayList
     */
    public void getData() {
        // Get the Database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for all entries
        db.collection("entries").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // Each document snapshot holds all the information for one Coral Entry object
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Map<String, Object> entryInfo = documentSnapshot.getData();
                    // Get All information for the coral entry
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

                    // create coralEntry object
                    CoralEntry coralEntry = new CoralEntry(reefName, coralName, latitude, longitude, airTemp, waterTemp,
                            salinity, cloudCov, waterTurbidity, userName, images, documentSnapshot.getId(), date, locationAccuracy,
                            waveHeight, windDirection, windSpeed, humidity, userId, time, numCorals);

                    // if entries does not contain the coralEntry
                    if (!entries.contains(coralEntry)) {
                        // add the coral Entry to the list
                        entries.add(coralEntry);
                    }
                }
                // sort the entries by date and time in ascending order
                Collections.sort(entries, new Comparator<CoralEntry>() {
                    @Override
                    public int compare(CoralEntry entry1, CoralEntry entry2) {
                        String date1 = entry1.getDate() + " " + entry1.getTime();
                        String date2 = entry2.getDate() + " " + entry2.getTime();
                        return (-1) * date1.compareTo(date2);
                    }
                });
                // let the adapter know that the information in entries has been updated so the
                // changes can be shown in the recyclerview
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sets up the Title Bar at the top of the screen
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0328F3"));
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity) getActivity()).setSupportTitle("Coral Entries");

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Create the search icon on the top right of screen
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        // Initializes the searchItem and dropdownItem
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem dropdownItem = menu.findItem(R.id.action_dropdown);
        Spinner dropdown = (Spinner) dropdownItem.getActionView();
        // Sets the layout for the contents in the dropdown
        dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dropdown.setGravity(Gravity.CENTER);
        dropdown.setAdapter(adapterCat);

        searchView = (SearchView) searchItem.getActionView();

        // Shows the full search bar and dropdown menu when the icon is clicked
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropdownItem.setVisible(true);
            }
        });

        // hides the full search bar and dropdown menu when the search bar is closed
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                dropdownItem.setVisible(false);
                return false;
            }
        });

        // When the user submits (presses Enter) a  query will be made
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String category = null;
                // finds out which dropdown item is selected
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
                // gets database instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (category != null) {
                    // makes query for all entries with category(field) equal to the users search
                    db.collection("entries")
                            .whereEqualTo(category, s)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    entries.clear();
                                    if (task.isSuccessful()) {
                                        // if no results were found show the noResults textView
                                        if (task.getResult().size() == 0) {
                                            noResults.setVisibility(View.VISIBLE);
                                        } else {
                                            // hid the noResults textView
                                            noResults.setVisibility(View.INVISIBLE);
                                            // add all entries found to the entries arrayList
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
                                        // sort the entries by date and time
                                        Collections.sort(entries, new Comparator<CoralEntry>() {
                                            @Override
                                            public int compare(CoralEntry entry1, CoralEntry entry2) {
                                                String date1 = entry1.getDate() + " " + entry1.getTime();
                                                String date2 = entry2.getDate() + " " + entry2.getTime();
                                                return (-1) *date1.compareTo(date2);
                                            }
                                        });
                                        // update the recyclerView with the new entries
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        // display error if the query was not successful
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

        // handles the search icon being clicked
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
        }
        return true;
    }

    /**
     * This class will extend the Adapter class so a custom layout can be made
     */
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
            // Each cardView has these components and are being set by the current coralEntry
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

        /**
         * This ViewHolder makes up each CardView shown on screen
         */
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