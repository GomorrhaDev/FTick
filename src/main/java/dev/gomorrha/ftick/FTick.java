package dev.gomorrha.ftick;

import dev.gomorrha.ftick.Listener.LoginListener;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegions;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class FTick extends JavaPlugin {
    public static FTick instance;
    public static double gmspt;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
        startMsptUpdater();
    }

    private void startMsptUpdater() {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(instance, task -> gmspt = getGlobalMspt(), 10, 200);
    }

    public static double getMspt() {
        return gmspt;
    }

    public static double getTick() {
        return gmspt;
    }

    public static double getGlobalMspt() {
        List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> ((CraftWorld) world).getHandle().regioniser.computeForAllRegions(regions::add));
        return regions.stream().mapToDouble(region -> {
            TickData.TickReportData report = region.getData().getRegionSchedulingHandle().getTickReport15s(System.nanoTime());
            return report == null ? 20.0 : report.tpsData().segmentAll().average();
        }).average().orElse(20.0) / 1.0E6;
    }
}
