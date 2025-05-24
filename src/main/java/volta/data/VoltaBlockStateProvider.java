package volta.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import volta.Volta;
import volta.block.VoltaBlocks;

import java.util.function.Supplier;

public class VoltaBlockStateProvider extends BlockStateProvider {
    public VoltaBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Volta.ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        VoltaBlocks.GROUP_HORIZONTAL.stream().map(Supplier::get).forEach(this::derivedHorizontalBlock);
    }

    private void derivedHorizontalBlock(Block block) {
        horizontalBlock(block, models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)));
    }
}
