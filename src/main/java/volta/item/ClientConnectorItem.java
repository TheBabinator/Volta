package volta.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import volta.electricity.Terminal;
import volta.electricity.wiring.ClientConnector;
import volta.electricity.wiring.WireStyle;

public abstract class ClientConnectorItem extends Item {
    public ClientConnectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                ClientConnector.disconnect();
                return InteractionResultHolder.success(player.getItemInHand(interactionHand));
            }
            return ClientConnector.interactFromItemUse(level, player, interactionHand, getWireStyle(), this::connect, this, shouldReset());
        }
        return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
    }

    public abstract WireStyle getWireStyle();
    public abstract void connect(Terminal first, Terminal last);
    public abstract boolean shouldReset();
}
