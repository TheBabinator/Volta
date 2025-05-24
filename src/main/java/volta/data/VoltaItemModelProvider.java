package volta.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import volta.Volta;
import volta.item.VoltaItems;

import java.util.function.Supplier;

public class VoltaItemModelProvider extends ItemModelProvider {
    public VoltaItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Volta.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        VoltaItems.GROUP_ITEMS.stream().map(Supplier::get).forEach(this::basicItem);
        VoltaItems.GROUP_BLOCKS.stream().map(Supplier::get).forEach(this::blockItem);
        VoltaItems.GROUP_ELECTRONICS.stream().map(Supplier::get).forEach(this::blockItem);
        VoltaItems.GROUP_MACHINES.stream().map(Supplier::get).forEach(this::blockItem);
    }

    private void blockItem(Item item) {
        if (item instanceof BlockItem blockItem) {
            simpleBlockItem(blockItem.getBlock());
        }
    }
}
