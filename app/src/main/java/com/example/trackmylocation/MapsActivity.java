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
    List<String> allMerchants;
    HashMap<String,Merchant> merchantHashMap = new HashMap<>();
    double latitude = 0.0;
    double longitude = 0.0;
    double latitude1 =0.0;
    double longitude1 = 0.0;

    //FirebaseDatabase database;
    //DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new HashMap<>();
    //    sqLiteDatabase = openOrCreateDatabase("FCSurfers", Context.MODE_PRIVATE ,null);
      //  sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS merchant_info (MerchantId VARCHAR(20), latitude VARCHAR(20) , longitude VARCHAR(20),TotalCoins INTEGER , REDEEMED INTEGER,TIMESTAMP VARCHAR(20))");
       /// sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_merchant_mapping (MerchantId VARCHAR(20), UserId VARCHAR(20) ,TIMESTAMP VARCHAR(20))");
        //sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_coins_mapping (UserId VARCHAR(20), Coins VARCHAR(20) , TIMESTAMP VARCHAR(20))");
        surferUtils.print();
        setContentView(R.layout.activity_maps);
        wallet = findViewById(R.id.wallet_points);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   select Coins from user_coins_mapping where UserId='u_1';
                DatabaseReference ref = database.getReference().child("users").child("u_1");
                ValueEventListener userListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        if(user!=null)
                        Toast.makeText(getApplicationContext(),"Your total FC Points : "+user.getTotal_coins(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
              //  Toast.makeText(getApplicationContext(),"Your total FC Points : "+"test",Toast.LENGTH_SHORT).show();

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

                   latitude = location.getLatitude();
                   longitude = location.getLongitude();
                /*   if(SurferUtils.diffInMeters(latitude1,longitude1,latitude,longitude) !=0
                           && SurferUtils.diffInMeters(latitude1,longitude1,latitude,longitude) < 70) return;
*/                  latitude1 = location.getLatitude();
                  longitude1 = location.getLongitude();
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
                             merchantHashMap = new HashMap<>();
                              for(DataSnapshot merchantSnapshot: dataSnapshot.getChildren()){
                                  String mid = merchantSnapshot.getKey();

                                  Log.w("mid: ",mid);
                                  //String key = merchantSnapshot.getValue(Merchant);
                                  Merchant merchant = merchantSnapshot.getValue(Merchant.class);
                                  //m.add(merchant);
                                  merchantHashMap.put(mid,merchant);
                              }
                              for(Map.Entry map : merchantHashMap.entrySet()){
                                  Log.w("MerchantMapKey ",(String)map.getKey());
                                  Merchant m123 = (Merchant)map.getValue();
                                  Log.w("details: ",m123.totalCoins+" "+m123.latitude+" "+m123.longitude);
                              }
//                              for(Merchant x: m){
//                                  //Log.w("mmmm",x.getMerchantId());
//                              }
                             // Log.w("merchants",merchantHashMap.toString().toString());
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      };
                      ref.addListenerForSingleValueEvent(merchantListener);

                      DatabaseReference refCoin = database.getReference().child("users").child("u_1");
                      ValueEventListener listenerUsers = new ValueEventListener() {

                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              User user = dataSnapshot.getValue(User.class);
                               allMerchants = user.getMerchants();

                              for(String x: allMerchants){
                                  Log.w("mmm:",x);

                                }
                              }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      };
                      refCoin.addListenerForSingleValueEvent(listenerUsers);

                      //plot nearby coins
                      merchantHashMap = SurferUtils.differenceMerchants(merchantHashMap,allMerchants);
                      merchantHashMap = SurferUtils.coinsAtADistance(merchantHashMap, 400,latitude,longitude);

                      createMarkerFromArray(merchantHashMap);

                      merchantHashMap =  SurferUtils.coinsAtADistance(merchantHashMap, 27,latitude,longitude);
                    //  coinsToCollect = SurferUtils.coinsAtADistance(latLng, 20);



                      for(Map.Entry<String,Merchant> hm : merchantHashMap.entrySet()){

                          String key = (String)hm.getKey();
                          Merchant value = (Merchant)hm.getValue();
                          double lat = Double.valueOf(value.getLatitude());
                          double lon = Double.valueOf(value.getLongitude());
                           key = lat+"_"+lon;
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
                        double DIFF = SurferUtils.diffInMeters(latLng.latitude,latLng.longitude,marker.getPosition().latitude,marker.getPosition().longitude);
                     Log.w("difference now ",String.valueOf(DIFF));
                        double la = marker.getPosition().latitude;
double lo = marker.getPosition().longitude;
                        if (DIFF<=27.0) {
                              marker.remove();
                              markers.remove(key);
                              Toast.makeText(getApplicationContext(), "Congratulations!! You got the FC COIN", Toast.LENGTH_SHORT).show();
//update user_coins_mapping set coins=coins+1 where UserId="u_1"
                            DatabaseReference ref = database.getReference().child("users").child(user_id);
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

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    /*if(SurferUtils.diffInMeters(latitude1,longitude1,latitude,longitude) !=0
                            && SurferUtils.diffInMeters(latitude1,longitude1,latitude,longitude) < 70) return;*/
                    latitude1 = location.getLatitude();
                    longitude1 = location.getLongitude();
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
                                merchantHashMap = new HashMap<>();
                                for(DataSnapshot merchantSnapshot: dataSnapshot.getChildren()){
                                    String mid = merchantSnapshot.getKey();

                                    Log.w("mid: ",mid);
                                    //String key = merchantSnapshot.getValue(Merchant);
                                    Merchant merchant = merchantSnapshot.getValue(Merchant.class);
                                    //m.add(merchant);
                                    merchantHashMap.put(mid,merchant);
                                }
                                for(Map.Entry map : merchantHashMap.entrySet()){
                                    Log.w("MerchantMapKey ",(String)map.getKey());
                                    Merchant m123 = (Merchant)map.getValue();
                                    Log.w("details: ",m123.totalCoins+" "+m123.latitude+" "+m123.longitude);
                                }
//                              for(Merchant x: m){
//                                  //Log.w("mmmm",x.getMerchantId());
//                              }
                                // Log.w("merchants",merchantHashMap.toString().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        ref.addListenerForSingleValueEvent(merchantListener);

                        DatabaseReference refCoin = database.getReference().child("users").child("u_1");
                        ValueEventListener listenerUsers = new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);
                                allMerchants = user.getMerchants();

                                for(String x: allMerchants){
                                    Log.w("mmm:",x);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        refCoin.addListenerForSingleValueEvent(listenerUsers);

                        //plot nearby coins
                        merchantHashMap = SurferUtils.differenceMerchants(merchantHashMap,allMerchants);
                        merchantHashMap = SurferUtils.coinsAtADistance(merchantHashMap, 400,latitude,longitude);

                        createMarkerFromArray(merchantHashMap);

                        merchantHashMap =  SurferUtils.coinsAtADistance(merchantHashMap, 27,latitude,longitude);
                        //  coinsToCollect = SurferUtils.coinsAtADistance(latLng, 20);



                        for(Map.Entry<String,Merchant> hm : merchantHashMap.entrySet()){

                            String key = (String)hm.getKey();
                            Merchant value = (Merchant)hm.getValue();
                            double lat = Double.valueOf(value.getLatitude());
                            double lon = Double.valueOf(value.getLongitude());
                            key = lat+"_"+lon;
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
                            double DIFF = SurferUtils.diffInMeters(latLng.latitude,latLng.longitude,marker.getPosition().latitude,marker.getPosition().longitude);
                            Log.w("difference now ",String.valueOf(DIFF));
                            double la = marker.getPosition().latitude;
                            double lo = marker.getPosition().longitude;
                            if (DIFF<=27.0) {
                                marker.remove();
                                markers.remove(key);
                                Toast.makeText(getApplicationContext(), "Congratulations!! You got the FC COIN", Toast.LENGTH_SHORT).show();
//update user_coins_mapping set coins=coins+1 where UserId="u_1"
                                DatabaseReference ref = database.getReference().child("users").child(user_id);
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
/*       ArrayList<Double> latLang = new ArrayList<Double>();
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
      }*/
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

    protected void createMarkerFromArray(HashMap<String,Merchant> hashMap) {

for(Map.Entry hm : hashMap.entrySet()){

    String key = (String)hm.getKey();
    Merchant value = (Merchant)hm.getValue();
    double latitude = Double.valueOf(value.getLatitude());
    double longitude = Double.valueOf(value.getLongitude());
    String keyMarker = latitude+"_"+longitude;
    Marker valueMarker =   mMap.addMarker(new MarkerOptions()
            .position(new LatLng(latitude, longitude))
            .title("Coins")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

    markers.put(keyMarker,valueMarker);

}


    }


}
