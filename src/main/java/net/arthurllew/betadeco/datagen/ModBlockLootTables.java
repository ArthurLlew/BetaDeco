package net.arthurllew.betadeco.datagen;

import net.arthurllew.betadeco.registry.BetaDecoBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModBlockLootTables extends BlockLootSubProvider {
    protected ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        // Drop themselves
        this.dropSelf(BetaDecoBlocks.DEKO_WINDOW.get());
        this.dropSelf(BetaDecoBlocks.ROPE.get());
        this.dropSelf(BetaDecoBlocks.BETA_GARVEL.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BetaDecoBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
