package com.example.quantumboots;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/**
 * Protection values for every vanilla armour piece.
 *
 * Combat/Blast/Fall/Fire values are taken directly from Better Than
 * Adventure's armour table: https://bta.miraheze.org/wiki/Armor
 *
 * LIGHTNING is a category BTA doesn't have 
 * Every other material contributes 0 lightning protection by default
 */
public final class ArmourProtection {

    private ArmourProtection() {}

    // Index positions within each float[] entry below.
    private static final int COMBAT = 0;
    private static final int BLAST = 1;
    private static final int FALL = 2;
    private static final int FIRE = 3;
    private static final int LIGHTNING = 4;

    private static final float PROTECTION_CAP = 99f;

    // { Combat, Blast, Fall, Fire, Lightning } per piece.
    private static final Map<Item, float[]> PROTECTION_TABLE = new HashMap<>();
    static {
        // --- Leather: specializes in Fall ---
        PROTECTION_TABLE.put(Items.LEATHER_HELMET,     new float[]{3f, 3f, 18f, 3f, 0f});
        PROTECTION_TABLE.put(Items.LEATHER_CHESTPLATE, new float[]{8f, 8f, 48f, 8f, 0f});
        PROTECTION_TABLE.put(Items.LEATHER_LEGGINGS,   new float[]{6f, 6f, 36f, 6f, 0f});
        PROTECTION_TABLE.put(Items.LEATHER_BOOTS,      new float[]{3f, 3f, 18f, 3f, 0f});

        // --- Copper: specializes in Lightning (this mod's own addition) ---
        PROTECTION_TABLE.put(Items.COPPER_HELMET,     new float[]{5f, 5f, 5f, 5f, 15f});
        PROTECTION_TABLE.put(Items.COPPER_CHESTPLATE, new float[]{13f, 13f, 13f, 13f, 40f});
        PROTECTION_TABLE.put(Items.COPPER_LEGGINGS,   new float[]{10f, 10f, 10f, 10f, 30f});
        PROTECTION_TABLE.put(Items.COPPER_BOOTS,      new float[]{5f, 5f, 5f, 5f, 15f});

        // --- Iron: flat across Combat/Blast/Fall/Fire, no specialty ---
        PROTECTION_TABLE.put(Items.IRON_HELMET,        new float[]{6.75f, 6.75f, 6.75f, 6.75f, 0f});
        PROTECTION_TABLE.put(Items.IRON_CHESTPLATE,    new float[]{18f, 18f, 18f, 18f, 0f});
        PROTECTION_TABLE.put(Items.IRON_LEGGINGS,      new float[]{13.5f, 13.5f, 13.5f, 13.5f, 0f});
        PROTECTION_TABLE.put(Items.IRON_BOOTS,         new float[]{6.75f, 6.75f, 6.75f, 6.75f, 0f});

        // --- Gold: flat across Combat/Blast/Fall/Fire, higher than iron ---
        PROTECTION_TABLE.put(Items.GOLDEN_HELMET,      new float[]{10.5f, 10.5f, 10.5f, 10.5f, 0f});
        PROTECTION_TABLE.put(Items.GOLDEN_CHESTPLATE,  new float[]{28f, 28f, 28f, 28f, 0f});
        PROTECTION_TABLE.put(Items.GOLDEN_LEGGINGS,    new float[]{21f, 21f, 21f, 21f, 0f});
        PROTECTION_TABLE.put(Items.GOLDEN_BOOTS,       new float[]{10.5f, 10.5f, 10.5f, 10.5f, 0f});

        // --- Chainmail: specializes in Combat (-> our Projectile reduction) ---
        PROTECTION_TABLE.put(Items.CHAINMAIL_HELMET,     new float[]{18f, 5.25f, 5.25f, 5.25f, 0f});
        PROTECTION_TABLE.put(Items.CHAINMAIL_CHESTPLATE, new float[]{48f, 14f, 14f, 14f, 0f});
        PROTECTION_TABLE.put(Items.CHAINMAIL_LEGGINGS,   new float[]{36f, 10.5f, 10.5f, 10.5f, 0f});
        PROTECTION_TABLE.put(Items.CHAINMAIL_BOOTS,      new float[]{18f, 5.25f, 5.25f, 5.25f, 0f});

        // --- Diamond: specializes in Fire ---
        PROTECTION_TABLE.put(Items.DIAMOND_HELMET,     new float[]{9.9f, 9.9f, 9.9f, 18.6f, 0f});
        PROTECTION_TABLE.put(Items.DIAMOND_CHESTPLATE, new float[]{26.4f, 26.4f, 26.4f, 49.6f, 0f});
        PROTECTION_TABLE.put(Items.DIAMOND_LEGGINGS,   new float[]{19.8f, 19.8f, 19.8f, 37.2f, 0f});
        PROTECTION_TABLE.put(Items.DIAMOND_BOOTS,      new float[]{9.9f, 9.9f, 9.9f, 18.6f, 0f});

        // --- Netherite (BTA's "Steel"): specializes in Blast ---
        PROTECTION_TABLE.put(Items.NETHERITE_HELMET,     new float[]{8.25f, 22.5f, 8.25f, 8.25f, 0f});
        PROTECTION_TABLE.put(Items.NETHERITE_CHESTPLATE, new float[]{22f, 60f, 22f, 22f, 0f});
        PROTECTION_TABLE.put(Items.NETHERITE_LEGGINGS,   new float[]{16.5f, 45f, 16.5f, 16.5f, 0f});
        PROTECTION_TABLE.put(Items.NETHERITE_BOOTS,      new float[]{8.25f, 22.5f, 8.25f, 8.25f, 0f});

    }

    // Quantum Boots: clone of Diamond boots in terms of stats.
    // { Combat, Blast, Fall, Fire, Lightning }
    // Note: the boots give total fall-damage immunity in ArmourOverhaulEvents,
    // so if you want the overlay to show that, set the Fall value to 99f.
    private static final float[] QUANTUM_BOOTS_VALUES = {9.9f, 9.9f, 99.f, 18.6f, 0f};

    private static float sumCategory(LivingEntity entity, int categoryIndex) {
        float total = 0f;
        total += valueFor(entity.getItemBySlot(EquipmentSlot.HEAD).getItem(), categoryIndex);
        total += valueFor(entity.getItemBySlot(EquipmentSlot.CHEST).getItem(), categoryIndex);
        total += valueFor(entity.getItemBySlot(EquipmentSlot.LEGS).getItem(), categoryIndex);
        total += valueFor(entity.getItemBySlot(EquipmentSlot.FEET).getItem(), categoryIndex);
        return Math.min(total, PROTECTION_CAP);
    }

    private static float valueFor(Item item, int categoryIndex) {
        float[] values = PROTECTION_TABLE.get(item);
        // Only runs during gameplay, after registration, so .get() is safe.
        if (values == null && item == quantumboots.QUANTUM_BOOTS.get()) {
            values = QUANTUM_BOOTS_VALUES;
        }
        return values != null ? values[categoryIndex] : 0f;
    }

    public static float getFallProtection(LivingEntity entity) {
        return sumCategory(entity, FALL);
    }

    public static float getBlastProtection(LivingEntity entity) {
        return sumCategory(entity, BLAST);
    }

    public static float getProjectileProtection(LivingEntity entity) {
        return sumCategory(entity, COMBAT);
    }

    public static float getFireProtection(LivingEntity entity) {
        return sumCategory(entity, FIRE);
    }

    /** Total lightning-damage protection (0-99) from all currently worn armour. */
    public static float getLightningProtection(LivingEntity entity) {
        return sumCategory(entity, LIGHTNING);
    }

    /** Used only for the farmland-trampling bonus */
    public static boolean isWearingLeatherBoots(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS);
    }

    /** Used for the "full copper set attracts lightning" mechanic. */
    public static boolean isFullCopper(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.COPPER_HELMET)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.COPPER_CHESTPLATE)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(Items.COPPER_LEGGINGS)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(Items.COPPER_BOOTS);
    }
}
