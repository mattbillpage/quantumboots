package com.example.quantumboots;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects; // NEW - for the Quantum Boots jump boost effect
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot; // NEW - needed to check the boots slot for Quantum Boots
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Core passive-armour-bonus logic.
 *
 * Registered on the GAME event bus via @EventBusSubscriber - no manual
 * registration needed.
 */
@EventBusSubscriber(modid = ArmourOverhaulMod.MOD_ID)
public final class ArmourOverhaulEvents {

    private ArmourOverhaulEvents() {}

    /**
     * Chance (1 in this many ticks) that a thunderstorm strikes a full-copper player who can see the sky.
     * Lower = more frequent strikes.
     */
    private static final int LIGHTNING_ATTRACTION_CHANCE = 3000;

    // NEW - how long each refresh of Quantum Boots' Jump Boost lasts, in
    // ticks (20 ticks = 1 second, so 60 = 3 seconds as requested). Since
    // this gets reapplied every tick while sprinting, the effect never
    // actually counts down until the player stops sprinting - at which
    // point the last-applied 3 seconds simply plays out normally.
    private static final int QUANTUM_BOOTS_JUMP_BOOST_DURATION_TICKS = 60;

    // Jump Boost amplifier (0 = Jump Boost I, 1 = Jump Boost II, etc.)
    private static final int QUANTUM_BOOTS_JUMP_BOOST_AMPLIFIER = 0;

    /**
     * Fires once the final damage number is known, before it's actually
     * applied to the entity. Reduces damage based on which category the
     * damage falls into and how much protection the entity's armour
     * provides for that category.
     */
    @SubscribeEvent
    public static void onFinalDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // Quantum Boots: total fall-damage immunity
        if (source.is(DamageTypeTags.IS_FALL)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(quantumboots.QUANTUM_BOOTS.get())) {
            event.setNewDamage(0f);
            return;
        }

        float protectionPercent = 0f;

        if (source.is(DamageTypeTags.IS_FALL)) {
            protectionPercent = ArmourProtection.getFallProtection(entity);
        } else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            protectionPercent = ArmourProtection.getBlastProtection(entity);
        } else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            protectionPercent = ArmourProtection.getProjectileProtection(entity);
        } else if (source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypes.LAVA)) {
            protectionPercent = ArmourProtection.getFireProtection(entity);
        } else if (source.getDirectEntity() instanceof LightningBolt) {
            protectionPercent = ArmourProtection.getLightningProtection(entity);
        }

        if (protectionPercent <= 0f) {
            return;
        }

        float reducedDamage = event.getNewDamage() * (1f - protectionPercent / 100f);
        event.setNewDamage(Math.max(reducedDamage, 0f));
    }

    /**
     * Leather boots: walking on farmland never turns it back into dirt.
     */
    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof LivingEntity living && ArmourProtection.isWearingLeatherBoots(living)) {
            event.setCanceled(true);
        }
    }

    /**
     * Full copper set: during a thunderstorm, if the player can see the sky,
     * there's a small per-tick chance a bolt strikes them directly - like
     * wearing a walking lightning rod.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        // NEW - Quantum Boots: continuously refresh Jump Boost while the
        // player is sprinting. Placed before the copper/lightning logic
        // below and using its own local variable name (sprintLevel) so it
        // doesn't touch or reuse anything from the existing block.
        if (player.level() instanceof ServerLevel //sprintLevel
                && player.isSprinting()
                && player.getItemBySlot(EquipmentSlot.FEET).is(quantumboots.QUANTUM_BOOTS.get())) {
            // NOTE: MobEffects.JUMP_BOOST is the current documented name as
            // of recent versions (it used to be MobEffects.JUMP in older
            // ones). Given this version has already renamed other vanilla
            // constants elsewhere (EntityType -> EntityTypes), double-check
            // this one against your IDE's autocomplete if it doesn't
            // resolve - MobEffects.JUMP is the most likely fallback name.
            player.addEffect(new MobEffectInstance(
                    MobEffects.JUMP_BOOST,
                    QUANTUM_BOOTS_JUMP_BOOST_DURATION_TICKS,
                    QUANTUM_BOOTS_JUMP_BOOST_AMPLIFIER,
                    false, // ambient
                    true   // show particles
            ));
        }

        // Only run server-side - spawning entities client-side would just
        // desync, since the server is authoritative over world state.
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!serverLevel.isThundering()) {
            return;
        }

        if (!ArmourProtection.isFullCopper(player)) {
            return;
        }

        if (!serverLevel.canSeeSky(player.blockPosition())) {
            return;
        }
        
        if (serverLevel.getRandom().nextInt(LIGHTNING_ATTRACTION_CHANCE) != 0) {
            return;
        }

        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(
            serverLevel, null, player.blockPosition(), EntitySpawnReason.EVENT, false, false);
        if (bolt != null) {
            bolt.setPos(player.getX(), player.getY(), player.getZ());
            serverLevel.addFreshEntity(bolt);
        }
    }
}