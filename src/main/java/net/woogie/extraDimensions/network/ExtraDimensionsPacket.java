package net.woogie.extraDimensions.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public abstract class ExtraDimensionsPacket {
	public abstract void handle(ByteBuf paramByteBuf,
			EntityPlayer paramEntityPlayer);

	public abstract byte getPacketType();

	protected static FMLProxyPacket buildPacket(ByteBuf payload) {
		return new FMLProxyPacket(payload, ExtraDimensionsPacketHandler.channel);
	}
}