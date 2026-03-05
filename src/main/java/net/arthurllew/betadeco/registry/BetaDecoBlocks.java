package net.arthurllew.betadeco.registry;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.block.RopeBlock;
import net.arthurllew.betadeco.block.WindowBlock;
import net.arthurllew.betadeco.item.RopeBlockItem;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BetaDecoBlocks {
    /**
     * Deferred Register for blocks.
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BetaDeco.MODID);

    /**
     * Beta gravel.
     */
    public static final DeferredBlock<ColoredFallingBlock> BETA_GARVEL = registerBlock("beta_gravel",
            () -> new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)));

    /**
     * Deko-Mod window.
     */
    public static final DeferredBlock<WindowBlock> DEKO_WINDOW = registerBlock("deko_window",
            () -> new WindowBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLUE_STAINED_GLASS)));

    /**
     * Rope mod rope :).
     */
    public static final DeferredBlock<RopeBlock> ROPE = registerRopeBlock("rope",
            () -> new RopeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)));

    /**
     * Registers block and its item.
     * @param name block id.
     * @param block block supplier.
     * @return registered block.
     * @param <T> block type.
     */
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> reg = BLOCKS.register(name, block);
        BetaDecoItems.ITEMS.register(name, () -> new BlockItem(reg.get(), new Item.Properties()));
        return reg;
    }

    /**
     * Registers rope block and its item.
     * @param name block id.
     * @param block block supplier.
     * @return registered block.
     * @param <T> block type.
     */
    private static <T extends Block> DeferredBlock<T> registerRopeBlock(String name, Supplier<T> block) {
        DeferredBlock<T> reg = BLOCKS.register(name, block);
        BetaDecoItems.ITEMS.register(name, () -> new RopeBlockItem(reg.get(), new Item.Properties()));
        return reg;
    }
}
