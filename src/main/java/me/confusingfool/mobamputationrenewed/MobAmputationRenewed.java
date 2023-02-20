package me.confusingfool.mobamputationrenewed;

import me.confusingfool.mobamputationrenewed.BloodParticle;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(MobAmputationRenewed.MOD_ID)
public class MobAmputationRenewed
{

    public static final String MOD_ID = "mobamputationrenewed";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();


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
    public void onAttackEntity(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        Hand hand = event.getPlayer().swingingArm;
        Entity target = event.getTarget();

        if (hand == null) {
            return; // or log an error, or throw an exception, depending on your needs
        }

        if (target instanceof ZombieEntity && player.getItemInHand(hand).getItem() instanceof SwordItem) {
            Vector3d playerPos = player.getPosition(1.0f);
            Vector3d lookVec = player.getLookAngle();
            Vector3d rayTraceEnd = playerPos.add(lookVec.scale(5.0));

            RayTraceResult result = player.level.clip(new RayTraceContext(playerPos, rayTraceEnd, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));

            if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(playerPos.x, playerPos.y, playerPos.z);
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos).inflate(1.0);
                if (axisAlignedBB.contains(result.getLocation())) {
                    if (((BlockRayTraceResult) result).getDirection().getAxis() == Direction.Axis.Y) {
                        player.sendMessage(new StringTextComponent("Hit zombie on the head"), player.getUUID());
                        for (int i = 0; i < 10; i++) {
                            double motionX = (player.level.random.nextDouble() - 0.5) * 0.2;
                            double motionY = -0.1;
                            double motionZ = (player.level.random.nextDouble() - 0.5) * 0.2;
                        }
                    } else if (((BlockRayTraceResult) result).getDirection() == Direction.WEST) {
                        player.sendMessage(new StringTextComponent("Hit zombie on the left arm"), player.getUUID());
                    } else if (((BlockRayTraceResult) result).getDirection() == Direction.EAST) {
                        player.sendMessage(new StringTextComponent("Hit zombie on the right arm"), player.getUUID());
                    }
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
