package net.woogie.extraDimensions.worldinfo;

import java.util.HashMap;

public class ExtraDimensionsWorldInfoRegister {
	public static HashMap<Integer, IExtraDimensionsWorldInfoHandler> dimensionWorldInfoMap = new HashMap();

	public static boolean hasHandler(int dimension) {
		return dimensionWorldInfoMap.containsKey(Integer.valueOf(dimension));
	}

	public static void registerDimensionWorldInfoHandler(int dimension,
			IExtraDimensionsWorldInfoHandler worldInfo) {
		if (dimension >= 0) {
			dimensionWorldInfoMap.put(Integer.valueOf(dimension), worldInfo);
		}
	}

	public static void removeDimensionWorldInfoHandler(int dimension) {
		if ((dimension >= 0) && (hasHandler(dimension))) {
			dimensionWorldInfoMap.remove(Integer.valueOf(dimension));
		}
	}
}