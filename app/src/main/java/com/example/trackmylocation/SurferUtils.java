package com.example.trackmylocation;

import java.lang.*;
import  java.util.*;
import com.google.android.gms.maps.model.LatLng;

public class SurferUtils {

    public static void print(){
        System.out.println("Hello");
    }

    public static HashMap<String,Merchant>  coinsAtADistance(HashMap<String,Merchant> hashMap,int dist,double latUser,double lonUser){

//           for(Map.Entry hm : hashMap.entrySet()){
//           String key = (String)hm.getKey();
//           Merchant value = (Merchant)hm.getValue();
//           double latitude = Double.valueOf(value.getLatitude());
//           double longitude = Double.valueOf(value.getLongitude());
//            double distance = diffInMeters(latitude,longitude,latUser,lonUser);
////            if(distance> dist ){
//////                hm.remove(key);
////
////            }
//
//
//           }
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry hm = (Map.Entry)it.next();
            String key = (String)hm.getKey();
           Merchant value = (Merchant)hm.getValue();
           double latitude = Double.valueOf(value.getLatitude());
           double longitude = Double.valueOf(value.getLongitude());
            double distance = diffInMeters(latitude,longitude,latUser,lonUser);
            if(distance> dist ) {
                it.remove();
            }
        }

        return hashMap;
    }

    //using Haversine method
    public static double diffInMeters(double lat1, double lon1, double lat2, double lon2)
    {
        final int R = 6371; // Radius of the earth in Km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public static long updateTotalUserPoints(String UserId,int points){
        long updatedpoints =0;
        //fetch current user points from db
        int currentPoint = 10;
        //update db with points;
        return updatedpoints;
    }
    public static  HashMap<String,Merchant>  differenceMerchants(HashMap<String,Merchant> hashMap,List<String> mer){
if(mer==null ) return hashMap;
        for(int i = 0 ; i<mer.size();i++){
           if(hashMap.containsKey(mer))
            hashMap.remove(mer);
        }
        return hashMap;

    }
}
