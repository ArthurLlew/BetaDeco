package net.arthurllew.betadeco.datagen;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.registry.BetaDecoBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BetaDeco.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BetaDecoBlocks.BETA_GARVEL);
    }

    private void blockWithItem(DeferredBlock<?> blockRegistry) {
        simpleBlockWithItem(blockRegistry.get(), cubeAll(blockRegistry.get()));
    }
}
