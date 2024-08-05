package dev.gomorrha.ftick.Listener;

import dev.gomorrha.ftick.FTick;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.TimeUnit;

public class LoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Bukkit.getAsyncScheduler().runAtFixedRate(FTick.instance, task1 -> player.getScheduler().run(FTick.instance, task2 -> {
            TickData.TickReportData report = TickRegionScheduler.getCurrentRegion().getData().getRegionSchedulingHandle().getTickReport15s(System.nanoTime());
            double rawLmspt = report == null ? 0.00 : report.timePerTickData().segmentAll().average() / 1.0E6;
            String localMspt = String.format("%.2f", rawLmspt);
            int rawPing = player.getPing();

            NamedTextColor pingColor = rawPing < 70 ? NamedTextColor.GREEN : rawPing < 120 ? NamedTextColor.GOLD : NamedTextColor.RED;
            NamedTextColor msptColor = rawLmspt < 50 ? NamedTextColor.GREEN : rawLmspt < 55 ? NamedTextColor.GOLD : NamedTextColor.RED;

            player.sendPlayerListHeaderAndFooter(
                    Component.text("MSPT: ", NamedTextColor.GRAY).append(Component.text(localMspt, msptColor)),
                    Component.text("Ping: ", NamedTextColor.GRAY).append(Component.text(rawPing, pingColor))
            );
        }, null), 1, 5, TimeUnit.SECONDS);
    }
}
