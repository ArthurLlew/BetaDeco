package net.arthurllew.betadeco.datagen;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.registry.BetaDecoBlocks;
import net.arthurllew.betadeco.registry.BetaDecoItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BetaDeco.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(BetaDecoItems.KAEVATOR_WALLPAPER);
        simpleBlockItem(BetaDecoBlocks.ROPE);
    }

    private ItemModelBuilder simpleItem(DeferredItem<? extends Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation
                        .withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetaDeco.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(DeferredBlock<? extends Block> block) {
        return withExistingParent(block.getId().getPath(),
                ResourceLocation
                        .withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetaDeco.MODID,"block/" + block.getId().getPath()));
    }
}
