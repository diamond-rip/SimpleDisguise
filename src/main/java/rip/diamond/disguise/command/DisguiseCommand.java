package rip.diamond.disguise.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import rip.diamond.disguise.SimpleDisguise;
import rip.diamond.disguise.util.CC;

@Command("disguise")
@CommandPermission("simpledisguise.command.disguise")
@RequiredArgsConstructor
public class DisguiseCommand {

    private final SimpleDisguise plugin;

    @CommandPlaceholder
    public void root(BukkitCommandActor actor, String name, String skinName) {
        Player player = actor.requirePlayer();

        plugin.disguiseManager.disguise(player, name, skinName);
        player.sendMessage(CC.GREEN + "Disguised as " + name + " (with skin " + skinName + ")");
    }

}
