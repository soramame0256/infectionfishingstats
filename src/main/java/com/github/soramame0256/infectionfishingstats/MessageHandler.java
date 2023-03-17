package com.github.soramame0256.infectionfishingstats;

import java.util.ArrayList;
import java.util.List;

import static com.github.soramame0256.infectionfishingstats.StatsHolder.FishType.OTHER;

public class MessageHandler {
    private static final List<String> otherMsg = new ArrayList<>();
    public static void init(){
        otherMsg.add("あっ！　餌だけ取られてる...");
        otherMsg.add("糸が絡まってしまった...");
        otherMsg.add("糸が切れてしまった...");
        otherMsg.add("何も釣れなかった...");
        otherMsg.add("超大物を逃した...");
        otherMsg.add("大物に逃げられた...");
        otherMsg.add("逃げられてしまった...");
        otherMsg.add("ちっ！取り逃がしたか！");
        otherMsg.add("危ない！ クラーケンだ！");
    }
    public static void handle(String msg){
        msg = msg.replaceAll("§[0-9a-fik-or]","");
        for(String ms : otherMsg){
            if(msg.contains(ms)) {
                if (ms.contains("クラーケン")) {
                    StatsHolder.incrementFishCount(OTHER, "クラーケン");
                    EventHandler.INSTANCE.handleNext("クラーケン");
                }else{
                    StatsHolder.incrementFishCount(OTHER, "ハズレ");
                }
                return;
            }
        }
    }

}
