package me.confusingfool.mobamputationrenewed;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BloodParticle extends SpriteTexturedParticle implements IParticleData {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mobamputationrenewed", "textures/particle/blood_particle");

    // Add the scale field
    private float scale;
    private IParticleRenderType ParticleRenderType;

    public BloodParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super((ClientWorld) world, x, y, z, velocityX, velocityY, velocityZ);

        // Set the sprite for the particle
        this.setSprite(Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(TEXTURE));

        // Set the particle properties
        this.lifetime = 10;
        this.gravity = 0.05f;
        this.setColor(1.0f, 0.0f, 0.0f);
    }

    @Override
    public net.minecraft.particles.ParticleType<?> getType() {
        return null;
    }

    @Override
    public void writeToNetwork(PacketBuffer p_197553_1_) {

    }

    @Override
    public String writeToString() {
        return null;
    }

    public static class BloodParticleType implements IParticleData {

        @Override
        public net.minecraft.particles.ParticleType<?> getType() {
            return null;
        }

        @Override
        public void writeToNetwork(PacketBuffer p_197553_1_) {

        }

        @Override
        public String writeToString() {
            return null;
        }
    }

    public static class Factory implements net.minecraft.client.particle.ParticleManager.IParticleMetaFactory<BloodParticle> {
        public Particle createParticle(BloodParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
            return new BloodParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }

        @Override
        public IParticleFactory<BloodParticle> create(IAnimatedSprite p_create_1_) {
            return null;
        }
    }


    @Override
    public void tick() {
        super.tick();

        // Make the particle shrink over time
        float lifeCoeff = (float) this.age / (float) this.lifetime;
        this.scale = 0.5f * (1.0f - lifeCoeff);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
