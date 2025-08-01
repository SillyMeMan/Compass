package net.vinh.compass.systems.rendering.particle.screen.emitter;

import net.vinh.compass.systems.rendering.particle.screen.base.ScreenParticle;
import net.minecraft.item.ItemStack;

public interface ItemParticleEmitter {
    void particleTick(ItemStack stack, float x, float y, ScreenParticle.RenderOrder renderOrder);
}
