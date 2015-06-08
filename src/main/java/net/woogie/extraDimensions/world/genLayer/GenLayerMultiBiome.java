package net.woogie.extraDimensions.world.genLayer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerMultiBiome extends GenLayerBiome {
	private BiomeGenBase[] biomes;

	public GenLayerMultiBiome(long seed, GenLayer parentLayer,
			WorldType worldType) {
		super(seed, parentLayer, worldType);

		this.biomes = new BiomeGenBase[] { BiomeGenBase.extremeHills,
				BiomeGenBase.mushroomIsland, BiomeGenBase.jungle };
	}

	@Override
	public int[] getInts(int par1, int par2, int par3, int par4) {
		int[] aint = this.parent.getInts(par1, par2, par3, par4);
		int[] aint1 = IntCache.getIntCache(par3 * par4);

		for (int i1 = 0; i1 < par4; i1++) {
			for (int j1 = 0; j1 < par3; j1++) {
				initChunkSeed(j1 + par1, i1 + par2);
				int k1 = aint[(j1 + i1 * par3)];
				int l1 = (k1 & 0xF00) >> 8;
				k1 &= -3841;

				if (isBiomeOceanic(k1)) {
					aint1[(j1 + i1 * par3)] = k1;
				} else {
					aint1[(j1 + i1 * par3)] = this.biomes[selectRandom(this.biomes.length)].biomeID;
				}
			}
		}

		return aint1;
	}
}