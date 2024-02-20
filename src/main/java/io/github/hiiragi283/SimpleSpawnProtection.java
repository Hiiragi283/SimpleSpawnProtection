package io.github.hiiragi283;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(SimpleSpawnProtection.MOD_ID)
public class SimpleSpawnProtection {

    public static final String MOD_ID = "ssp";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ITag<Block> TAG = BlockTags.createOptional(new ResourceLocation(MOD_ID, "disable_spawn"));

    public SimpleSpawnProtection() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMobSpawning(LivingSpawnEvent.CheckSpawn event) {
        BlockPos pos = new BlockPos(event.getX(), event.getY() - 1, event.getZ());
        BlockState state = event.getWorld().getBlockState(pos);
        if (state.is(TAG)) {
            event.setResult(Event.Result.DENY);
            LOGGER.debug("Spawn canceled at pos; {}, block, {}!", pos, state);
        }
    }

}