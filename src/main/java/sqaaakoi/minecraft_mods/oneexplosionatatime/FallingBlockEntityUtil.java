package sqaaakoi.minecraft_mods.oneexplosionatatime;

import sqaaakoi.minecraft_mods.oneexplosionatatime.Main;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.Overwrite;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.Redirect;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
// import org.objectweb.asm.Opcodes;
import javax.annotation.Nullable;
import net.minecraft.entity.FallingBlockEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

// @Mixin(FallingBlockEntity.class)
public class FallingBlockEntityUtil {
  // Yes I copied this entire method and changed the variable names.
  public static FallingBlockEntity spawnFromBlockWithVelocity(World w, BlockPos bp, BlockState bs, Vec3d v) {
      boolean bw = bs.contains(Properties.WATERLOGGED);
      FallingBlockEntity e = new FallingBlockEntity(w, (double)bp.getX() + 0.5, (double)bp.getY(), (double)bp.getZ() + 0.5, bw ? bs.with(Properties.WATERLOGGED, false) : bs);
      e.setVelocity(v);
      w.setBlockState(bp, bw ? bs.getFluidState().getBlockState() : Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
      w.spawnEntity(e);
      return e;
  }
}
