package net.woogie.extraDimensions.network;

import org.apache.logging.log4j.Level;

import net.woogie.extraDimensions.ExtraDimensionsLogger;
import net.woogie.extraDimensions.ExtraDimensionsUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DeleteDimensionIdMessageHandler implements
		IMessageHandler<DeleteDimensionIdMessage, IMessage> {

	@Override
	public IMessage onMessage(DeleteDimensionIdMessage message,
			MessageContext ctx) {

		ExtraDimensionsLogger.log(Level.INFO,
				"[ExtraDimensions] Got notification of deleted dimension: "
						+ message.dimensionId);

		ExtraDimensionsUtil.deleteDimensionClientOnly(message.dimensionId);

		return null;
	}
}
