package com.example.coralreefproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewDataFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ENTRY = "entry";

    private CoralEntry entry;
    private ArrayList<Uri> images;
    private RecyclerView recyclerView;
    private CoralEntryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        images = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewViewData);
        adapter = new CoralEntryAdapter(images);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (entry.getImages() != null) {
            for (int i = 0; i < entry.getImages().size(); i++) {
                int count = i;
                StorageReference httpsReference = storage.getReferenceFromUrl(entry.getImages().get(i));
                httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(Uri uri) {
                        images.add(uri);
                        if (count == entry.getImages().size() - 1) {
                            adapter.notifyDataSetChanged();
                            TextView reefName = view.findViewById(R.id.textViewShowReef);
                            TextView coralName = view.findViewById(R.id.textViewShowCoral);
                            TextView date = view.findViewById(R.id.textViewDatee);
                            TextView coordinates = view.findViewById(R.id.textViewShowCoord);
                            TextView locationAccuracy = view.findViewById(R.id.textViewShowAccuracy);
                            TextView airTemp = view.findViewById(R.id.textViewShowTemp);
                            TextView cloudCoverage = view.findViewById(R.id.textViewShowCloud);
                            TextView humidity = view.findViewById(R.id.textViewShowHumid);
                            TextView windDirection = view.findViewById(R.id.textViewShowWindDir);
                            TextView windSpeed = view.findViewById(R.id.textViewShowWindSp);
                            TextView waterTemp = view.findViewById(R.id.textViewShowWaterTemp);
                            TextView salinity = view.findViewById(R.id.textViewShowSal);
                            TextView waterTurb = view.findViewById(R.id.textViewShowWaterTurb);
                            TextView waveHeight = view.findViewById(R.id.textViewShowHeight);
                            TextView user = view.findViewById(R.id.textViewUser);
                            TextView numCorals = view.findViewById(R.id.textViewShowNumCorals);

                            reefName.setText(entry.getReefName());
                            coralName.setText(entry.getCoralName());
                            date.setText(entry.getDate() + " " + entry.getTime());
                            String coordinatesString = entry.getLatitude() + " , " + entry.getLongitude();
                            coordinates.setText(coordinatesString);
                            locationAccuracy.setText(entry.getLocationAccuracy());
                            airTemp.setText(entry.getAirTemp());
                            cloudCoverage.setText(entry.getCloudCover());
                            humidity.setText(entry.getHumidity());
                            windDirection.setText(entry.getWindDirection());
                            windSpeed.setText(entry.getWindSpeed());
                            waterTemp.setText(entry.getWaterTemp());
                            salinity.setText(entry.getSalinity());
                            waterTurb.setText(entry.getWaterTurbidity());
                            waveHeight.setText(entry.getWaveHeight());
                            user.setText(entry.getUserName());
                            numCorals.setText(entry.getNumCorals());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to upload images.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setSupportTitle("Coral Entry");
    }

    static class CoralEntryAdapter extends RecyclerView.Adapter<CoralEntryAdapter.EntryViewHolder> {
        ArrayList<Uri> images;

        public CoralEntryAdapter(ArrayList<Uri> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public CoralEntryAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);
            return new CoralEntryAdapter.EntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CoralEntryAdapter.EntryViewHolder holder, int position) {
            Uri image = images.get(position);
            Picasso.with(holder.imageView.getContext()).load(image).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        static class EntryViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public EntryViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.imageViewImage);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
                                View inflatedView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.larger_image_layout, (ViewGroup) itemView, false);
                                ImageView image = inflatedView.findViewById(R.id.imageViewLargeImage);
                                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                image.setImageDrawable(imageView.getDrawable());
                                alertDialog.setView(inflatedView);

                                alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
                            }
                        });
                    }
                });
            }
        }
    }
}