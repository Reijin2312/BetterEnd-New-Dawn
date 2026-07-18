package org.betterx.betterend.item;

import org.betterx.bclib.interfaces.ItemModelProvider;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluids;

public class EndBucketItem extends MobBucketItem implements ItemModelProvider {
    public EndBucketItem(EntityType<? extends Mob> type) {
        super(type, Fluids.WATER, SoundEvents.BUCKET_EMPTY, EndItems.makeEndItemSettings().stacksTo(1));
    }
}
