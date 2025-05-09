package rip.diamond.disguise;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import rip.diamond.disguise.command.DisguiseCommand;
import rip.diamond.disguise.manager.DisguiseManager;

public class SimpleDisguise extends JavaPlugin {

    public static SimpleDisguise INSTANCE;

    public Lamp<BukkitCommandActor> lamp;
    public DisguiseManager disguiseManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        lamp = BukkitLamp.builder(this).build();
        lamp.register(new DisguiseCommand(this));
        disguiseManager = new DisguiseManager(this);
    }
}
