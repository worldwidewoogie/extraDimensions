package net.woogie.extraDimensions.network;

import org.apache.logging.log4j.Level;

import net.woogie.extraDimensions.ExtraDimensionsLogger;
import net.woogie.extraDimensions.ExtraDimensionsUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CreateDimensionIdMessageHandler implements
		IMessageHandler<CreateDimensionIdMessage, IMessage> {

	@Override
	public IMessage onMessage(CreateDimensionIdMessage message,
			MessageContext ctx) {

		ExtraDimensionsLogger.log(Level.INFO,
				"[ExtraDimensions] Got notification of new dimension: "
						+ message.dimensionId);

		ExtraDimensionsUtil.createDimensionClientOnly(message.dimensionId);

		return null;
	}
}
