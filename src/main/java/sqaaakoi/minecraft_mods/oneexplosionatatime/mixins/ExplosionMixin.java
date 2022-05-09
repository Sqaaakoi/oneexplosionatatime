package sqaaakoi.minecraft_mods.oneexplosionatatime.mixins;

import sqaaakoi.minecraft_mods.oneexplosionatatime.Main;
import sqaaakoi.minecraft_mods.oneexplosionatatime.FallingBlockEntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.objectweb.asm.Opcodes;
import javax.annotation.Nullable;
import net.minecraft.world.explosion.Explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.Explosion.DestructionType;

@Mixin(Explosion.class)
public class ExplosionMixin {
  @Shadow
  private boolean createFire;
  @Shadow
  private DestructionType destructionType;
  @Shadow
  private Random random;
  @Shadow
  private World world;
  @Shadow
  private double x;
  @Shadow
  private double y;
  @Shadow
  private double z;
  @Shadow
  @Nullable
  private Entity entity;
  @Shadow
  private float power;
  @Shadow
  private DamageSource damageSource;
  @Shadow
  private ExplosionBehavior behavior;
  @Shadow
  private List<BlockPos> affectedBlocks = Lists.newArrayList();
  @Shadow
  private Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

  @Inject(at = @At("HEAD"), method = "affectWorld(Z)V", cancellable = true)
  public void affectWorld(boolean particles, CallbackInfo ci) {
      boolean bl2;
      if (this.world.isClient) {
          this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
      }
      boolean bl = bl2 = this.destructionType != DestructionType.NONE;
      if (particles) {
          if (this.power < 2.0f || !bl2) {
              this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
          } else {
              this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
          }
      }
      if (bl2) {
          Collections.shuffle(this.affectedBlocks, this.world.random);
          SimpleInventory lv = new SimpleInventory(ItemStack.EMPTY);
          for (BlockPos affectedBlock : this.affectedBlocks) {
              Object blockEntity;
              BlockState lv3 = this.world.getBlockState(affectedBlock);
              Block lv4 = lv3.getBlock();
              // if (this.random.nextFloat() > 0.3f) {
              //     BlockState lv5 = Blocks.AIR.getDefaultState();
              //     if (lv3.isAir()) continue;
              //     BlockPos lv6 = affectedBlock.toImmutable();
              //     this.world.getProfiler().push("explosion_blocks");
              //     if (lv4.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
              //         blockEntity = lv3.hasBlockEntity() ? this.world.getBlockEntity(affectedBlock) : null;
              //         LootContext.Builder lv8 = new LootContext.Builder((ServerWorld)this.world).random(this.world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(affectedBlock)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
              //         if (this.destructionType == DestructionType.DESTROY) {
              //             lv8.parameter(LootContextParameters.EXPLOSION_RADIUS, Float.valueOf(this.power));
              //         }
              //         lv5 = Util.getRandomOrEmpty(lv3.getDroppedStacks(lv8).stream().mapMulti((arg22, consumer) -> {
              //             lv.setStack(0, (ItemStack)arg22);
              //             this.world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, lv, this.world).forEach(arg -> {
              //                 Item lv = arg.getOutput().getItem();
              //                 if (lv instanceof BlockItem) {
              //                     BlockItem affectedBlock = (BlockItem)lv;
              //                     consumer.accept(affectedBlock.getBlock().getDefaultState());
              //                 }
              //             });
              //         }).toList(), this.random).orElse(lv5);
              //     }
              //     this.world.setBlockState(affectedBlock, lv5, Block.NOTIFY_ALL);
              //     lv4.onDestroyedByExplosion(this.world, affectedBlock, this);
              //     this.world.getProfiler().pop();
              //     continue;
              // }
              this.world.setBlockState(affectedBlock, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
              float affectedBlockX = (float)((double)affectedBlock.getX() - this.x);
              float affectedBlockZ = (float)((double)affectedBlock.getZ() - this.z);
              Vec2f vector = new Vec2f(affectedBlockX, affectedBlockZ);
              float lv8 = vector.length();
              if (lv8 > 1.0f) {
                  blockEntity = vector.multiply(1.0f / lv8);
              }
              FallingBlockEntityUtil.spawnFromBlockWithVelocity(this.world, affectedBlock, lv3, new Vec3d(vector.x, 0.6, vector.y));
          }
      }
      if (this.createFire) {
          for (BlockPos lv9 : this.affectedBlocks) {
              if (this.random.nextInt(3) != 0 || !this.world.getBlockState(lv9).isAir() || !this.world.getBlockState(lv9.down()).isOpaqueFullCube(this.world, lv9.down())) continue;
              this.world.setBlockState(lv9, AbstractFireBlock.getState(this.world, lv9));
          }
      }
      ci.cancel();
  }
}
