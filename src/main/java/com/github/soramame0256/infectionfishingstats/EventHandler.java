package com.github.soramame0256.infectionfishingstats;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.BIG;
import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.NORMAL;

public class EventHandler {
    private static final Pattern catchMsg = Pattern.compile("§7§l\\[ §r§e§lFishing §r§7§l] §r(?<fish>.*)§r§7を釣り上げた！§r");
    private static final Pattern moneyMsg = Pattern.compile("§9§l》 §r§e\\+(?<amount>.*)円§r");
    private static final Pattern announceMsg = Pattern.compile("§9§l》 §r§7§l(?<mcid>.*)§r§7が§r(?<fish>.*)§r§7を釣り上げた！§r");
    private boolean isNextCaughtMoney = false;
    public static EventHandler INSTANCE;
    private String latestFish = "";
    private Instant insta = Instant.now();
    public EventHandler(){
        INSTANCE = this;
    }
    public void handleNext(String name){
        latestFish = name;
        insta = Instant.now();
        isNextCaughtMoney = true;
    }
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent e) {
        String msg = e.getMessage().getFormattedText();
        Matcher m = catchMsg.matcher(msg);
        Matcher m2 = announceMsg.matcher(msg);
        if(m2.matches()){
            if(m2.group("fish").equals(latestFish) && m2.group("mcid").equals(Minecraft.getMinecraft().getSession().getUsername())) {
                if(StatsHolder.getFish(latestFish)!=null) {
                    if (!StatsHolder.getFish(latestFish).isBig()) {
                        StatsHolder.getFish(latestFish).setBig(true);
                        StatsHolder.incrementBigCaught();
                        return;
                    }
                }
            }
        }
        if(isNextCaughtMoney && insta.getEpochSecond()+1>Instant.now().getEpochSecond()){
            Matcher m3 = moneyMsg.matcher(msg);
            if(m3.matches()){
                isNextCaughtMoney=false;
                StatsHolder.addPrice(latestFish, Integer.parseInt(m3.group("amount")));
                StatsHolder.onMoneyIncrementByFish(Integer.parseInt(m3.group("amount")));
            }
            isNextCaughtMoney = false;
        }else if(m.matches()){
            insta = Instant.now();
            isNextCaughtMoney = true;
            latestFish = m.group("fish");
            StatsHolder.addFish(latestFish, false);
            if(StatsHolder.getFish(latestFish).isBig()){
                StatsHolder.incrementFishCount(BIG, latestFish);
            }else{
                StatsHolder.incrementFishCount(NORMAL, latestFish);
            }
        }else if(msg.startsWith("§7§l[ §r§e§lFishing §r§7§l]")){
            MessageHandler.handle(msg);
        }
    }
}
