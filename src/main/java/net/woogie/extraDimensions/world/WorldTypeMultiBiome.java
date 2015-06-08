package net.woogie.extraDimensions.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.woogie.extraDimensions.world.genLayer.GenLayerBiomesCustom;

public class WorldTypeMultiBiome extends WorldType {
	public WorldTypeMultiBiome() {
		super("xdMultiBiome");
	}

	@Override
	public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer) {
		GenLayer ret = new GenLayerBiomesCustom(200L, parentLayer);
		return ret;
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderMultiBiome(world, world.getSeed(), world
				.getWorldInfo().isMapFeaturesEnabled());
	}

	@Override
	public WorldChunkManager getChunkManager(World world) {
		return new WorldChunkManagerMultiBiome(world);
	}
}