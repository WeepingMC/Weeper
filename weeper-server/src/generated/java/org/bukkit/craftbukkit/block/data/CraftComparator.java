package org.bukkit.craftbukkit.block.data;

import com.google.common.base.Preconditions;
import io.papermc.paper.generated.GeneratedFrom;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.craftbukkit.block.CraftBlockData;

@GeneratedFrom("1.21.5")
public class CraftComparator extends CraftBlockData implements Comparator {
    private static final EnumProperty<Direction> FACING = ComparatorBlock.FACING;

    private static final EnumProperty<ComparatorMode> MODE = ComparatorBlock.MODE;

    private static final BooleanProperty POWERED = ComparatorBlock.POWERED;

    public CraftComparator(BlockState state) {
        super(state);
    }

    @Override
    public BlockFace getFacing() {
        return this.get(FACING, BlockFace.class);
    }

    @Override
    public void setFacing(final BlockFace blockFace) {
        Preconditions.checkArgument(blockFace != null, "blockFace cannot be null!");
        Preconditions.checkArgument(blockFace.isCartesian() && blockFace.getModY() == 0, "Invalid face, only cartesian horizontal face are allowed for this property!");
        this.set(FACING, blockFace);
    }

    @Override
    public Set<BlockFace> getFaces() {
        return this.getValues(FACING, BlockFace.class);
    }

    @Override
    public Comparator.Mode getMode() {
        return this.get(MODE, Comparator.Mode.class);
    }

    @Override
    public void setMode(final Comparator.Mode mode) {
        Preconditions.checkArgument(mode != null, "mode cannot be null!");
        this.set(MODE, mode);
    }

    @Override
    public boolean isPowered() {
        return this.get(POWERED);
    }

    @Override
    public void setPowered(final boolean powered) {
        this.set(POWERED, powered);
    }
}
