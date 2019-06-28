package com.example.trackmylocation;

import java.lang.*;
import  java.util.*;
import com.google.android.gms.maps.model.LatLng;

public class SurferUtils {

    public static void print(){
        System.out.println("Hello");
    }

    public static ArrayList<LatLng> coinsAtADistance(LatLng user,int dist){
        double latitude = user.latitude;
        double longitude = user.longitude;

        ArrayList<LatLng> coinsToBePlotted = new ArrayList<>();
        //get all coins from database
        //select MerchantId,latitude,longitude from merchant_info where MerchantId not in (select MerchantId from user_merchant_mapping where  UserId='u_1') ;
        ArrayList<Double> allCoinLat = new ArrayList<Double>();
        ArrayList<Double> allCoinLon = new ArrayList<Double>();
        for(int i =0 ; i<allCoinLat.size();i++){

            double lat = allCoinLat.get(i);
            double lon = allCoinLon.get(i);
            double distance = diffInMeters(latitude,longitude,lat,lon);
            if(distance<= dist ){
                coinsToBePlotted.add(new LatLng(lat,lon));
            }
        }
        return coinsToBePlotted;
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
}
