package org.betterx.betterend.integration.jade;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        BetterEnd.LOGGER.info("Registering Jade Integration for BetterEnd.");
        registration.registerBlockComponent(EndPlantProvider.INSTANCE, EndPlantWithAgeBlock.class);
    }
}
