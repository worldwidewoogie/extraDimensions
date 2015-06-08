package net.woogie.extraDimensions.world.genLayer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.woogie.extraDimensions.ExtraDimensionsLogger;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

import org.apache.logging.log4j.Level;

public class GenLayerBiomesCustom extends GenLayer {
	protected BiomeGenBase[] allowedBiomes = new BiomeGenBase[0];

	public GenLayerBiomesCustom(long seed, GenLayer genlayer) {
		super(seed);
		this.parent = genlayer;

		if (this.allowedBiomes != null) {
			ExtraDimensionsLogger.log(Level.INFO,
					"GenLayerBiomesCustom biomeCount="
							+ this.allowedBiomes.length);
		} else {
			ExtraDimensionsLogger.log(Level.INFO,
					"GenLayerBiomesCustom failed to initialize biomes");
			this.allowedBiomes = new BiomeGenBase[] { BiomeGenBase.plains };
		}
	}

	public GenLayerBiomesCustom(long seed, WorldType worldType, int dim) {
		super(seed);

		this.allowedBiomes = ExtraDimensionsUtil.getBiomesList(dim);
		if (this.allowedBiomes != null) {
			ExtraDimensionsLogger.log(Level.INFO,
					"GenLayerBiomesCustom biomeCount="
							+ this.allowedBiomes.length);
		} else
			this.allowedBiomes = new BiomeGenBase[] { BiomeGenBase.plains };
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth) {
		int[] dest = IntCache.getIntCache(width * depth);

		for (int dz = 0; dz < depth; dz++) {
			for (int dx = 0; dx < width; dx++) {
				initChunkSeed(dx + x, dz + z);
				dest[(dx + dz * width)] = this.allowedBiomes[nextInt(this.allowedBiomes.length)].biomeID;
			}
		}
		return dest;
	}
}