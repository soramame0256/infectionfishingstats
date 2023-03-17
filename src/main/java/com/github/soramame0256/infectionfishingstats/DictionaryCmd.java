package com.github.soramame0256.infectionfishingstats;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DictionaryCmd extends CommandBase {

    @Override
    public String getName() {
        return "ifish";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ifish <dic/stat>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length==1){
            if(args[0].equals("dic")){
                StatsHolder.dump(sender, "dic");
            }else if(args[0].equals("stat")){
                StatsHolder.dump(sender, "stat");
            }
        }
    }
}
