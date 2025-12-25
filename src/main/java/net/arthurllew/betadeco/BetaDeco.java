package net.arthurllew.betadeco;

import net.arthurllew.betadeco.block.BetaDecoBlocks;
import net.arthurllew.betadeco.entity.BetaDecoEntityTypes;
import net.arthurllew.betadeco.item.BetaDecoItems;
import net.arthurllew.betadeco.sound.BetaDecoSounds;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BetaDeco.MODID)
public class BetaDeco {
    /**
     * Mod ID.
     */
    public static final String MODID = "betadeco";

    /**
     * Basic mod init.
     */
    public BetaDeco(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Registers to the mod event bus
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
