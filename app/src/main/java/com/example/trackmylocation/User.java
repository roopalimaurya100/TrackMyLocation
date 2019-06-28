package com.example.trackmylocation;

import java.util.List;

public class User {

    public String user_id;

    public Integer total_coins;

    public List<String> merchants;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getTotal_coins() {
        return total_coins;
    }

    public void setTotal_coins(Integer total_coins) {
        this.total_coins = total_coins;
    }

    public List<String> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<String> merchants) {
        this.merchants = merchants;
    }

    public User(){

    }

    public User(String user_id, Integer total_coins, List<String> merchants) {
        this.user_id = user_id;
        this.total_coins = total_coins;
        this.merchants = merchants;
    }
}
