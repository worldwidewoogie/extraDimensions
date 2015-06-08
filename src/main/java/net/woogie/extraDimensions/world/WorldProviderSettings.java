package net.woogie.extraDimensions.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public final class WorldProviderSettings {
	public static final WorldProviderSettings instance = new WorldProviderSettings();
	public static Hashtable<Integer, WorldProviderSettings> dimensionSettings = new Hashtable();
	public static Hashtable<Integer, Boolean> initialized = new Hashtable();

	public static Hashtable<Integer, Class<? extends BiomeGenBase>> biomes = new Hashtable();

	public static WorldInfo getWorldInfo(int dimension) {
		return dimensionSettings.get(Integer.valueOf(dimension)).worldInfo;
	}

	private Long seed = Long.valueOf(0L);
	private String dimensionName = "Unnamed Dimension";

	private WorldInfo worldInfo = null;

	private WorldProviderSettings() {
	}

	public WorldProviderSettings(int dimension, Long dim0Seed,
			WorldInfo overworld) {
		if (!dimensionSettings.containsKey(Integer.valueOf(dimension))) {
			dimensionSettings.put(Integer.valueOf(dimension), this);
		}

		initialized.put(Integer.valueOf(dimension), Boolean.valueOf(true));

		if (MinecraftServer.getServer() == null) {
			return;
		}

		Long seed = Long.valueOf(0L);

		if (!parseLevelDatFile(dimension).booleanValue()) {
			this.seed = Long.valueOf(overworld.getSeed() + dimension);
			createNewWorldInfo(overworld, dimension);
		}
	}

	public void addBiome(BiomeGenBase biome) {
	}

	private void createNewWorldInfo(WorldInfo overWorld, int dimension) {
		NBTTagCompound data = new NBTTagCompound();

		data.setLong("RandomSeed", this.seed.longValue());

		data.setString("generatorName", ExtraDimensionsUtil
				.getWorldTypeForDimension(dimension).getWorldTypeName());

		data.setInteger("generatorVersion", overWorld.getTerrainType()
				.getGeneratorVersion());
		data.setString("generatorOptions", overWorld.getGeneratorOptions());
		data.setInteger("GameType", overWorld.getGameType().getID());
		data.setBoolean("MapFeatures", overWorld.isMapFeaturesEnabled());
		data.setInteger("SpawnX", 0);
		data.setInteger("SpawnY", 0);
		data.setInteger("SpawnZ", 0);
		data.setLong("Time", overWorld.getWorldTotalTime());
		data.setLong("DayTime", overWorld.getWorldTime());
		data.setLong("SizeOnDisk", 0L);
		// data.setLong("LastPlayed", overWorld.getLastTimePlayed());
		data.setString("LevelName",
				ExtraDimensionsUtil.getDimensionName(dimension));
		data.setInteger("version", overWorld.getSaveVersion());
		data.setInteger("rainTime", 0);
		data.setBoolean("raining", false);
		data.setInteger("thunderTime", 0);
		data.setBoolean("thundering", false);
		data.setBoolean("hardcore", overWorld.isHardcoreModeEnabled());
		data.setBoolean("allowCommands", overWorld.areCommandsAllowed());
		data.setBoolean("initialized", false);

		this.worldInfo = new WorldInfo(data);

		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		nbttagcompound1.setTag("Data", data);

		File file = new File(DimensionManager.getCurrentSaveRootDirectory(),
				"/ExtraDimensions/" + dimension + "/level.dat");

		if (!file.exists())
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			FileOutputStream fileoutputstream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(nbttagcompound1,
					fileoutputstream);
			fileoutputstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDimensionName() {
		return this.dimensionName;
	}

	public WorldProviderSettings getDimensionSettings(int dimension) {
		if (!dimensionSettings.containsKey(Integer.valueOf(dimension))) {
			return null;
		}
		return dimensionSettings.get(Integer.valueOf(dimension));
	}

	public Long getSeed() {
		return this.seed;
	}

	public Boolean isSettingsInitialized(int dimension) {
		return Boolean
				.valueOf((initialized.containsKey(Integer.valueOf(dimension)))
						&& (initialized.get(Integer.valueOf(dimension))
								.booleanValue()));
	}

	private Boolean parseLevelDatFile(int dimension) {
		File file = new File(DimensionManager.getCurrentSaveRootDirectory(),
				"level.dat");

		if (dimension != 0) {
			file = new File(DimensionManager.getCurrentSaveRootDirectory(),
					"/ExtraDimensions/" + dimension + "/level.dat");
		}

		this.worldInfo = null;

		if (file.exists()) {
			try {
				NBTTagCompound var2 = CompressedStreamTools
						.readCompressed(new FileInputStream(file));
				NBTTagCompound var3 = var2.getCompoundTag("Data");
				this.worldInfo = new WorldInfo(var3);
				this.seed = Long.valueOf(this.worldInfo.getSeed());
				return Boolean.valueOf(true);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}
		return Boolean.valueOf(false);
	}

	public void resetInitialization() {
		initialized = new Hashtable();
	}

	public void setAllBiomes(
			Map<? extends Integer, ? extends Class<? extends BiomeGenBase>> biome) {
		biomes.putAll(biome);
	}

	public void setDimensionName(String name) {
		this.dimensionName = name;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}
}