package net.arthurllew.betadeco.client;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.client.render.KaevatorWallpaperRenderer;
import net.arthurllew.betadeco.registry.BetaDecoEntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = BetaDeco.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BetaDeco.MODID, value = Dist.CLIENT)
public class BetaDecoClient {
    /**
     * Mod setup on client.
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Register renderer for wallpaper entity
        EntityRenderers.register(BetaDecoEntityTypes.KAEVATOR_WALLPAPER_ENTITY_TYPE.get(),
                KaevatorWallpaperRenderer::new);
    }
}
