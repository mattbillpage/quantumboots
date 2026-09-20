package com.example.quantumboots;

import java.util.Map; // NEW - for the armour material's defence map

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier; // NEW - for the equipment asset id
import net.minecraft.resources.ResourceKey; // NEW - for the equipment asset key
import net.minecraft.sounds.SoundEvents; // NEW - equip sound for the material
import net.minecraft.tags.ItemTags; // NEW - repair tag for the material
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial; // NEW - custom material (replaces ArmorMaterials)
import net.minecraft.world.item.equipment.ArmorType; // for Quantum Boots' equipment slot
import net.minecraft.world.item.equipment.EquipmentAsset; // NEW
import net.minecraft.world.item.equipment.EquipmentAssets; // NEW
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(quantumboots.MODID)
public class quantumboots {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "quantumboots";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "quantumboots" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "quantumboots" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "quantumboots" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final ResourceKey<EquipmentAsset> QUANTUM_ASSET = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(MODID, "quantum"));

    // NEW - Our own armour material. These are Netherite's numbers, matching
    // what the boots used before. Change them freely.
    public static final ArmorMaterial QUANTUM_MATERIAL = new ArmorMaterial(
            37,                                                   // durability multiplier
            Map.of(ArmorType.BOOTS, 3,
                   ArmorType.LEGGINGS, 6,
                   ArmorType.CHESTPLATE, 8,
                   ArmorType.HELMET, 3),                          // armour points per slot
            15,                                                   // enchantability
            SoundEvents.ARMOR_EQUIP_NETHERITE,                    // equip sound
            3.0F,                                                 // toughness
            0.1F,                                                 // knockback resistance
            ItemTags.REPAIRS_NETHERITE_ARMOR,                     // repair tag
            QUANTUM_ASSET);                                       // worn texture asset

    // CHANGED - now uses QUANTUM_MATERIAL instead of ArmorMaterials.NETHERITE
    public static final DeferredItem<Item> QUANTUM_BOOTS = ITEMS.registerItem(
            "quantum_boots",
            properties -> new Item(properties.humanoidArmor(QUANTUM_MATERIAL, ArmorType.BOOTS))
    );

    // Creates a creative tab with the id "quantumboots:example_tab" for the example item, that is placed after the combat tab
    /*public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.quantumboots")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());// Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());*/

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public quantumboots(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");

        /*if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        */
        //LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

       // Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(QUANTUM_BOOTS);
        }
    }

    /* You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }*/
}
