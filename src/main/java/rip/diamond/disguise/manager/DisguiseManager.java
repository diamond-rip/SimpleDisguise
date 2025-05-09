package rip.diamond.disguise.manager;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.diamond.disguise.SimpleDisguise;
import rip.diamond.disguise.util.DisguiseUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DisguiseManager {

    public final SimpleDisguise plugin;

    @SneakyThrows
    public void disguise(Player player, String name, String skinName) {
        boolean flying = player.isFlying();
        boolean allowFlight = player.getAllowFlight();

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();

        //Get skin and change skin
        String uuid = DisguiseUtil.readUUID(skinName);;
        JsonObject profileData = DisguiseUtil.readData(uuid);
        String value = profileData.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        String signature = profileData.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("signature").getAsString();

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, Collections.singleton(entityPlayer));
        for (Player online : Bukkit.getOnlinePlayers()) {
            sendPacket(online, packetPlayOutPlayerInfoRemove);
        }

        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        for (Player online : Bukkit.getOnlinePlayers()) {
            sendPacket(online, packetPlayOutEntityDestroy);
        }

        changeField(gameProfile, "name", name);
        changeField(entityPlayer, "displayName", name);

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfoAdd = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, Collections.singleton(entityPlayer));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.canSee(player)) {
                sendPacket(online, packetPlayOutPlayerInfoAdd);
            }
        }

        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        for (Player online : Bukkit.getOnlinePlayers()) {
            sendPacket(online, packetPlayOutNamedEntitySpawn);
        }

        Stream.of(0, 1, 2, 3).forEach(i -> {
            PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(entityPlayer.getId(), i, entityPlayer.getEquipment(i));
            for (Player online : Bukkit.getOnlinePlayers()) {
                sendPacket(online, packetPlayOutEntityEquipment);
            }
        });

        int held = player.getInventory().getHeldItemSlot();
        sendPacket(player, packetPlayOutEntityDestroy);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            moveToWorld(player);
            player.setFlying(flying);
            player.setAllowFlight(allowFlight);
            player.getInventory().setHeldItemSlot(held);

            player.updateInventory();
        });
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @SneakyThrows
    private Field assessField(Object object, String fieldName) {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    @SneakyThrows
    private void changeField(Object object, String fieldName, Object to) {
        Field field = assessField(object, fieldName);
        field.set(object, to);
    }

    @SneakyThrows
    private void moveToWorld(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        int dimension = nmsPlayer.dimension;

        PlayerConnection connection = nmsPlayer.playerConnection;
        Field minecraftServerField = assessField(connection, "minecraftServer");
        MinecraftServer minecraftServer = (MinecraftServer) minecraftServerField.get(connection);

        minecraftServer.getPlayerList().moveToWorld(nmsPlayer, dimension, true, player.getLocation(), false);
    }

}
