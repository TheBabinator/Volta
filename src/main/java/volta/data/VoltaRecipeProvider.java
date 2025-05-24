package volta.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import volta.item.VoltaItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class VoltaRecipeProvider extends RecipeProvider {
    public VoltaRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        fillWireSpool(recipeOutput, VoltaItems.COPPER_WIRE_SPOOL, VoltaItems.COPPER_WIRE);
        fillWireSpool(recipeOutput, VoltaItems.WAXED_COPPER_WIRE_SPOOL, VoltaItems.WAXED_COPPER_WIRE);
        fillWireSpool(recipeOutput, VoltaItems.INSULATED_COPPER_WIRE_SPOOL, VoltaItems.INSULATED_COPPER_WIRE);
    }

    private void fillWireSpool(RecipeOutput recipeOutput, Supplier<Item> result, Supplier<Item> ingredient) {
        new ShapedRecipeBuilder(RecipeCategory.MISC, new ItemStack(result.get()))
                .define('X', ingredient.get())
                .define('O', VoltaItems.EMPTY_WIRE_SPOOL.get())
                .pattern(" X ")
                .pattern("XOX")
                .pattern(" X ")
                .unlockedBy("has_wire", has(ingredient.get()))
                .unlockedBy("has_spool", has(VoltaItems.EMPTY_WIRE_SPOOL.get()))
                .save(recipeOutput);
    }
}
