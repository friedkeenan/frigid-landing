package io.github.friedkeenan.frigid_landing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

@Mixin(Block.class)
public class UpgradeIceOnLanding {
    private static final float NEEDED_FALL_DISTANCE = 10.0f;
    private static final float BASE_UPGRADE_CHANCE  = 1.0f / 64.0f;

    private static void UpgradeIce(BlockState new_block, BlockPos pos, Level level) {
        level.setBlockAndUpdate(pos, new_block);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(new_block));
    }

    @Inject(at = @At("TAIL"), method = "fallOn")
    private void upgradeIce(Level level, BlockState block, BlockPos pos, Entity entity, float fall_distance, CallbackInfo info) {
        if (level.isClientSide) {
            return;
        }

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        final var living_entity = (LivingEntity) entity;

        final var frost_walker_level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, living_entity);
        if (frost_walker_level == 0) {
            return;
        }

        final BlockState new_block;
        if (block.is(Blocks.ICE)) {
            new_block = Blocks.PACKED_ICE.defaultBlockState();
        } else if (block.is(Blocks.PACKED_ICE)) {
            new_block = Blocks.BLUE_ICE.defaultBlockState();
        } else {
            return;
        }

        if (Mth.ceil(fall_distance) < NEEDED_FALL_DISTANCE) {
            return;
        }

        if (level.random.nextFloat() >= frost_walker_level * BASE_UPGRADE_CHANCE) {
            return;
        }

        UpgradeIce(new_block, pos, level);
    }
}
