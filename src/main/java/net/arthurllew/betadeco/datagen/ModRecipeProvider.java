package net.arthurllew.betadeco.datagen;

import net.arthurllew.betadeco.registry.BetaDecoBlocks;
import net.arthurllew.betadeco.registry.BetaDecoItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Deko-Mod window
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BetaDecoBlocks.DEKO_WINDOW.get(), 4)
                .pattern("NSN")
                .pattern("SXS")
                .pattern("NSN")
                .define('X', Items.LIGHT_BLUE_STAINED_GLASS)
                .define('N', Items.IRON_NUGGET)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(Items.LIGHT_BLUE_STAINED_GLASS), has(Items.LIGHT_BLUE_STAINED_GLASS))
                .unlockedBy(getHasName(Items.IRON_NUGGET), has(Items.IRON_NUGGET))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(recipeOutput);

        // RopePlus rope
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BetaDecoBlocks.ROPE.get(), 2)
                .pattern(" S ")
                .pattern(" S ")
                .pattern(" S ")
                .define('S', Items.STRING)
                .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                .save(recipeOutput);

        // Kaevator's Wallpaper
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BetaDecoItems.KAEVATOR_WALLPAPER.get(), 6)
                .pattern("PW")
                .pattern("PW")
                .pattern("PW")
                .define('P', Items.PAPER)
                .define('W', Items.WHITE_WOOL)
                .unlockedBy(getHasName(Items.PAPER), has(Items.PAPER))
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL))
                .save(recipeOutput);
    }
}
