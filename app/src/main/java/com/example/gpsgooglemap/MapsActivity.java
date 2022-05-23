package com.example.gpsgooglemap;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpsgooglemap.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.core.content.ContentProviderCompat.requireContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    boolean bool = false;
    private LatLng admin, previous;
    Boolean isAdmin = false;
    private Statement statement;
    private String lon, lat, alt, fuel, oil, timebegin, timeend;
    ArrayList<LatLng> arraypoints = new ArrayList<>();
    String myIP = "192.168.1.165";

    public void AdminClick(View view){
        String query = "SELECT lvl FROM test.ip WHERE ip = " + "'" + myIP + "'";
        try {
            ResultSet result = statement. executeQuery(query);
            result.next();
            if(result.getString("lvl").equals("2")) isAdmin = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Intent intent = new Intent(this, AdminActivity.class);
        intent.putExtra("extraRules", isAdmin);
        intent.putExtra("altitude", alt);
        intent.putExtra("fuel", fuel);
        intent.putExtra("oil", oil);
        intent.putExtra("timebegin", timebegin);
        intent.putExtra("timeend", timeend);
        startActivity(intent);
    }

    private void LocationUpdate() {

        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run(){
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        String query = "SELECT longtitude, latitude, altitude, fuel, oil, timebegin, timeend FROM test.testair";
                        try {
                            ResultSet result = statement.executeQuery(query);
                            while (result.next()) {
                                lon = result.getString("longtitude");
                                lat = result.getString("latitude");
                                alt = result.getString("altitude");
                                fuel = result.getString("fuel");
                                oil = result.getString("oil");
                                timebegin = result.getString("timebegin");
                                timeend = result.getString("timeend");
                            }
                            Toast.makeText(getBaseContext(), "altitude: " + alt,Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMap.clear();
                        if (bool) previous = admin;
                        admin = new LatLng(Double.parseDouble(lat.replace(',', '.')),
                                Double.parseDouble(lon.replace(',', '.')));
                        arraypoints.add(admin);
                        if (mMap != null) {

                            mMap.addMarker(new MarkerOptions().position(admin).title(""));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(admin, 9));
                            if (bool)
                            {
                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .addAll(arraypoints)
                                    .width(5)
                                    .color(Color.RED));

                            }
                            bool = true;
                        }

                    }
                });
            }
        }, 0, 10000);
    }
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.2.236:3306/test","rootLocal", "123456");
            statement = connection.createStatement();
            Toast.makeText(this, "Connection OK", Toast.LENGTH_SHORT).show();

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Context context = this;
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this, "Your IP: " + ip, Toast.LENGTH_SHORT).show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LocationUpdate();

    }

}