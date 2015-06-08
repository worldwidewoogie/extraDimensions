package net.woogie.extraDimensions.network;

import io.netty.util.concurrent.GenericFutureListener;
import net.woogie.extraDimensions.ExtraDimensions;
import net.woogie.extraDimensions.ExtraDimensionsUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ExtraDimensionsConnectionHandler {
	private static boolean connected = false;

	@SubscribeEvent
	public void clientConnectedToServer(
			FMLNetworkEvent.ClientConnectedToServerEvent event) {
		connected = true;
	}

	@SubscribeEvent
	public void clientDisconnectionFromServer(
			FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		if (connected) {
			connected = false;
			ExtraDimensions.instance.unregisterDimensions();
		}
	}

	@SubscribeEvent
	public void serverConnectionFromClient(
			FMLNetworkEvent.ServerConnectionFromClientEvent event) {
		event.manager.scheduleOutboundPacket(
				ExtraDimensionsDimensionIdPacket
						.createPacket(ExtraDimensionsUtil.getDimensionIds()),
				new GenericFutureListener[0]);
	}
}