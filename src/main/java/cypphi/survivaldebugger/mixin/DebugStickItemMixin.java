package cypphi.survivaldebugger.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(DebugStickItem.class)
public abstract class DebugStickItemMixin {
    @Shadow
    private static void sendMessage(PlayerEntity player, Text message) {}

    @Shadow
    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) { return null; }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Block block = state.getBlock();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        Collection<Property<?>> collection = stateManager.getProperties();
        String string = Registries.BLOCK.getId(block).toString();
        if (collection.isEmpty()) {
            sendMessage(player, Text.of(String.format("Block: %s, No properties", string)));
            cir.setReturnValue(false);
        } else {
            NbtCompound nbtCompound = stack.getOrCreateSubNbt("DebugProperty");
            String string2 = nbtCompound.getString(string);
            Property<?> property = stateManager.getProperty(string2);
            if (update) {
                if (property == null) {
                    property = (Property)collection.iterator().next();
                }

                BlockState blockState = cycle(state, property, player.shouldCancelInteraction());
                world.setBlockState(pos, blockState, 18);
                sendMessage(player, Text.of(String.format("Block: %s, Property: %s, Value: %s", string, property.getName(), blockState.get(property))));
            } else {
//                I don't know how to do this - Github Copilot
//                property = (Property)cycle((Iterable)collection, (Object)property, player.shouldCancelInteraction());
                String string3 = property.getName();
                nbtCompound.putString(string, string3);
                sendMessage(player, Text.of(String.format("Block: %s, Property: %s", string, string3)));
            }

            cir.setReturnValue(true);
        }
    }
}
