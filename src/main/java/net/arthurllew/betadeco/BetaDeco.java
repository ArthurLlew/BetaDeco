package net.arthurllew.betadeco;

import net.arthurllew.betadeco.registry.*;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BetaDeco.MODID)
public class BetaDeco {
    /**
     * Mod ID.
     */
    public static final String MODID = "betadeco";

    /**
     * Mod constructor. Performs basic mod init.
     */
    public BetaDeco(IEventBus modEventBus) {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Registers to the mod event bus
        BetaDecoDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        BetaDecoEntityTypes.ENTITY_TYPES.register(modEventBus);
        BetaDecoItems.ITEMS.register(modEventBus);
        BetaDecoItems.CREATIVE_MODE_TABS.register(modEventBus);
        BetaDecoSounds.SOUND_EVENTS.register(modEventBus);
        BetaDecoBlocks.BLOCKS.register(modEventBus);
    }

    /**
     * Common mod setup event handler.
     * @param event common setup event
     */
    private void commonSetup(FMLCommonSetupEvent event) {}
}
