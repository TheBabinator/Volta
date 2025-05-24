package volta.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import volta.electricity.wiring.WireStyle;

public class VoltaGraphics {
    public static void renderShape(MultiBufferSource bufferSource, PoseStack poseStack, VoxelShape shape, Vector3f position, float r, float g, float b, float a) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            float xn = (float) (x1 - x0);
            float yn = (float) (y1 - y0);
            float zn = (float) (z1 - z0);
            float rn = Mth.sqrt(xn * xn + yn * yn + zn * zn);
            xn /= rn;
            yn /= rn;
            zn /= rn;
            buffer.addVertex(pose, position.x + (float) x0, position.y + (float) y0, position.z + (float) z0)
                    .setColor(r, g, b, a)
                    .setNormal(pose, xn, yn, zn);
            buffer.addVertex(pose, position.x + (float) x1, position.y + (float) y1, position.z + (float) z1)
                    .setColor(r, g, b, a)
                    .setNormal(pose, xn, yn, zn);
        });
    }

    public static void renderWire(MultiBufferSource bufferSource, PoseStack poseStack, WireStyle wireStyle, Vector3f source, Vector3f destination, int sourceLight, int destinationLight) {
        Vector3f midpoint = new Vector3f(source).lerp(destination, 0.5f);
        renderHalfWire(bufferSource, poseStack, wireStyle, source, midpoint, sourceLight, destinationLight);
        renderHalfWire(bufferSource, poseStack, wireStyle, destination, midpoint, destinationLight, sourceLight);
    }

    public static void renderHalfWire(MultiBufferSource bufferSource, PoseStack poseStack, WireStyle wireStyle, Vector3f source, Vector3f midpoint, int sourceLight, int destinationLight) {
        int color = wireStyle.getColor();
        int accentColor = wireStyle.getAccentColor();
        float r = FastColor.ARGB32.red(color) / 255f;
        float g = FastColor.ARGB32.green(color) / 255f;
        float b = FastColor.ARGB32.blue(color) / 255f;
        float R = FastColor.ARGB32.red(accentColor) / 255f;
        float G = FastColor.ARGB32.green(accentColor) / 255f;
        float B = FastColor.ARGB32.blue(accentColor) / 255f;
        float radius = wireStyle.getRadius();

        Vector3f vector = new Vector3f(midpoint).sub(source);
        Vector3f down = new Vector3f(0f, -0.0625f, 0f).mul(vector.length());
        Vector3f upright = new Vector3f(0f, 1f, 0f);
        Vector3f sideways = new Vector3f(vector).cross(upright);

        if (sideways.length() < 0.0625f) {
            sideways.set(1f, 0f, 0f);
        }

        upright.set(vector).cross(sideways).normalize().mul(radius);
        sideways.normalize().mul(radius);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.leash());
        PoseStack.Pose pose = poseStack.last();

        int n = 12;
        vector.div(n);
        Vector3f segment = new Vector3f(midpoint);
        Vector3f adjustedSegment = new Vector3f();
        for (int i = n; i >= 0; i--) {
            float t = (float) i / n;
            int packedLight = blendLight(sourceLight, destinationLight, t / 2f);
            adjustedSegment.set(down).mul(1f - (t - 1f) * (t - 1f)).add(segment);
            if (i % 3 == 1) {
                renderWireSegment(buffer, pose, adjustedSegment, upright, R, G, B, packedLight);
            } else {
                renderWireSegment(buffer, pose, adjustedSegment, upright, r, g, b, packedLight);
            }
            segment.sub(vector);
        }
        segment.add(vector);
        for (int i = 0; i <= n; i++) {
            float t = (float) i / n;
            int packedLight = blendLight(sourceLight, destinationLight, t / 2f);
            adjustedSegment.set(down).mul(1f - (t - 1f) * (t - 1f)).add(segment);
            if (i % 3 == 2) {
                renderWireSegment(buffer, pose, adjustedSegment, sideways, R, G, B, packedLight);
            } else {
                renderWireSegment(buffer, pose, adjustedSegment, sideways, r, g, b, packedLight);
            }
            segment.add(vector);
        }
    }

    public static void renderWireSegment(VertexConsumer buffer, PoseStack.Pose pose, Vector3f position, Vector3f axis, float r, float g, float b, int packedLight) {
        buffer.addVertex(pose, position.x + axis.x, position.y + axis.y, position.z + axis.z);
        buffer.setColor(r, g, b, 1f);
        buffer.setLight(packedLight);
        buffer.addVertex(pose, position.x - axis.x, position.y - axis.y, position.z - axis.z);
        buffer.setColor(r, g, b, 1f);
        buffer.setLight(packedLight);
    }

    public static int blendInt(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }

    public static int blendLight(int a, int b, float t) {
        int block = blendInt(LightTexture.block(a), LightTexture.block(b), t);
        int sky = blendInt(LightTexture.sky(a), LightTexture.sky(b), t);
        return LightTexture.pack(block, sky);
    }

    public static int getLight(BlockAndTintGetter level, BlockPos blockPos) {
        return LightTexture.pack(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos));
    }
}
