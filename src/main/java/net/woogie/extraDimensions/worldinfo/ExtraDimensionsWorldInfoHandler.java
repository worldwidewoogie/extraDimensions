package net.woogie.extraDimensions.worldinfo;

import net.minecraft.world.storage.WorldInfo;
import net.woogie.extraDimensions.world.WorldProviderSettings;

public class ExtraDimensionsWorldInfoHandler implements
		IExtraDimensionsWorldInfoHandler {
	@Override
	public WorldInfo getModdedWorldInfo(int dimension, WorldInfo worldInfo) {
		new WorldProviderSettings(dimension, Long.valueOf(worldInfo.getSeed()),
				worldInfo);

		return WorldProviderSettings.getWorldInfo(dimension);
	}
}