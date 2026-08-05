package net.arthurllew.betadeco.registry;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.item.KaevatorWallpaperItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class BetaDecoItems {
    /**
     * Deferred Register for items.
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BetaDeco.MODID);
    /**
     * Deferred Register for creative tabs.
     */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetaDeco.MODID);

    /**
     * Kaevator's Wallpaper item.
     */
    public static final DeferredItem<KaevatorWallpaperItem> KAEVATOR_WALLPAPER =
            ITEMS.register("kaevator_wallpaper",
                    () -> new KaevatorWallpaperItem(new Item.Properties()));

    /**
     * Beta Deco item group.
     */
    public static final Supplier<CreativeModeTab> BETA_DECO_ITEM_GROUP =
            CREATIVE_MODE_TABS.register("beta_deco", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemgroup." + BetaDeco.MODID + ".decorations"))
                    .icon(() -> KAEVATOR_WALLPAPER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(BetaDecoBlocks.DEKO_WINDOW.get());
                        output.accept(BetaDecoBlocks.ROPE.get());
                        output.accept(KAEVATOR_WALLPAPER.get().getDefaultInstance());
                    }).build());
}
