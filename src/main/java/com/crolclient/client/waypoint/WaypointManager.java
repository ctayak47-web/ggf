package com.crolclient.client.waypoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранит список точек-меток (waypoints) игрока, создаваемых с ТЕКУЩИХ координат
 * по хоткею или команде /crol waypoint add <имя>. Список сохраняется в JSON
 * между сессиями в .minecraft/config/crolclient/waypoints.json.
 */
public class WaypointManager {

    private static WaypointManager instance;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;
    private final List<Waypoint> waypoints = new ArrayList<>();

    public static WaypointManager getInstance() {
        if (instance == null) {
            instance = new WaypointManager();
        }
        return instance;
    }

    private WaypointManager() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("crolclient");
        this.file = configDir.resolve("waypoints.json");
        load();
    }

    /** Создаёт новую точку с текущих координат игрока. */
    public Waypoint addAtCurrentPosition(String name) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return null;

        String dimension = mc.world != null ? mc.world.getRegistryKey().getValue().toString() : "minecraft:overworld";
        Waypoint wp = new Waypoint(name, player.getX(), player.getY(), player.getZ(), dimension);
        waypoints.add(wp);
        save();
        return wp;
    }

    public boolean remove(String name) {
        boolean removed = waypoints.removeIf(w -> w.name.equalsIgnoreCase(name));
        if (removed) save();
        return removed;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    /** Возвращает только точки текущего измерения — используется для отображения на HUD. */
    public List<Waypoint> getWaypointsForCurrentDimension() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return waypoints;
        String currentDim = mc.world.getRegistryKey().getValue().toString();
        List<Waypoint> result = new ArrayList<>();
        for (Waypoint w : waypoints) {
            if (currentDim.equals(w.dimension)) {
                result.add(w);
            }
        }
        return result;
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(waypoints, writer);
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось сохранить waypoints: " + e.getMessage());
        }
    }

    private void load() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<Waypoint>>() {}.getType();
            List<Waypoint> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                waypoints.clear();
                waypoints.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось загрузить waypoints: " + e.getMessage());
        }
    }

    /** Вычисляет расстояние по прямой от игрока до точки — для отображения на HUD. */
    public static double distanceFromPlayer(Waypoint wp) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;
        double dx = wp.x - mc.player.getX();
        double dy = wp.y - mc.player.getY();
        double dz = wp.z - mc.player.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /** Вычисляет направление (азимут в градусах, 0 = юг, по часовой) от игрока до точки. */
    public static double directionFromPlayer(Waypoint wp) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;
        double dx = wp.x - mc.player.getX();
        double dz = wp.z - mc.player.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        angle %= 360;
        if (angle < 0) angle += 360;
        return angle;
    }
}
