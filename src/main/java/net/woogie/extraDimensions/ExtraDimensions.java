package net.woogie.extraDimensions;

import java.util.EnumMap;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.woogie.extraDimensions.command.CommandXdCreate;
import net.woogie.extraDimensions.command.CommandXdDelete;
import net.woogie.extraDimensions.command.CommandXdBiomes;
import net.woogie.extraDimensions.command.CommandXdList;
import net.woogie.extraDimensions.command.CommandXdRename;
import net.woogie.extraDimensions.command.CommandXdTp;
import net.woogie.extraDimensions.network.CreateDimensionIdMessage;
import net.woogie.extraDimensions.network.CreateDimensionIdMessageHandler;
import net.woogie.extraDimensions.network.DeleteDimensionIdMessage;
import net.woogie.extraDimensions.network.DeleteDimensionIdMessageHandler;
import net.woogie.extraDimensions.network.ExtraDimensionsConnectionHandler;
import net.woogie.extraDimensions.network.ExtraDimensionsDimensionIdPacket;
import net.woogie.extraDimensions.network.ExtraDimensionsPacketHandler;
import net.woogie.extraDimensions.world.ExtraDimensionsWorldProvider;
import net.woogie.extraDimensions.world.WorldTypeMultiBiome;
import net.woogie.extraDimensions.worldinfo.ExtraDimensionsWorldInfoHandler;
import net.woogie.extraDimensions.worldinfo.ExtraDimensionsWorldInfoRegister;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "extraDimensions", version = "1.7.10.0", name = "ExtraDimensions", useMetadata = true, dependencies = "required-after:Forge@[10.12.0.1024,);after:TooManyBiomes;after:BiomesOPlenty")
public class ExtraDimensions {

	@SidedProxy(clientSide = "net.woogie.extraDimensions.ClientProxy", serverSide = "net.woogie.extraDimensions.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance("ExtraDimensions")
	public static ExtraDimensions instance;
	public static Configuration config;
	public static EnumMap<Side, FMLEmbeddedChannel> channels;
	public static WorldType[] worldTypes;
	public static SimpleNetworkWrapper extraDimensionsSimpleChannel;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		ExtraDimensionsPacketHandler
				.registerPacketHandler(new ExtraDimensionsDimensionIdPacket());
		FMLCommonHandler.instance().bus()
				.register(new ExtraDimensionsConnectionHandler());
		ExtraDimensionsPacketHandler.bus = NetworkRegistry.INSTANCE
				.newEventDrivenChannel("extraDimensions");
		ExtraDimensionsPacketHandler.bus
				.register(new ExtraDimensionsPacketHandler());

		ExtraDimensionsLogger.log(Level.INFO,
				"[ExtraDimensions] Setting up extraDimensionsSimpleChannel");
		extraDimensionsSimpleChannel = NetworkRegistry.INSTANCE
				.newSimpleChannel("extraDimensions0");
		extraDimensionsSimpleChannel.registerMessage(
				CreateDimensionIdMessageHandler.class,
				CreateDimensionIdMessage.class, 0, Side.CLIENT);
		extraDimensionsSimpleChannel.registerMessage(
				DeleteDimensionIdMessageHandler.class,
				DeleteDimensionIdMessage.class, 1, Side.CLIENT);

		ExtraDimensions.config = new Configuration(
				event.getSuggestedConfigurationFile());

		ExtraDimensions.config.load();

		ExtraDimensionsUtil.readConfig(ExtraDimensions.config);

		DimensionManager.registerProviderType(
				ExtraDimensionsUtil.getProviderId(),
				ExtraDimensionsWorldProvider.class, false);

		new WorldTypeMultiBiome();

		registerDimensions();

		ExtraDimensions.config.save();
	}

	public void registerDimensions() {

		if (ExtraDimensionsUtil.getDimensionIds() == null)
			return;

		for (int dimId : ExtraDimensionsUtil.getDimensionIds()) {

			ExtraDimensionsLogger.log(Level.INFO, "Registering Dimension "
					+ dimId);

			ExtraDimensionsWorldInfoRegister.registerDimensionWorldInfoHandler(
					dimId, new ExtraDimensionsWorldInfoHandler());
			DimensionManager.registerDimension(dimId,
					ExtraDimensionsUtil.getProviderId());
		}
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdDelete());
		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdCreate());
		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdList());
		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdBiomes());
		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdRename());
		((ServerCommandManager) event.getServer().getCommandManager())
				.registerCommand(new CommandXdTp());
	}

	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		unregisterDimensions();
		DimensionManager.unregisterProviderType(ExtraDimensionsUtil
				.getProviderId());
	}

	public void unregisterDimensions() {
		if (ExtraDimensionsUtil.getDimensionIds() == null)
			return;
		for (int i : ExtraDimensionsUtil.getDimensionIds()) {
			if (DimensionManager.isDimensionRegistered(i)) {
				DimensionManager.unregisterDimension(i);
			}
		}
	}
}