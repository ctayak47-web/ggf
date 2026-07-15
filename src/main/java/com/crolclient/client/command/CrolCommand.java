package com.crolclient.client.command;

import com.crolclient.client.gui.ClickGuiScreen;
import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.waypoint.Waypoint;
import com.crolclient.client.waypoint.WaypointManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Регистрирует клиентскую команду /crol и её подкоманды:
 *   /crol                     — открывает ClickGUI
 *   /crol search <запрос>     — выводит в чат список модулей по поиску
 *   /crol toggle <модуль>     — включает/выключает модуль по имени
 *   /crol waypoint add <имя>  — создаёт точку с текущих координат
 *   /crol waypoint remove <имя> — удаляет точку по имени
 *   /crol waypoint list       — выводит список точек в чат
 */
public final class CrolCommand {

    private CrolCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("crol")
                .executes(ctx -> {
                    openClickGui();
                    return 1;
                })
                .then(literal("search")
                        .then(argument("query", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String query = StringArgumentType.getString(ctx, "query");
                                    runSearch(ctx, query);
                                    return 1;
                                })))
                .then(literal("toggle")
                        .then(argument("module", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "module");
                                    runToggle(ctx, name);
                                    return 1;
                                })))
                .then(literal("waypoint")
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            Waypoint wp = WaypointManager.getInstance().addAtCurrentPosition(name);
                                            ctx.getSource().sendFeedback(Text.literal("§b[CrolClient] §fТочка \"" + name + "\" сохранена."));
                                            return 1;
                                        })))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            boolean removed = WaypointManager.getInstance().remove(name);
                                            ctx.getSource().sendFeedback(Text.literal(removed
                                                    ? "§b[CrolClient] §fТочка \"" + name + "\" удалена."
                                                    : "§b[CrolClient] §cТочка не найдена."));
                                            return 1;
                                        })))
                        .then(literal("list")
                                .executes(ctx -> {
                                    listWaypoints(ctx);
                                    return 1;
                                }))));
    }

    private static void openClickGui() {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ClickGuiScreen()));
    }

    private static void runSearch(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx, String query) {
        var results = ModuleManager.getInstance().search(query);
        if (results.isEmpty()) {
            ctx.getSource().sendFeedback(Text.literal("§b[CrolClient] §fНичего не найдено по запросу \"" + query + "\"."));
            return;
        }
        StringBuilder sb = new StringBuilder("§b[CrolClient] §fНайдено: ");
        for (Module m : results) {
            sb.append(m.getName()).append(", ");
        }
        ctx.getSource().sendFeedback(Text.literal(sb.substring(0, sb.length() - 2)));
    }

    private static void runToggle(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx, String name) {
        Module module = ModuleManager.getInstance().getByName(name);
        if (module == null) {
            ctx.getSource().sendFeedback(Text.literal("§b[CrolClient] §cМодуль \"" + name + "\" не найден."));
            return;
        }
        module.toggle();
        ctx.getSource().sendFeedback(Text.literal("§b[CrolClient] §f" + module.getName() + ": " + (module.isEnabled() ? "§aвключён" : "§cвыключен")));
    }

    private static void listWaypoints(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
        var waypoints = WaypointManager.getInstance().getWaypoints();
        if (waypoints.isEmpty()) {
            ctx.getSource().sendFeedback(Text.literal("§b[CrolClient] §fСписок точек пуст."));
            return;
        }
        for (Waypoint wp : waypoints) {
            ctx.getSource().sendFeedback(Text.literal(String.format("§b- %s: §f%.0f, %.0f, %.0f (%s)",
                    wp.name, wp.x, wp.y, wp.z, wp.dimension)));
        }
    }
}
