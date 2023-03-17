package com.github.soramame0256.infectionfishingstats;

import com.github.soramame0256.infectionfishingstats.util.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.BIG;

public class StatsHolder {
    private static long total = 0;
    private static long dTotal = 0;
    private static long bigCatches = 0;
    private static long dBigCatches = 0;
    private static long caughtAmount = 0;
    private static long dCaughtAmount = 0;
    private static long totalMoney = 0;
    private static long dTotalMoney = 0;

    private static JsonUtils ju;
    private static long today;
    static{
        Calendar cl = Calendar.getInstance();
        today = cl.get(Calendar.YEAR)* 10000L + cl.get(Calendar.MONTH)*100 + cl.get(Calendar.DAY_OF_MONTH);
    }
    private static final Map<String, FishStats> fishes = new HashMap<>();
    private static Instant cd = Instant.now();
    public static void init() throws IOException {
        ju = new JsonUtils("infectionfishingstats/data.json");
        JsonObject jo = ju.getRoot().getAsJsonObject();
        if(jo.has("dictionary")){
            for(JsonElement je : jo.get("dictionary").getAsJsonArray()){
                FishStats fs = FishStats.genFromJson(je.getAsJsonObject());
                fishes.put(fs.getName(), fs);
            }
        }
        if(jo.has("statistics")){
            JsonObject jo2 = jo.get("statistics").getAsJsonObject();
            total = jo2.has("total") ? jo2.get("total").getAsLong() : 0;
            bigCatches = jo2.has("bigCatches") ? jo2.get("bigCatches").getAsLong() : 0;
            caughtAmount = jo2.has("caughtAmount") ? jo2.get("caughtAmount").getAsLong() : 0;
            totalMoney = jo2.has("totalMoney") ? jo2.get("totalMoney").getAsLong() : 0;
        }
        if(jo.has("dailyStatistics")){
            JsonObject jo2 = jo.get("dailyStatistics").getAsJsonObject();
            if(today == (jo2.has("today") ? jo2.get("today").getAsLong() : today)){
                dTotal = jo2.has("total") ? jo2.get("total").getAsLong() : 0;
                dBigCatches = jo2.has("bigCatches") ? jo2.get("bigCatches").getAsLong() : 0;
                dCaughtAmount = jo2.has("caughtAmount") ? jo2.get("caughtAmount").getAsLong() : 0;
                dTotalMoney = jo2.has("totalMoney") ? jo2.get("totalMoney").getAsLong() : 0;
            }
        }
    }
    private static void checkDay(){
        long tod;
        Calendar cl = Calendar.getInstance();
        tod = cl.get(Calendar.YEAR)* 10000L + cl.get(Calendar.MONTH)*100 + cl.get(Calendar.DAY_OF_MONTH);
        if(tod != today){
            today = tod;
            dTotal = 0;
            dBigCatches = 0;
            dCaughtAmount = 0;
        }
    }
    public static void incrementBigCaught(){
        checkDay();
        dBigCatches++;
        bigCatches++;
    }
    public static void incrementFishCount(FishType ft, String name){
        checkDay();
        switch(ft){
            case BIG:
                dBigCatches++;
                bigCatches++;
            case NORMAL:
                dTotal++;
                total++;
            default:
                incrementCaughtCount(ft, name);
        }
    }
    private static void incrementCaughtCount(FishType ft, String name){
        if(!fishes.containsKey(name)){
            addFish(name, ft==BIG);
        }
        fishes.get(name).incrementCount();
        if(cd.getEpochSecond()!= Instant.now().getEpochSecond()){
            cd = Instant.now();
            caughtAmount++;
            dCaughtAmount++;
        }
    }
    public static void onMoneyIncrementByFish(long money){
        dTotalMoney+=money;
        totalMoney+=money;
    }
    public static void addFish(String name, boolean isBig){
        if(!fishes.containsKey(name)) fishes.put(name, new FishStats(name, new ArrayList<>(), 0, isBig));
    }
    public static void addPrice(String name, int price){
        if(fishes.containsKey(name)){
            fishes.get(name).addPrice(price);
        }
    }
    public static void dump(ICommandSender c,String ty){
        if(ty.equals("dic")) {
            c.sendMessage(new TextComponentString("魚名      | 価格    | キャッチ回数 | 大物か?"));
            for (Map.Entry<String, FishStats> f : fishes.entrySet()) {
                ITextComponent tc = new TextComponentString(f.getValue().getName() + "§r | ");
                StringBuilder sb = new StringBuilder();
                for (Integer i : f.getValue().getPrices()) {
                    sb.append(i.toString()).append(",");
                }
                sb.append("~");
                tc.appendText(sb.toString().replaceAll(",~", ""));
                tc.appendText(" | ").appendText(String.valueOf(f.getValue().getCount())).appendText(" | ").appendText(String.valueOf(f.getValue().isBig()));
                c.sendMessage(tc);
            }
        }else if(ty.equals("stat")){
            c.sendMessage(new TextComponentString("統計      :"));
            c.sendMessage(new TextComponentString("魚全体    : " + total));
            c.sendMessage(new TextComponentString("大物釣り  : "+ bigCatches));
            c.sendMessage(new TextComponentString("釣り回数  : " + caughtAmount));
            c.sendMessage(new TextComponentString("売却金累計: " + totalMoney + "円"));
            c.sendMessage(new TextComponentString("本日統計  :"));
            c.sendMessage(new TextComponentString("魚全体    : " + dTotal));
            c.sendMessage(new TextComponentString("大物釣り  : "+ dBigCatches));
            c.sendMessage(new TextComponentString("釣り回数  : " + dCaughtAmount));
            c.sendMessage(new TextComponentString("売却金累計: " + dTotalMoney + "円"));

        }
    }
    public static FishStats getFish(String name){
        return fishes.getOrDefault(name, null);
    }

    public static void serializeAndFlush() {
        JsonArray dicArr = new JsonArray();
        for(Map.Entry<String, FishStats> fs : fishes.entrySet()){
            JsonObject fish = new JsonObject();
            FishStats fs2 = fs.getValue();
            fish.addProperty("name", fs2.getName());
            JsonArray ja = new JsonArray();
            for(Integer price : fs2.getPrices()){
                ja.add(price);
            }
            fish.add("prices", ja);
            fish.addProperty("count",fs2.getCount());
            fish.addProperty("isBig",fs2.isBig());
            dicArr.add(fish);
        }
        ju.saveData("dictionary", dicArr);
        JsonObject statistics = new JsonObject();
        statistics.addProperty("total", total);
        statistics.addProperty("bigCatches", bigCatches);
        statistics.addProperty("caughtAmount", caughtAmount);
        statistics.addProperty("totalMoney", totalMoney);
        JsonObject dStatistics = new JsonObject();
        dStatistics.addProperty("today", today);
        dStatistics.addProperty("total", dTotal);
        dStatistics.addProperty("bigCatches", dBigCatches);
        dStatistics.addProperty("caughtAmount", dCaughtAmount);
        dStatistics.addProperty("totalMoney", dTotalMoney);
        ju.saveData("statistics", statistics);
        ju.saveData("dailyStatistics", dStatistics);
    }

    public enum FishType{
        BIG,
        NORMAL,
        OTHER
    }
}