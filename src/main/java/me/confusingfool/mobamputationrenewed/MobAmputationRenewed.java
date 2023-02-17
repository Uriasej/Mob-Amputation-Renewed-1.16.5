package me.confusingfool.mobamputationrenewed;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MobAmputationRenewed.MOD_ID)
public class MobAmputationRenewed
{

    public static final String MOD_ID = "mobamputationrenewed";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final UUID LEFT_ARM_MODIFIER = UUID.fromString("12c7f62f-3414-4e1d-b229-9c34aeb76658");
    private static final UUID RIGHT_ARM_MODIFIER = UUID.fromString("bdf7e42a-9e6a-45a1-85ed-136c6d5e9838");
    private static final UUID LEFT_LEG_MODIFIER = UUID.fromString("3f79e48e-3e2d-474f-93c8-81a29e46c916");
    private static final UUID RIGHT_LEG_MODIFIER = UUID.fromString("8872b2ea-19e0-44ea-a6e5-9ac44e706d55");

    public MobAmputationRenewed() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client

    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();

        if (entity.getType() == EntityType.ZOMBIE && !source.isBypassArmor())
        {
            if (Math.random() < 0.25)
            {
                String[] limbs = {"left_arm", "right_arm", "left_leg", "right_leg"};
                int limb = (int) Math.floor(Math.random() * 4);

                Attribute armorAttribute = Attributes.ARMOR;
                switch (limb)
                {
                    case 0:
                        entity.getAttribute(armorAttribute).addPermanentModifier(new AttributeModifier(LEFT_ARM_MODIFIER, "Left Arm Modifier", 0, AttributeModifier.Operation.ADDITION));
                        break;
                    case 1:
                        entity.getAttribute(armorAttribute).addPermanentModifier(new AttributeModifier(RIGHT_ARM_MODIFIER, "Right Arm Modifier", 0, AttributeModifier.Operation.ADDITION));
                        break;
                    case 2:
                        entity.getAttribute(armorAttribute).addPermanentModifier(new AttributeModifier(LEFT_LEG_MODIFIER, "Left Leg Modifier", 0, AttributeModifier.Operation.ADDITION));
                        break;
                    case 3:
                        entity.getAttribute(armorAttribute).addPermanentModifier(new AttributeModifier(RIGHT_LEG_MODIFIER, "Right Leg Modifier", 0, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
