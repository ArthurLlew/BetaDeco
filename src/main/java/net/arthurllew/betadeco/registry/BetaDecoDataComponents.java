package net.arthurllew.betadeco.registry;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.core.component.WallpaperVariant;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BetaDecoDataComponents {
    /**
     * Deferred Register for data components.
     */
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BetaDeco.MODID);

    /**
     * Wallpaper variant component.
     */
    public static final Supplier<DataComponentType<WallpaperVariant>> WALLPAPER_VARIANT_COMPONENT =
            DATA_COMPONENT_TYPES.register("wallpaper_variant", () -> DataComponentType.<WallpaperVariant>builder()
                    .persistent(WallpaperVariant.CODEC)
                    .networkSynchronized(WallpaperVariant.STREAM_CODEC)
                    .build());
}
