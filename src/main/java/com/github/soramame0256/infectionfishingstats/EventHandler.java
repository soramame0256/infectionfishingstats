package com.github.soramame0256.infectionfishingstats;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.BIG;
import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.NORMAL;

public class EventHandler {
    private static final Pattern CATCH_MSG = Pattern.compile("§7§l\\[ §r§e§lFishing §r§7§l] §r(?<fish>.*)§r§7を釣り上げた！§r");
    private static final Pattern MONEY_MSG = Pattern.compile("§9§l》 §r§e\\+(?<amount>.*)円§r");
    private static final Pattern ANNOUNCE_MSG = Pattern.compile("§9§l》 §r§7§l(?<mcid>.*)§r§7が§r(?<fish>.*)§r§7を釣り上げた！§r");
    private boolean isNextCaughtMoney = false;
    public static final String COLOR_CODE_REG = "§[0-9a-fik-or]";
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
        Matcher m = CATCH_MSG.matcher(msg);
        Matcher m2 = ANNOUNCE_MSG.matcher(msg);
        if(m2.matches()){
            String name = m2.group("fish").replaceAll(COLOR_CODE_REG,"");
            String latestCache = latestFish.replaceAll(COLOR_CODE_REG,"");
            if(m2.group("mcid").equals(Minecraft.getMinecraft().getSession().getUsername())) {
                Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("ui.toast.challenge_complete")), 1f, 0f);
                if (name.equals(latestCache)) {
                    if (StatsHolder.getFish(latestFish) != null) {
                        if (!StatsHolder.getFish(latestFish).isBig()) {
                            StatsHolder.getFish(latestFish).setBig(true);
                            StatsHolder.incrementBigCaught();
                            return;
                        }
                    }
                }
            }
        }
        if(isNextCaughtMoney && insta.getEpochSecond()+1>Instant.now().getEpochSecond()){
            Matcher m3 = MONEY_MSG.matcher(msg);
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
