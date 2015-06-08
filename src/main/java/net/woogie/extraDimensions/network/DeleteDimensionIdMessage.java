package net.woogie.extraDimensions.network;

import io.netty.buffer.ByteBuf;
import net.woogie.extraDimensions.ExtraDimensionsUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DeleteDimensionIdMessage implements IMessage {

	int dimensionId;

	public DeleteDimensionIdMessage() {
	}

	public DeleteDimensionIdMessage(int i) {
		this.dimensionId = i;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.dimensionId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimensionId);
	}

}
