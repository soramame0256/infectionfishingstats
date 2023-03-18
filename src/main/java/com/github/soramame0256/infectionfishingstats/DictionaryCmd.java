package com.github.soramame0256.infectionfishingstats;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryCmd extends CommandBase {
    private static final Map<Integer, List<String>> ARG_FORMATS = new HashMap<>();
    static{
        List<String> l = new ArrayList<>();
        l.add("dic");
        l.add("stat");
        ARG_FORMATS.put(1, l);
        l = new ArrayList<>();
        l.add("sort");
        ARG_FORMATS.put(2, l);
        l = new ArrayList<>();
        l.add("name");
        l.add("price");
        l.add("count");
        l.add("big");
        ARG_FORMATS.put(3, l);
    }
    @Override
    public String getName() {
        return "ifish";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ifish <dic [<sort name/price/count/big>] [<page(>=1)>]/stat>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> strs = new ArrayList<>();
        if(args.length>=1 && args[0].equalsIgnoreCase("stat")) return strs;
        args[args.length-1]=args[args.length-1].toLowerCase();
        if(ARG_FORMATS.containsKey(args.length)) {
            for (String s : ARG_FORMATS.get(args.length)) {
                if (s.startsWith(args[args.length - 1])) {
                    strs.add(s);
                }
            }
        }
        return strs;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int a;
        if(args.length>=1){
            if(args[0].equals("dic")){
                if(args.length == 2){
                    if(args[1].equalsIgnoreCase("sort")){
                        sender.sendMessage(new TextComponentString(getUsage(sender)));
                    }else {
                        if (args[1].equalsIgnoreCase("all")) {
                            StatsHolder.dump(sender, "dic");
                        } else if ((a = Integer.parseInt(args[1])) >= 1) {
                            StatsHolder.dumpBySpecific(sender, "dic", "none", a);
                        }else{
                            sender.sendMessage(new TextComponentString(getUsage(sender)));
                        }
                    }
                }else if(args.length==1) {
                    StatsHolder.dumpBySpecific(sender,"dic","none",1);
                }else{
                    if(args.length==3 && args[1].equalsIgnoreCase("sort")){
                        if(ARG_FORMATS.get(3).contains(args[2].toLowerCase())){
                            StatsHolder.dumpBySpecific(sender, "dic", args[2], 1);
                        }else{
                            sender.sendMessage(new TextComponentString(getUsage(sender)));
                        }
                    }else if(args.length==4 && args[1].equalsIgnoreCase("sort")){
                        if(ARG_FORMATS.get(3).contains(args[2].toLowerCase()) && (a = Integer.parseInt(args[3])) >= 1){
                            StatsHolder.dumpBySpecific(sender, "dic", args[2], a);
                        }else{
                            sender.sendMessage(new TextComponentString(getUsage(sender)));
                        }
                    }else{
                        sender.sendMessage(new TextComponentString(getUsage(sender)));
                    }
                }
            }else if(args[0].equals("stat")){
                StatsHolder.dump(sender, "stat");
            }
        }
    }
}
