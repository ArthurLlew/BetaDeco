package net.arthurllew.betadeco.client.event;

import net.arthurllew.betadeco.BetaDeco;
import net.arthurllew.betadeco.core.component.WallpaperVariant;
import net.arthurllew.betadeco.registry.BetaDecoDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = BetaDeco.MODID, value = Dist.CLIENT)
public class ItemTooltipListener {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        // Check if custom component is present
        if (itemStack.has(BetaDecoDataComponents.WALLPAPER_VARIANT_COMPONENT.get())) {
            // Get component value
            WallpaperVariant variant = itemStack.get(BetaDecoDataComponents.WALLPAPER_VARIANT_COMPONENT.get());
            // If value is present
            if (variant != null) {
                // Create additional tooltip string
                Component tooltipLine = Component
                        .translatable("tooltip.betadeco.kaevator_wallpaper", variant.value())
                        .withStyle(ChatFormatting.GOLD);
                // Append it to the existing tooltip list
                event.getToolTip().add(tooltipLine);
            }
        }
    }
}
