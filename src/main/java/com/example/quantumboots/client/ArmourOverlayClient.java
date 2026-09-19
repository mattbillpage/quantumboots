package com.example.quantumboots.client;

import com.example.quantumboots.ArmourOverhaulMod;
import com.example.quantumboots.ArmourProtection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Purely visual - draws the player's armour value, toughness, and current
 * protection percentages (fall/blast/projectile/fire) on the inventory
 * screen. Client-only, since a dedicated server has no screen to draw on.
 *
 * MC/NeoForge 26.x REWRITE NOTE:
 * This version's rendering pipeline replaced GuiGraphics with
 * GuiGraphicsExtractor and moved from immediate drawString(...) calls to a
 * "render state extraction" model. The practical difference for us:
 *   - drawString(Font, String, x, y, color, dropShadow) is gone.
 *   - .text(Font, Component, x, y, color) is the replacement - note it
 *     takes a Component, not a raw String, and has no shadow boolean.
 *   - Colors are now full ARGB, not RGB-with-implicit-alpha, so we need
 *     the 0xFF alpha prefix or the text won't render (alpha 0 = invisible).
 */
@EventBusSubscriber(modid = ArmourOverhaulMod.MOD_ID, value = Dist.CLIENT)
public final class ArmourOverlayClient {

    private ArmourOverlayClient() {}

    // ARGB now, not RGB - 0xFF alpha prefix is required or this draws nothing.
    private static final int TEXT_COLOR = 0xFF404040;

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        // Vanilla inventory screen is always 176x166, centered on the window.
        int screenWidth = event.getScreen().width;
        int screenHeight = event.getScreen().height;
        int guiLeft = (screenWidth - 176) / 2;
        int guiTop = (screenHeight - 166) / 2;

        int textX = guiLeft + 185;
        int textY = guiTop + 6;
        int lineHeight = 10;

        // NOTE: Attributes.ARMOR / ARMOR_TOUGHNESS are Mojang's own vanilla
        // attribute names - we can't rename those, they're fixed API.
        double armour = player.getAttributeValue(Attributes.ARMOR);
        double toughness = player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

        event.getGuiGraphics().text(
                mc.font,
                Component.literal(String.format("Armour: %.0f  Toughness: %.1f", armour, toughness)),
                textX, textY, TEXT_COLOR
        );
        textY += lineHeight;

        for (String line : getActiveProtections(player)) {
            event.getGuiGraphics().text(mc.font, Component.literal(line), textX, textY, TEXT_COLOR);
            textY += lineHeight;
        }
    }

    /**
     * Builds one line per damage category that currently has any
     * protection at all (skips categories at 0%).
     */
    private static List<String> getActiveProtections(Player player) {
        List<String> lines = new ArrayList<>();

        float fall = ArmourProtection.getFallProtection(player);
        if (fall > 0f) {
            lines.add(String.format("Fall Resist: %.0f%%", fall));
        }

        float blast = ArmourProtection.getBlastProtection(player);
        if (blast > 0f) {
            lines.add(String.format("Blast Resist: %.0f%%", blast));
        }

        float projectile = ArmourProtection.getProjectileProtection(player);
        if (projectile > 0f) {
            lines.add(String.format("Projectile Resist: %.0f%%", projectile));
        }

        float fire = ArmourProtection.getFireProtection(player);
        if (fire > 0f) {
            lines.add(String.format("Fire Resist: %.0f%%", fire));
        }

        return lines;
    }
}