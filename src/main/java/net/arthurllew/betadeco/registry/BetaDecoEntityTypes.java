package net.arthurllew.betadeco.registry;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.entity.KaevatorWallpaperEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public abstract class BetaDecoEntityTypes {
    /**
     * Deferred Register for entity types.
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BetaDeco.MODID);

    /**
     * Kaevator's Wallpaper entity type.
     */
    public static final Supplier<EntityType<KaevatorWallpaperEntity>> KAEVATOR_WALLPAPER_ENTITY_TYPE =
            ENTITY_TYPES.register("kaevator_wallpaper_entity", () ->
                    EntityType.Builder.<KaevatorWallpaperEntity>of(KaevatorWallpaperEntity::new, MobCategory.MISC)
                            .sized(1f, 1f)
                            .setTrackingRange(10)
                            .setUpdateInterval(Integer.MAX_VALUE)
                            .build("kaevator_wallpaper_entity"));
}
