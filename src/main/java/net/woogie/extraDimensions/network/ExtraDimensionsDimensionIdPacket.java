package net.woogie.extraDimensions.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.woogie.extraDimensions.ExtraDimensionsUtil;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class ExtraDimensionsDimensionIdPacket extends
		ExtraDimensionsPacket {
	public static final int packetType = 1;

	public static FMLProxyPacket createPacket(Collection<Integer> set) {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetType);
		data.writeInt(set.size());
		for (Integer dimID : set) {
			data.writeInt(dimID.intValue());
		}
		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(Integer dim) {
		ArrayList set = new ArrayList();
		set.add(dim);
		return createPacket(set);
	}

	@Override
	public byte getPacketType() {
		return packetType;
	}

	@Override
	public void handle(ByteBuf networkData, EntityPlayer paramEntityPlayer) {
		int length = networkData.readInt();

		for (int i = 0; i < length; i++) {
			int id = networkData.readInt();
			if (!ExtraDimensionsUtil.getDimensionIds().contains(
					Integer.valueOf(id))) {
				ExtraDimensionsUtil.createDimensionClientOnly(id);
			}
		}
	}
}