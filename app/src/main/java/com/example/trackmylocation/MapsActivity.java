package com.example.trackmylocation;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SQLiteDatabase sqLiteDatabase ;
    private GoogleMap mMap;
    private GoogleMap usermMap;
    LocationManager locationManager;
    Marker marker;
    Button wallet;
    SurferUtils surferUtils;
    ArrayList<LatLng> coinsToCollect = null;
    HashMap<String,Marker> markers;
    String user_id = "u_1";
    //FirebaseDatabase database;
    //DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new HashMap<>();
        sqLiteDatabase = openOrCreateDatabase("FCSurfers", Context.MODE_PRIVATE ,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS merchant_info (MerchantId VARCHAR(20), latitude VARCHAR(20) , longitude VARCHAR(20),TotalCoins INTEGER , REDEEMED INTEGER,TIMESTAMP VARCHAR(20))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_merchant_mapping (MerchantId VARCHAR(20), UserId VARCHAR(20) ,TIMESTAMP VARCHAR(20))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_coins_mapping (UserId VARCHAR(20), Coins VARCHAR(20) , TIMESTAMP VARCHAR(20))");
        surferUtils.print();
        setContentView(R.layout.activity_maps);
        wallet = findViewById(R.id.wallet_points);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   select Coins from user_coins_mapping where UserId='u_1';
                DatabaseReference ref = database.getReference().child("users").child("users").child("u_1");
                ValueEventListener userListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Object yyy = dataSnapshot.child("users").child("u_1");
                        User user = dataSnapshot.getValue(User.class);
                        Toast.makeText(getApplicationContext(),"Your total FC Points : "+user.getTotal_coins(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };

                ref.addListenerForSingleValueEvent(userListener);
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




      if(  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
          locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
              @Override
              public void onLocationChanged(Location location) {

                  double latitude = location.getLatitude();
                  double longitude = location.getLongitude();
                  final LatLng latLng = new LatLng(latitude, longitude);
                  Geocoder geocoder = new Geocoder(getApplicationContext());
                  try {
                      List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                      String str = addressList.get(0).getLocality() + " , " + addressList.get(0).getCountryName() + latitude + " " + longitude;
                      if (marker != null) {
                          marker.remove();
                      }
                      marker = usermMap.addMarker(new MarkerOptions().position(latLng).title(str));
                      usermMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.9f));
                      usermMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                  //steps to repeat if user moved a significant distance
                  {
                      //clear all markers
                      for (Map.Entry hm : markers.entrySet()) {
                          Marker value = (Marker) hm.getValue();
                          value.remove();
                      }

                      DatabaseReference ref = database.getReference().child("merchants");
                      ValueEventListener merchantListener = new ValueEventListener() {

                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              //Map<String,Merchant> m = new HashMap<>();
                                      //(Map<String, Object>) dataSnapshot.getChildren();
                             List<Merchant> m = new ArrayList<>();
                              for(DataSnapshot merchantSnapshot: dataSnapshot.getChildren()){
                                  //String key = merchantSnapshot.getValue(Merchant);
                                  Merchant merchant = merchantSnapshot.getValue(Merchant.class);
                                  m.add(merchant);
                              }
//                              for(Map.Entry map : m.entrySet()){
//                                  Log.w("MerchantMapKey ",(String)map.getKey());
//                                  Log.w("MerchantMapValue ",(String) map.getValue());
//                              }
                              for(Merchant x: m){
                                  Log.w("mmmm",x.getMerchantId());
                              }
                              Log.w("merchants",m.toString().toString());
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      };
                      ref.addListenerForSingleValueEvent(merchantListener);
                      //plot nearby coins
                      ArrayList<LatLng> coinsToPlot = SurferUtils.coinsAtADistance(latLng, 500);
                      LatLng obj  = new LatLng(28.4938342, 77.0927204);
                      coinsToPlot.add(obj);
                      LatLng obj1  = new LatLng(28.4938392, 77.0927604);
                      coinsToPlot.add(obj1);
                      coinsToPlot.add(new LatLng(28.4938742, 77.0927244));
                      createMarkerFromArray(coinsToPlot);

                      coinsToCollect = SurferUtils.coinsAtADistance(latLng, 20);
                      coinsToCollect.add(obj);
                      coinsToCollect.add(obj1);

                      TextView text = new TextView(getApplicationContext());
                      text.setText("Tap to collect!!");
                    /*  IconGenerator generator = new IconGenerator(getApplicationContext());
                      generator.setBackground(context.getDrawable(R.drawable.coin));
                      generator.setContentView(text);
                      Bitmap icon = generator.makeIcon();*/
                      for (int i = 0; i < coinsToCollect.size(); i++) {
                          double lat = coinsToCollect.get(i).latitude;
                          double lon = coinsToCollect.get(i).longitude;
                          String key = lat+"_"+lon;
                          if(markers.containsKey(key)) {
                              Marker val = markers.get(key);
                              val.setTag("Tap to collect!!");
                              val.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                          }
                      }


                  }
                  mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                      @Override
                      public boolean onMarkerClick(Marker marker) {
                          if (marker.getPosition().latitude == latLng.latitude && marker.getPosition().longitude == latLng.longitude)
                              return false;
                          LatLng coin = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                          String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
                          if (coinsToCollect.contains(coin)) {
                              marker.remove();
                              markers.remove(key);
                              Toast.makeText(getApplicationContext(), "Congratulations!! You got the FC COIN", Toast.LENGTH_SHORT).show();
//update user_coins_mapping set coins=coins+1 where UserId="u_1
                              //update user_merchant_mapping set coins=coins-1 where latitude={} and longitude={}




                              return true;
                          } else {
                              Toast.makeText(getApplicationContext(), "Move to the coin location to collect it!", Toast.LENGTH_SHORT).show();
                              return false;
                          }
                      }
                  });
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
     else   if(  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    final LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality() + " , " + addressList.get(0).getCountryName() + latitude + " " + longitude;
                        if (marker != null) {
                            marker.remove();
                        }
                        marker = usermMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        usermMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.9f));
                        usermMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //steps to repeat if user moved a significant distance
                    {
                        //clear all markers
                        for (Map.Entry hm : markers.entrySet()) {
                            Marker value = (Marker) hm.getValue();
                            value.remove();
                        }
                        //plot nearby coins
                        ArrayList<LatLng> coinsToPlot = SurferUtils.coinsAtADistance(latLng, 500);
                        LatLng obj  = new LatLng(28.4938342, 77.0927204);
                        coinsToPlot.add(obj);
                        LatLng obj1  = new LatLng(28.4938392, 77.0927604);
                        coinsToPlot.add(obj1);
                        coinsToPlot.add(new LatLng(28.4938742, 77.0927244));
                        createMarkerFromArray(coinsToPlot);

                        coinsToCollect = SurferUtils.coinsAtADistance(latLng, 20);
                        coinsToCollect.add(obj);
                        coinsToCollect.add(obj1);

                        TextView text = new TextView(getApplicationContext());
                        text.setText("Tap to collect!!");
                    /*  IconGenerator generator = new IconGenerator(getApplicationContext());
                      generator.setBackground(context.getDrawable(R.drawable.coin));
                      generator.setContentView(text);
                      Bitmap icon = generator.makeIcon();*/
                        for (int i = 0; i < coinsToCollect.size(); i++) {
                            double lat = coinsToCollect.get(i).latitude;
                            double lon = coinsToCollect.get(i).longitude;
                            String key = lat+"_"+lon;
                            if(markers.containsKey(key)) {
                                Marker val = markers.get(key);
                                val.setTag("Tap to collect!!");
                                val.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }
                        }


                    }
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (marker.getPosition().latitude == latLng.latitude && marker.getPosition().longitude == latLng.longitude)
                                return false;
                            LatLng coin = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                            String key = marker.getPosition().latitude + "_" + marker.getPosition().longitude;
                            if (coinsToCollect.contains(coin)) {
                                marker.remove();
                                markers.remove(key);
                                return true;
                            } else {
                                Toast.makeText(getApplicationContext(), "Move to the coin location to collect it!", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    });
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
        usermMap = googleMap;
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
        String key = latitude+"_"+longitude;
        Marker value =
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                 .title("Coins")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        markers.put(key,value);
    }

    protected void createMarkerFromArray(ArrayList<LatLng> arr) {


        for(int i = 0; i<arr.size() ;i++) {
            double latitude = arr.get(i).latitude;
            double longitude = arr.get(i).longitude;
            String key = latitude+"_"+longitude;
            Marker value =   mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Coins")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            markers.put(key,value);
        }

    }


}
