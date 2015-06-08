package net.woogie.extraDimensions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	public boolean serverDetected = false;

	@Override
	public boolean isEnabled() {
		return this.serverDetected;
	}

	@Override
	public void setServerDetected() {
		this.serverDetected = true;
	}
}