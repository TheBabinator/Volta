package volta.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import volta.Volta;
import volta.block.LeverVoltaBlock;
import volta.block.VoltaBlocks;

import java.util.function.Supplier;

public class VoltaBlockStateProvider extends BlockStateProvider {
    public VoltaBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Volta.ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        VoltaBlocks.GROUP_HORIZONTAL.stream().map(Supplier::get).forEach(this::deriveHorizontalBlock);
        VoltaBlocks.GROUP_SWITCH.stream().map(Supplier::get).forEach(this::deriveSwitchBlock);
    }

    private void deriveHorizontalBlock(Block block) {
        horizontalBlock(block, models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)));
    }

    private void deriveSwitchBlock(Block block) {
        ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
        ModelFile openModel = models().getExistingFile(resourceLocation);
        ModelFile closedModel = models().getExistingFile(resourceLocation.withSuffix("_closed"));
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(LeverVoltaBlock.POWERED) ? closedModel : openModel)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build());
    }
}
