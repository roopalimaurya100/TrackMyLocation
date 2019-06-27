package com.example.trackmylocation;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.*;
import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SQLiteDatabase sqLiteDatabase ;
    private GoogleMap mMap;
    LocationManager locationManager;
    Marker marker;
    Button wallet;
    SurferUtils surferUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteDatabase = openOrCreateDatabase("FCSurfers", Context.MODE_PRIVATE ,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS merchant_info (MerchantId VARCHAR(20), latitude VARCHAR(20) , longitude VARCHAR(20),TotalCoins INTEGER , REDEEMED INTEGER,TIMESTAMP VARCHAR(20))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_merchant_mapping (MerchantId VARCHAR(20), UserId VARCHAR(20) ,TIMESTAMP VARCHAR(20))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_coins_mapping (UserId VARCHAR(20), Coins VARCHAR(20) , TIMESTAMP VARCHAR(20))");
        surferUtils.print();
        setContentView(R.layout.activity_maps);
        wallet = findViewById(R.id.wallet_points);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Points",Toast.LENGTH_SHORT).show();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }




      if(  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
          locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
              @Override
              public void onLocationChanged(Location location) {

                  double latitude = location.getLatitude();
                  double longitude = location.getLongitude();
                  LatLng latLng = new LatLng(latitude,longitude);
                  Geocoder geocoder = new Geocoder(getApplicationContext());
                  try {
                      List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                      String str = addressList.get(0).getLocality() + " , "+addressList.get(0).getCountryName()+latitude+" "+longitude;
                      if(marker != null){
                          marker.remove();
                      }
                    marker=  mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 19.9f));
                      mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                  }
                  catch(IOException e){
                      e.printStackTrace();
                  }
              }

              @Override
              public void onStatusChanged(String s, int i, Bundle bundle) {

              }

              @Override
              public void onProviderEnabled(String s) {

              }

              @Override
              public void onProviderDisabled(String s) {

              }
          });

      }
     else   if(  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude,longitude);
                    Log.d("new latlng " ,latitude + " " + longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + " , "+addressList.get(0).getCountryName();
                        if(marker != null){
                            marker.remove();
                        }
                     marker = mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 19.9f));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

        }


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
       ArrayList<Double> latLang = new ArrayList<Double>();
        latLang.add(28.4982091) ; latLang.add(77.1054209);
        latLang.add(28.4982012) ; latLang.add(77.1056465);
        latLang.add(28.4981213) ; latLang.add(77.1056364);
        latLang.add(28.4720844) ; latLang.add(77.1052663);
        latLang.add(28.4720844) ; latLang.add(77.099499);
        latLang.add(28.4720804) ; latLang.add(77.099498);
        latLang.add(28.4720834) ; latLang.add(77.099497);
        latLang.add(28.4720874) ; latLang.add(77.099484);
        latLang.add(28.4720864) ; latLang.add(77.099490);
        latLang.add(28.4720844) ; latLang.add(77.095662);
     for(int i = 0 ;i<20;i+=2){
          createMarker(latLang.get(i),latLang.get(i+1));
      }
    }

    protected void createMarker(double latitude, double longitude) {

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                 .title("Coins")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }


}
