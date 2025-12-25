package net.arthurllew.betadeco.sound;

import net.arthurllew.betadeco.BetaDeco;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BetaDecoSounds {
    /**
     * Deferred Register for sound events.
     */
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BetaDeco.MODID);

    // Deko-Mod window sounds
    public static final Supplier<SoundEvent> DEKO_WINDOW_SOUND_OPEN = registerSoundEvent("deko_window_open");
    public static final Supplier<SoundEvent> DEKO_WINDOW_SOUND_CLOSE = registerSoundEvent("deko_window_close");

    /**
     * Registers sound event.
     * @param name sound event name (also its resource location).
     * @return registered sound.
     */
    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BetaDeco.MODID, name)));
    }
}
