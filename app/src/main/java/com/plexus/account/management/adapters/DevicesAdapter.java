package com.plexus.account.management.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.plexus.R;
import com.plexus.account.management.activities.ChangePasswordActivity;
import com.plexus.model.account.Devices;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    List<Devices> devicesList;
    Context mContext;
    Activity activity;

    public DevicesAdapter(Context context, List<Devices> devicesList, Activity activity) {
        mContext = context;
        this.devicesList = devicesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Devices devices = devicesList.get(position);

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(devices.getDevice_latitude(), devices.getDevice_longitude(), 1);
            Address obj = addresses.get(0);
            String country = obj.getCountryName();
            String area = obj.getAdminArea();

            holder.location.setText(String.format("%s, %s", area, country));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (devices.getDevice_token().equals(FirebaseInstanceId.getInstance().getToken())) {
            holder.login_date.setText("Active Now");
            holder.login_date.setTextColor(Color.parseColor("#09FF00"));
            holder.login_device.setText("Current Device");
        } else {
            holder.login_date.setText(devices.getDevice_login_time());
            holder.login_device.setText(devices.getDevice_name());
        }

        holder.show_details.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.sheet_device_location);

            MaterialButton change_password = bottomSheetDialog.findViewById(R.id.change_password);

            change_password.setOnClickListener(v12 -> mContext.startActivity(new Intent(mContext, ChangePasswordActivity.class)));

            MapView mapView = bottomSheetDialog.findViewById(R.id.mapView);
            mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
            mapView.setBuiltInZoomControls(false);
            mapView.setMultiTouchControls(false);

            mapView.setOnTouchListener((v1, event) -> true);

            //Permission checking needs to be added in order for the map to show
            switch (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                case PackageManager.PERMISSION_GRANTED:
                case PackageManager.PERMISSION_DENIED:
                    break;
                default:
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    break;
            }

            IMapController mapController = mapView.getController();
            mapController.setZoom(9.5);

            GeoPoint startPoint = new GeoPoint(devices.getDevice_latitude(), devices.getDevice_longitude());

            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            mapView.getOverlays().add(startMarker);
            mapController.setCenter(startPoint);

            bottomSheetDialog.show();

        });

    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView location, login_date, login_device;
        ImageView show_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.location);
            login_date = itemView.findViewById(R.id.login_date);
            login_device = itemView.findViewById(R.id.login_device);
            show_details = itemView.findViewById(R.id.show_details);

        }
    }

}
