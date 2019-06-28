package com.example.trackmylocation;

public class Merchant {

    public String merchantId;

    public String latitude;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String longitude;

    public Integer totalCoins;

    public Integer redeemed;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(Integer totalCoins) {
        this.totalCoins = totalCoins;
    }

    public Integer getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(Integer redeemed) {
        this.redeemed = redeemed;
    }
}
