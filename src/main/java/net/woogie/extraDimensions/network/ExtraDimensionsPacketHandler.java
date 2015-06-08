package net.woogie.extraDimensions.network;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExtraDimensionsPacketHandler {
	public static final String channel = "extraDimensions";
	public static FMLEventChannel bus;
	private static HashMap<Byte, ExtraDimensionsPacket> packethandlers = new HashMap();

	public static void registerPacketHandler(
			ExtraDimensionsPacket mPacket) {
		if (packethandlers.get(Byte.valueOf(mPacket.getPacketType())) != null) {
			return;
		}
		packethandlers.put(Byte.valueOf(mPacket.getPacketType()), mPacket);
	}

	public void onPacketData(NetworkManager manager, FMLProxyPacket pkt,
			EntityPlayer player) {
		try {
			if ((pkt == null) || (pkt.payload() == null)) {
				throw new RuntimeException("Empty packet sent");
			}
			ByteBuf data = pkt.payload();
			byte type = data.readByte();
			try {
				ExtraDimensionsPacket handler = packethandlers.get(Byte
						.valueOf(type));
				if (handler == null) {
					throw new RuntimeException("Unrecognized packet sent");
				}
				handler.handle(data, player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPacketDataClient(FMLNetworkEvent.ClientCustomPacketEvent event) {
		FMLProxyPacket pkt = event.packet;
		onPacketData(event.manager, pkt, Minecraft.getMinecraft().thePlayer);
	}

	@SubscribeEvent
	public void onPacketDataServer(FMLNetworkEvent.ServerCustomPacketEvent event) {
		FMLProxyPacket pkt = event.packet;
		onPacketData(event.manager, pkt,
				((NetHandlerPlayServer) event.handler).playerEntity);
	}
}