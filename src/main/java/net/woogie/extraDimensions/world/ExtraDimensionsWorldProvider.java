package net.woogie.extraDimensions.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public class ExtraDimensionsWorldProvider extends WorldProvider {
	private static WorldProviderSettings worldProviderSettings = WorldProviderSettings.instance;

	@Override
	public boolean canSnowAt(int x, int y, int z, boolean checkLight) {
		return this.worldObj.canSnowAtBody(x, y, z, checkLight);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return this.terrainType.getChunkGenerator(this.worldObj,
				this.field_82913_c);
	}

	@Override
	public String getDepartMessage() {
		if ((this instanceof ExtraDimensionsWorldProvider)) {
			return "Leaving dimension " + getDimensionName();
		}
		return null;
	}

	@Override
	public String getDimensionName() {
		int dim = this.dimensionId;
		if (dim == 0)
			return "Overworld";
		return ExtraDimensionsUtil.getDimensionName(dim);
	}

	@Override
	public String getSaveFolder() {
		try {
			if (this.dimensionId == 0)
				return "";
			return "ExtraDimensions/" + this.dimensionId;
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public long getSeed() {
		Long seed = Long.valueOf(super.getSeed());
		if (this.dimensionId > 0) {
			if (!WorldProviderSettings.dimensionSettings.containsKey(Integer
					.valueOf(this.dimensionId))) {
				WorldProviderSettings.dimensionSettings.put(
						Integer.valueOf(this.dimensionId),
						new WorldProviderSettings(this.dimensionId,
								Long.valueOf(this.worldObj.getWorldInfo()
										.getSeed()), this.worldObj
										.getWorldInfo()));
				this.worldObj.getWorldInfo();
			}

			return worldProviderSettings.getDimensionSettings(this.dimensionId)
					.getSeed().longValue();
		}
		return seed.longValue();
	}

	@Override
	public String getWelcomeMessage() {
		if ((this instanceof ExtraDimensionsWorldProvider)) {
			return "Entering dimension " + getDimensionName();
		}
		return null;
	}

	@Override
	public void registerWorldChunkManager() {
		WorldType worldType = ExtraDimensionsUtil
				.getWorldTypeForDimension(this.dimensionId);
		if (worldType != null) {
			this.terrainType = worldType;
		}

		this.worldChunkMgr = this.terrainType.getChunkManager(this.worldObj);
	}

	@Override
	public void setDimension(int dim) {
		this.dimensionId = dim;
		super.setDimension(dim);
	}
}