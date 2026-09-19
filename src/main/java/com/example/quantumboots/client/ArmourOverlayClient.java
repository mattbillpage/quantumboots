package com.example.quantumboots.client;

import com.example.quantumboots.ArmourOverhaulMod;
import com.example.quantumboots.ArmourProtection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;
import java.util.function.ToDoubleFunction;


@EventBusSubscriber(modid = ArmourOverhaulMod.MOD_ID, value = Dist.CLIENT)
public final class ArmourOverlayClient {

    private ArmourOverlayClient() {}

    // ---------------------------------------------------------------- state
    // Session-only. Resets to true every time the game launches.
    private static boolean overlayEnabled = true;

    // ------------------------------------------------------------ textures
    private static Identifier gui(String name) {
        return Identifier.fromNamespaceAndPath("textures", "gui/" + name + ".png");
    }

    private static final Identifier TOGGLE_ICON = gui("protection_toggle");

    /** Pixel size of your source PNGs (assumed square). Change if yours differ. */
    private static final int TEX_SIZE = 16;

    private record Stat(Identifier icon, ToDoubleFunction<Player> value) {}

    // Display order: projectile, blast, fall, fire.
    private static final List<Stat> STATS = List.of(
            new Stat(gui("protection_projectile"), ArmourProtection::getProjectileProtection),
            new Stat(gui("protection_blast"), ArmourProtection::getBlastProtection),
            new Stat(gui("protection_fall"), ArmourProtection::getFallProtection),
            new Stat(gui("protection_fire"), ArmourProtection::getFireProtection)
    );

    /** Protection values are percentages; 100 = full bar. */
    private static final float MAX_PROTECTION = 100f;

    // -------------------------------------------------------------- layout
    // All values are in GUI pixels, measured from the inventory's top-left.
    private static final int PANEL_W = 45;
    private static final int PANEL_H = 44;
    private static final int PANEL_GAP = 3;        // gap between panel and inventory
    private static final int PANEL_TOP_OFFSET = 4; // panel starts this far below inventory top

    private static final int ROW_PITCH = 10;       // vertical distance between rows
    private static final int PAD_X = 3;
    private static final int PAD_Y = 2;
    private static final int ICON_SIZE = 12;
    private static final int ICON_BAR_GAP = 3;
    private static final int BAR_H = 5;

    // The black player-preview box in the vanilla inventory: x 26..75, y 8..77.
    private static final int PREVIEW_RIGHT = 75;
    private static final int PREVIEW_TOP = 8;
    private static final int TOGGLE_SIZE = 9;
    private static final int TOGGLE_X = PREVIEW_RIGHT - TOGGLE_SIZE - 1;
    private static final int TOGGLE_Y = PREVIEW_TOP + 1;

    // --------------------------------------------------------------- colors (ARGB)
    private static final int PANEL_BORDER = 0xFF000000;
    private static final int PANEL_BG = 0xE0303030;
    private static final int BAR_BORDER = 0xFF000000;
    private static final int BAR_TRACK = 0xFF3A3A3A;
    private static final int DISABLED_TINT = 0xA0000000;

    // -------------------------------------------------------------- render
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        GuiGraphicsExtractor g = event.getGuiGraphics();

        // Use the screen's real position so it follows the inventory when the
        // recipe book opens and shifts everything sideways.
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();

        drawToggle(g, guiLeft + TOGGLE_X, guiTop + TOGGLE_Y);

        if (!overlayEnabled) {
            return;
        }

        int panelX = guiLeft - PANEL_GAP - PANEL_W;
        int panelY = guiTop + PANEL_TOP_OFFSET;

        // Background: 1px black border, dark body.
        g.fill(panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, PANEL_BORDER);
        g.fill(panelX + 1, panelY + 1, panelX + PANEL_W - 1, panelY + PANEL_H - 1, PANEL_BG);

        int barX = panelX + PAD_X + ICON_SIZE + ICON_BAR_GAP;
        int barW = PANEL_W - (barX - panelX) - PAD_X;

        int rowY = panelY + PAD_Y;
        for (Stat stat : STATS) {
            drawIcon(g, stat.icon(), panelX + PAD_X, rowY, ICON_SIZE);

            float fraction = Mth.clamp((float) stat.value().applyAsDouble(player) / MAX_PROTECTION, 0f, 1f);
            drawBar(g, barX, rowY + (ICON_SIZE - BAR_H) / 2, barW, BAR_H, fraction);

            rowY += ROW_PITCH;
        }
    }

    private static void drawToggle(GuiGraphicsExtractor g, int x, int y) {
        drawIcon(g, TOGGLE_ICON, x, y, TOGGLE_SIZE);
        if (!overlayEnabled) {
            // Darken the icon so it reads as "off".
            g.fill(x, y, x + TOGGLE_SIZE, y + TOGGLE_SIZE, DISABLED_TINT);
        }
    }

    /** Draws the whole texture scaled to size x size. */
    private static void drawIcon(GuiGraphicsExtractor g, Identifier tex, int x, int y, int size) {
        g.blit(RenderPipelines.GUI_TEXTURED, tex,
                x, y,
                0, 0,
                size, size,
                TEX_SIZE, TEX_SIZE,
                TEX_SIZE, TEX_SIZE);
    }

    private static void drawBar(GuiGraphicsExtractor g, int x, int y, int w, int h, float fraction) {
        // 1px border, then track, then fill.
        g.fill(x, y, x + w, y + h, BAR_BORDER);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, BAR_TRACK);

        int innerW = w - 2;
        int fillW = Math.round(innerW * fraction);
        if (fillW > 0) {
            g.fill(x + 1, y + 1, x + 1 + fillW, y + h - 1, barColor(fraction));
        }
    }

    /** Red (empty) -> yellow -> green (full). */
    private static int barColor(float fraction) {
        return 0xFF000000 | Mth.hsvToRgb(fraction / 3f, 0.95f, 0.75f);
    }

    // --------------------------------------------------------------- input
    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen) || event.getButton() != 0) {
            return;
        }

        int x = screen.getGuiLeft() + TOGGLE_X;
        int y = screen.getGuiTop() + TOGGLE_Y;
        double mx = event.getMouseX();
        double my = event.getMouseY();

        if (mx >= x && mx < x + TOGGLE_SIZE && my >= y && my < y + TOGGLE_SIZE) {
            overlayEnabled = !overlayEnabled;
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            event.setCanceled(true); // don't let the click fall through to the screen
        }
    }
}