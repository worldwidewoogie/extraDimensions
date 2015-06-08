package net.woogie.extraDimensions.world.genLayer;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class GenLayerCustom extends GenLayer {
	public static GenLayer[] makeTheWorld(long seed, WorldType worldtype,
			int dim) {
		GenLayer biomes = new GenLayerBiomesCustom(1L, worldtype, dim);

		biomes = new GenLayerZoom(1000L, biomes);
		biomes = new GenLayerZoom(1001L, biomes);
		biomes = new GenLayerZoom(1002L, biomes);
		biomes = new GenLayerZoom(1003L, biomes);
		biomes = new GenLayerZoom(1004L, biomes);
		biomes = new GenLayerZoom(1005L, biomes);

		GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10L, biomes);

		biomes.initWorldGenSeed(seed);

		genlayervoronoizoom.initWorldGenSeed(seed);

		return new GenLayer[] { biomes, genlayervoronoizoom };
	}

	public GenLayerCustom(long seed) {
		super(seed);
	}
}