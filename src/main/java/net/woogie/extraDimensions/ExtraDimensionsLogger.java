package net.woogie.extraDimensions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;

public class ExtraDimensionsLogger {
	static Logger logger = FMLCommonHandler.instance().getFMLLogger();

	public static void log(Level level, String msg) {
		logger.log(level, msg);
	}

}