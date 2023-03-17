package com.github.soramame0256.infectionfishingstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FishStats {
    private final String name;
    private final List<Integer> prices = new ArrayList<>();
    private long count;
    private boolean isBig;
    public FishStats(String name, List<Integer> prices, long count, boolean isBig){
        this.name = name;
        this.prices.addAll(prices);
        this.count = count;
        this.isBig = isBig;
    }
    public static FishStats genFromJson(JsonObject jo){
        String name = jo.get("name").getAsString();
        List<Integer> prices = new ArrayList<>();
        boolean isBig;
        long count = 0;
        if(jo.has("prices")){
            for(JsonElement je : jo.get("prices").getAsJsonArray()){
                prices.add(je.getAsInt());
            }
        }
        if(jo.has("count")){
            count = jo.get("count").getAsLong();
        }
        isBig = jo.has("isBig") && jo.get("isBig").getAsBoolean();
        return new FishStats(name, prices, count, isBig);
    }
    public void incrementCount(){
        count++;
    }
    public long getCount(){
        return this.count;
    }
    public void setBig(boolean is){
        isBig = is;
    }
    public boolean isBig(){
        return isBig;
    }
    public String getName(){
        return name;
    }
    public void addPrice(int price){
        if(!prices.contains(price)) prices.add(price);
        if(prices.contains(0) && prices.size()>1) prices.remove(0);
    }
    public List<Integer> getPrices(){
        return this.prices;
    }
}
