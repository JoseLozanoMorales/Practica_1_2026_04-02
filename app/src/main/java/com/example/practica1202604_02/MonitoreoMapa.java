package com.example.practica1202604_02;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonitoreoMapa extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mapa;
    Marker marcador = null;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    DatabaseReference coordinatesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monitoreo_mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaGoogle);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error: No se pudo encontrar el fragmento del mapa.", Toast.LENGTH_LONG).show();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        coordinatesRef = mDatabase.child("Coordenadas");

        coordinatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitud = snapshot.child("latitud").getValue(Double.class);
                Double longitud = snapshot.child("longitud").getValue(Double.class);
                if (latitud != null && longitud != null) {
                    actualizarMarcadorMapa(latitud, longitud);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
    }
    @SuppressLint("MissingPermission")
    private void setupLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    grabarNuevaPosicionGPS(location);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == 			PackageManager.PERMISSION_GRANTED) {
            setupLocationUpdates();
        } else {
            Toast.makeText(this, "Acceso NO permitido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    private void grabarNuevaPosicionGPS(Location location) {

        TextView txtLatitud = findViewById(R.id.txt_latitud);
        TextView txtLongitud = findViewById(R.id.txt_longitud);
        txtLatitud.setText (String.format("%.5f", location.getLatitude()));
        txtLongitud.setText (String.format("%.5f", location.getLongitude()));

        coordinatesRef.child("latitud").setValue (location.getLatitude());
        coordinatesRef.child("longitud").setValue (location.getLongitude());

    }

    private void actualizarMarcadorMapa(double latitud, double longitud) {
        if (mapa != null) {
            LatLng nuevaPosicion = new LatLng(latitud, longitud);
            if (marcador == null) {
                marcador = mapa.addMarker(new MarkerOptions().position(nuevaPosicion).title("Mi Ubicación"));
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(nuevaPosicion, 15));
            } else {
                marcador.setPosition(nuevaPosicion);
            }
        }
    }
}
