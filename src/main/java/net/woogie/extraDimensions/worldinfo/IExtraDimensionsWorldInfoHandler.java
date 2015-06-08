package net.woogie.extraDimensions.worldinfo;

import net.minecraft.world.storage.WorldInfo;

public abstract interface IExtraDimensionsWorldInfoHandler {
	public abstract WorldInfo getModdedWorldInfo(int paramInt,
			WorldInfo paramWorldInfo);
}