package org.betterx.betterend.item;

import org.betterx.bclib.items.ModelProviderItem;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.util.LangUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class GuideBookItem extends ModelProviderItem {
    public GuideBookItem() {
        super(EndItems.makeEndItemSettings().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        //TODO: 1.19.3 Re-Enable once patchouli is available
//        if (!world.isClientSide && user instanceof ServerPlayer) {
//            PatchouliAPI.get().openBookGUI((ServerPlayer) user, BOOK_ID);
//            return InteractionResultHolder.success(user.getItemInHand(hand));
//        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag tooltipFlag
    ) {
        consumer.accept(LangUtil.getText("book.betterend", "subtitle")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
    }
}
