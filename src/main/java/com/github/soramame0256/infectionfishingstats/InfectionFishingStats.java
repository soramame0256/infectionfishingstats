package com.github.soramame0256.infectionfishingstats;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;

@Mod(
        modid = InfectionFishingStats.MOD_ID,
        name = InfectionFishingStats.MOD_NAME,
        version = InfectionFishingStats.VERSION
)
public class InfectionFishingStats {

    public static final String MOD_ID = "infectionfishingstats";
    public static final String MOD_NAME = "InfectionFishingStats";
    public static final String VERSION = "1.2-SNAPSHOT-B";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static InfectionFishingStats INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        StatsHolder.init();
        MessageHandler.init();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ClientCommandHandler.instance.registerCommand(new DictionaryCmd());
        Runtime.getRuntime().addShutdownHook(new Thread(StatsHolder::serializeAndFlush));
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

}
