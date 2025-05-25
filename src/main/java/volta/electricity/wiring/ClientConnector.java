package volta.electricity.wiring;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3f;
import volta.electricity.Connection;
import volta.electricity.LevelSimulationManager;
import volta.electricity.Simulation;
import volta.electricity.Terminal;
import volta.electricity.connections.WireConnection;
import volta.util.VoltaGraphics;

import java.util.function.BiConsumer;

public class ClientConnector {
    private static Terminal connectionPoint;
    private static WireStyle connectionStyle;
    private static BiConsumer<Terminal, Terminal> connectionHandler;
    private static Item connectionItem;

    public static InteractionResultHolder<ItemStack> interactFromItemUse(Level level, Player player, InteractionHand interactionHand, WireStyle wireStyle, BiConsumer<Terminal, Terminal> callback, Item item, boolean disconnect) {
        Simulation simulation = LevelSimulationManager.getSimulation(level);
        Vec3 eyes = player.getEyePosition();
        Vec3 look = player.getLookAngle().scale(player.blockInteractionRange());
        Terminal interactedTerminal = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (Terminal terminal : simulation.getTerminals()) {
            Vec3 origin = terminal.getWorldPosition();
            Vec3 start = eyes.subtract(origin);
            Vec3 end = look.add(start);
            BlockHitResult hit = terminal.getShape().clip(start, end, BlockPos.ZERO);
            if (hit == null) {
                continue;
            }
            double distance = hit.getLocation().distanceTo(start);
            if (distance > closestDistance) {
                continue;
            }
            interactedTerminal = terminal;
            closestDistance = distance;
        }
        if (interactedTerminal == null) {
            return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
        }
        interact(interactedTerminal, wireStyle, callback, item, disconnect);
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }

    public static void interact(Terminal terminal, WireStyle wireStyle, BiConsumer<Terminal, Terminal> callback, Item item, boolean disconnect) {
        if (connectionPoint == null || connectionItem != item) {
            connectionPoint = terminal;
            connectionStyle = wireStyle;
            connectionHandler = callback;
            connectionItem = item;
            return;
        }
        if (connectionHandler != null) {
            connectionHandler.accept(connectionPoint, terminal);
        }
        if (disconnect) {
            connectionPoint = null;
            connectionStyle = null;
            connectionHandler = null;
            connectionItem = null;
        }
    }

    public static void disconnect() {
        connectionPoint = null;
        connectionStyle = null;
        connectionHandler = null;
        connectionItem = null;
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientConnector::onPostLevelTick);
        NeoForge.EVENT_BUS.addListener(ClientConnector::onRenderLevelStage);
    }

    private static void onPostLevelTick(LevelTickEvent.Post postLevelTickEvent) {
        if (!postLevelTickEvent.getLevel().isClientSide()) {
            return;
        }
        if (connectionPoint == null || connectionItem == null) {
            return;
        }
        if (connectionPoint.isInvalid()) {
            disconnect();
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isHolding(connectionItem)) {
            return;
        }
        disconnect();
    }

    private static void onRenderLevelStage(RenderLevelStageEvent renderLevelStageEvent) {
        if (renderLevelStageEvent.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            return;
        }
        Camera camera = renderLevelStageEvent.getCamera();
        MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = renderLevelStageEvent.getPoseStack();
        float partialTick = renderLevelStageEvent.getPartialTick().getGameTimeDeltaPartialTick(true);
        renderLevelTerminals(bufferSource, poseStack, camera, level);
        renderLevelConnections(bufferSource, poseStack, camera, level);
        renderNewConnection(bufferSource, poseStack, camera, level, player, partialTick);
    }

    private static void renderLevelTerminals(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, Level level) {
        Vec3 cameraPosition = camera.getPosition();
        Simulation simulation = LevelSimulationManager.getSimulation(level);
        for (Terminal terminal : simulation.getTerminals()) {
            VoxelShape shape = terminal.getShape();
            Vec3 terminalPosition = terminal.getWorldPosition();
            Vector3f position = terminalPosition.subtract(cameraPosition).toVector3f();
            double potential = terminal.getPotential();
            double r, g, b;
            if (potential > 0.0) {
                r = 1.0;
                g = Math.pow(0.98, potential);
                b = Math.pow(0.92, potential);
            } else if (potential < 0.0) {
                r = Math.pow(0.92, -potential);
                g = Math.pow(0.98, -potential);
                b = 1.0;
            } else {
                r = 1.0;
                g = 1.0;
                b = 1.0;
            }
            VoltaGraphics.renderShape(bufferSource, poseStack, shape, position, (float) r, (float) g, (float) b, 0.5f);
        }
    }

    private static void renderLevelConnections(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, Level level) {
        Vec3 cameraPosition = camera.getPosition();
        Simulation simulation = LevelSimulationManager.getSimulation(level);
        for (Terminal positive : simulation.getTerminals()) {
            Vec3 positivePosition = positive.getWorldPosition();
            Vector3f source = positivePosition.subtract(cameraPosition).toVector3f();
            int sourceLight = VoltaGraphics.getLight(level, BlockPos.containing(positivePosition));
            for (Terminal negative : simulation.getConnectedTerminals(positive)) {
                Vec3 negativePosition = negative.getWorldPosition();
                Vector3f destination = negativePosition.subtract(cameraPosition).toVector3f();
                int destinationLight = VoltaGraphics.getLight(level, BlockPos.containing(negativePosition));
                for (Connection connection : simulation.getConnections(positive, negative)) {
                    if (connection instanceof WireConnection wireConnection) {
                        WireStyle wireStyle = wireConnection.getItem().getWireType().getWireStyle();
                        VoltaGraphics.renderWire(bufferSource, poseStack, wireStyle, source, destination, sourceLight, destinationLight);
                    }
                }
            }
        }
    }

    private static void renderNewConnection(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, Level level, Player player, float partialTick) {
        if (connectionPoint == null || connectionStyle == null) {
            return;
        }
        Vec3 connectionPointPosition = connectionPoint.getWorldPosition();
        Vec3 destinationPosition = player.getRopeHoldPosition(partialTick);
        Vec3 cameraPosition = camera.getPosition();
        Vector3f source = connectionPointPosition.subtract(cameraPosition).toVector3f();
        Vector3f destination = destinationPosition.subtract(cameraPosition).toVector3f();
        int sourceLight = VoltaGraphics.getLight(level, BlockPos.containing(connectionPointPosition));
        int destinationLight = VoltaGraphics.getLight(level, BlockPos.containing(destinationPosition));
        VoltaGraphics.renderWire(bufferSource, poseStack, connectionStyle, source, destination, sourceLight, destinationLight);
    }
}
