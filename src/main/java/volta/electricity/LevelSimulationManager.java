package volta.electricity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;
import volta.VoltaConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelSimulationManager {
    private static final Map<Level, Simulation> SIMULATIONS = new ConcurrentHashMap<>();

    public static Simulation getSimulation(Level level) {
        return SIMULATIONS.computeIfAbsent(level, ignored -> new Simulation());
    }

    public static @Nullable Level findLevel(Simulation simulation) {
        for (Map.Entry<Level, Simulation> entry : SIMULATIONS.entrySet()) {
            if (entry.getValue().equals(simulation)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(LevelSimulationManager::onPostLevelTick);
        NeoForge.EVENT_BUS.addListener(LevelSimulationManager::onUnloadLevel);
    }

    private static void onPostLevelTick(LevelTickEvent.Post postLevelTickEvent) {
        Level level = postLevelTickEvent.getLevel();
        if (level.isClientSide()) {
            return;
        }
        Simulation simulation = SIMULATIONS.get(level);
        if (simulation == null) {
            return;
        }
        simulation.simulate(VoltaConfig.SIMULATION_QUALITY.getAsInt(), 0.05);
    }

    private static void onUnloadLevel(LevelEvent.Unload unloadLevelEvent) {
        LevelAccessor levelAccessor = unloadLevelEvent.getLevel();
        if (levelAccessor instanceof Level level) {
            SIMULATIONS.remove(level);
        }
    }
}
