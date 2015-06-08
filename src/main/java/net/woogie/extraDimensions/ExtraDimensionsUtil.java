package net.woogie.extraDimensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.woogie.extraDimensions.network.CreateDimensionIdMessage;
import net.woogie.extraDimensions.network.DeleteDimensionIdMessage;
import net.woogie.extraDimensions.worldinfo.ExtraDimensionsWorldInfoHandler;
import net.woogie.extraDimensions.worldinfo.ExtraDimensionsWorldInfoRegister;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class ExtraDimensionsUtil {
	private static int extraDimensionsWorldType;
	private static int extraDimensionsProviderId;
	private static String defaultAllowedBiomes;
	private static String dimensionIdsString;
	private static List<Integer> dimensionIds = new ArrayList();
	private static Map<Integer, String> dimensionNames = new HashMap();
	private static Map<Integer, String> dimensionWorldTypes = new HashMap();
	private static Map<Integer, String> dimensionAllowedBiomes = new HashMap();

	private static final int defaultProviderId = 99;
	private static final int defaultExtraDimensionsWorldType = 99;

	public static String createDimension(String dimensionName,
			String worldType, String allowedBiomes) {

		DimensionManager.loadDimensionDataMap(null);

		Integer dimensionId = DimensionManager.getNextFreeDimId();

		if (dimensionName == null || dimensionName.equals("")) {
			dimensionName = "Dimension" + dimensionId;
		}

		if (dimensionNameExists(dimensionName)) {
			return ("Dimension with name " + dimensionName + " already exists.");
		}

		if (worldType == null || worldType.equals("")) {
			worldType = "xdMultiBiome";
		}

		if (!ExtraDimensionsUtil.isValidWorldType(worldType)) {
			return ("World Type " + worldType + " is not valid.");
		}

		if (allowedBiomes == null || allowedBiomes.equals("")) {
			allowedBiomes = "*";
		}

		ExtraDimensionsWorldInfoRegister.registerDimensionWorldInfoHandler(
				dimensionId, new ExtraDimensionsWorldInfoHandler());
		DimensionManager.registerDimension(dimensionId,
				ExtraDimensionsUtil.getProviderId());

		dimensionIds.add(ExtraDimensions.config.get("dimension" + dimensionId,
				"dimensionId", "" + dimensionId).getInt());

		dimensionWorldTypes.put(
				dimensionId,
				ExtraDimensions.config.get("dimension" + dimensionId,
						"worldType", worldType).getString());

		dimensionNames.put(
				dimensionId,
				ExtraDimensions.config.get("dimension" + dimensionId, "name",
						dimensionName).getString());

		dimensionAllowedBiomes.put(
				dimensionId,
				ExtraDimensions.config.get("dimension" + dimensionId,
						"allowedBiomes", allowedBiomes).getString());

		ConfigCategory category = ExtraDimensions.config.getCategory("general");

		Property dimensionIdsProperty = new Property("dimensionIds",
				StringUtils.join(dimensionIds, ","), Property.Type.STRING);

		category.put("dimensionIds", dimensionIdsProperty);

		ExtraDimensions.config.save();

		if (!MinecraftServer.getServer().isSinglePlayer()) {

			ExtraDimensionsLogger.log(Level.INFO,
					"[ExtraDimensions] Notifying clients of new dimension " + dimensionId);

			ExtraDimensions.extraDimensionsSimpleChannel
					.sendToAll(new CreateDimensionIdMessage(dimensionId));
		}

		return "Dimension " + dimensionName + " created with ID " + dimensionId;

	}

	public static void createDimensionClientOnly(int dimensionId) {

		ExtraDimensionsLogger.log(Level.INFO,
				"[ExtraDimensions] Creating new dimension on client: " + dimensionId);

		ExtraDimensionsWorldInfoRegister.registerDimensionWorldInfoHandler(
				dimensionId, new ExtraDimensionsWorldInfoHandler());
		DimensionManager.registerDimension(dimensionId,
				ExtraDimensionsUtil.getProviderId());
		dimensionIds.add(dimensionId);
	}

	public static String deleteDimension(int dimensionId) {

		if (MinecraftServer.getServer().worldServerForDimension(dimensionId).playerEntities
				.size() == 0) {

			World world = DimensionManager.getWorld(dimensionId);

			String savePath = world.getSaveHandler().getWorldDirectory() + "/"
					+ world.provider.getSaveFolder();

			DimensionManager.setWorld(dimensionId, null);

			if (DimensionManager.isDimensionRegistered(dimensionId)) {
				DimensionManager.unregisterDimension(dimensionId);
			}
			ExtraDimensionsWorldInfoRegister
					.removeDimensionWorldInfoHandler(dimensionId);

			dimensionNames.remove(dimensionId);
			dimensionWorldTypes.remove(dimensionId);
			dimensionAllowedBiomes.remove(dimensionId);
			dimensionIds.remove(Integer.valueOf(dimensionId));

			ExtraDimensions.config.load();

			ExtraDimensions.config.removeCategory(ExtraDimensions.config
					.getCategory("dimension" + dimensionId));
			ConfigCategory category = ExtraDimensions.config
					.getCategory("general");

			Property dimensionIdsProperty = new Property("dimensionIds",
					StringUtils.join(dimensionIds, ","), Property.Type.STRING);

			category.put("dimensionIds", dimensionIdsProperty);

			// remove the existing dimension files, if they exist

			try {
				FileUtils.deleteDirectory(new File(savePath));
			} catch (IOException e) {
				return ("File I/O error deleting dimension " + dimensionId);
			}

			ExtraDimensions.config.save();

			if (!MinecraftServer.getServer().isSinglePlayer()) {

				ExtraDimensionsLogger
						.log(Level.INFO,
								"[ExtraDimensions] Notifying clients of deleted dimension "
										+ dimensionId);

				ExtraDimensions.extraDimensionsSimpleChannel
						.sendToAll(new DeleteDimensionIdMessage(dimensionId));
			}

			return ("Deleted dimension " + dimensionId);

		} else {
			return ("Dimension not empty, unable to delete.");
		}
	}

	public static String deleteDimension(String dimensionName) {
		int dimensionId = getDimensionId(dimensionName);
		if (dimensionId != 0) {
			return deleteDimension(dimensionId);
		} else {
			return ("Dimension " + dimensionName + " not found.");
		}
	}

	public static void deleteDimensionClientOnly(int dimensionId) {

		ExtraDimensionsLogger.log(Level.INFO, "[ExtraDimensions] Deleting dimension on client: "
				+ dimensionId);

		if (DimensionManager.isDimensionRegistered(dimensionId)) {
			DimensionManager.unregisterDimension(dimensionId);
		}
		ExtraDimensionsWorldInfoRegister
				.removeDimensionWorldInfoHandler(dimensionId);
		dimensionNames.remove(dimensionId);
		dimensionWorldTypes.remove(dimensionId);
		dimensionAllowedBiomes.remove(dimensionId);
		dimensionIds.remove(Integer.valueOf(dimensionId));
	}

	public static boolean dimensionNameExists(String dimensionName) {
		return dimensionNames.containsValue(dimensionName);
	}

	public static BiomeGenBase[] getBiomesList(int dimensionId) {

		ArrayList<BiomeGenBase> biomesList = new ArrayList<BiomeGenBase>();

		if (dimensionId == 0
				|| dimensionAllowedBiomes.get(dimensionId).equals("*")) {
			for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
				if (biome != null && biome.biomeName != null) {
					biomesList.add(biome);
				}
			}
		} else {
			String[] allowedBiomesString = dimensionAllowedBiomes.get(
					dimensionId).split(",");

			for (String allowedBiomeName : allowedBiomesString) {
				if (allowedBiomeName != null) {
					for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
						if (biome != null
								&& biome.biomeName != null
								&& Integer.parseInt(allowedBiomeName) == biome.biomeID) {
							biomesList.add(biome);
						}
					}
				}
			}
		}
		if (biomesList.size() == 0) {
			biomesList.add(BiomeGenBase.plains);
		}
		return (biomesList.toArray(new BiomeGenBase[biomesList.size()]));
	}

	public static String getDefaultAllowedBiomes() {
		return defaultAllowedBiomes;
	}

	public static int getDimensionId(String dimensionName) {
		for (int i : dimensionNames.keySet()) {
			if (dimensionNames.get(i).equals(dimensionName)) {
				return i;
			}
		}
		return 0;
	}

	public static List<Integer> getDimensionIds() {
		return dimensionIds;
	}

	public static String getDimensionName(int dimensionId) {
		return dimensionNames.get(dimensionId);
	}

	public static int getProviderId() {
		return extraDimensionsProviderId;
	}

	public static WorldType getWorldTypeForDimension(Integer dimensionId) {
		WorldType type = WorldType.DEFAULT;

		for (WorldType w : WorldType.worldTypes) {
			if (w != null
					&& w.getWorldTypeName() != null
					&& w.getWorldTypeName().equals(
							dimensionWorldTypes.get(dimensionId))) {
				return w;
			}
		}
		return null;
	}

	public static boolean isValidWorldType(String worldType) {
		return ExtraDimensionsUtil.validWorldTypes().contains(worldType);
	}

	public static void readConfig(Configuration config) {

		ExtraDimensions.config.load();

		extraDimensionsProviderId = config.get("general",
				"extraDimensionsProviderId", defaultProviderId).getInt();
		extraDimensionsWorldType = config.get("general",
				"ExtraDimensionsWorldType", defaultExtraDimensionsWorldType)
				.getInt();
		defaultAllowedBiomes = config.get("general", "defaultAllowedBiomes",
				"*").getString();

		DimensionManager.loadDimensionDataMap(DimensionManager
				.saveDimensionDataMap());

		dimensionIdsString = config.get("general", "dimensionIds", "")
				.getString();

		if (!dimensionIdsString.equals("")) {

			for (String dimensionIdString : dimensionIdsString.split(",")) {
				dimensionIds.add(Integer.valueOf(dimensionIdString));
			}
			for (Integer dimensionId : dimensionIds) {
				dimensionWorldTypes.put(
						dimensionId,
						config.get("dimension" + dimensionId, "worldType",
								"xdMultiBiome").getString());
				dimensionNames.put(
						dimensionId,
						config.get("dimension" + dimensionId, "name",
								"Dimension" + dimensionId).getString());
				config.get("dimension" + dimensionId, "dimensionId", ""
						+ dimensionId);
				dimensionAllowedBiomes.put(
						dimensionId,
						config.get("dimension" + dimensionId, "allowedBiomes",
								"*").getString());
			}

		}
		ExtraDimensions.config.save();
	}

	public static void renameDimension(int dimensionId, String newName) {
		ExtraDimensions.config.load();
		int entry = dimensionIds.indexOf(Integer.valueOf(dimensionId));
		ConfigCategory category = ExtraDimensions.config
				.getCategory("dimension" + entry);
		Property newNameProperty = new Property("name", newName,
				Property.Type.STRING);
		category.put("name", newNameProperty);
		dimensionNames.put(entry, newName);
		ExtraDimensions.config.save();
		MinecraftServer.getServer().worldServerForDimension(dimensionId)
				.getWorldInfo().setWorldName(newName);
	}

	public static void renameDimension(String dimensionName, String newName) {
		int dimensionId = getDimensionId(dimensionName);
		if (dimensionId != 0) {
			renameDimension(dimensionId, newName);
		}
	}

	public static void updateClientDimensionIds(
			List<Integer> currentDimensionIds) {

		List<Integer> dimensionIdsToAdd = new ArrayList();
		List<Integer> dimensionIdsToRemove = new ArrayList();

		Collections.copy(dimensionIdsToAdd, currentDimensionIds);
		Collections.copy(dimensionIdsToRemove, getDimensionIds());
		dimensionIdsToAdd.removeAll(getDimensionIds());
		dimensionIdsToRemove.removeAll(currentDimensionIds);

		for (Integer dimensionId : dimensionIdsToAdd) {
			ExtraDimensionsWorldInfoRegister.registerDimensionWorldInfoHandler(
					dimensionId, new ExtraDimensionsWorldInfoHandler());
			DimensionManager.registerDimension(dimensionId,
					ExtraDimensionsUtil.getProviderId());
			dimensionIds.add(dimensionId);
		}

		for (Integer dimensionId : dimensionIdsToRemove) {
			if (DimensionManager.isDimensionRegistered(dimensionId)) {
				DimensionManager.unregisterDimension(dimensionId);
			}
			ExtraDimensionsWorldInfoRegister
					.removeDimensionWorldInfoHandler(dimensionId);
			dimensionNames.remove(dimensionId);
			dimensionWorldTypes.remove(dimensionId);
			dimensionAllowedBiomes.remove(dimensionId);
			dimensionIds.remove(Integer.valueOf(dimensionId));
		}
	}

	private static List<String> validWorldTypes() {
		return Arrays.asList("xdMultiBiome", "BOP", "flat", "default",
				"largeBiomes", "amplified", "default_1_1");
	}
}