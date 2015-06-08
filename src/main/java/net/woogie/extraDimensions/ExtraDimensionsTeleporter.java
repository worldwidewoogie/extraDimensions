package net.woogie.extraDimensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ExtraDimensionsTeleporter extends Teleporter {
	private final WorldServer worldServer;
	private final Random random;
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	private final List<Long> destinationCoordinateKeys = new ArrayList();

	public ExtraDimensionsTeleporter(WorldServer par1WorldServer) {
		super(par1WorldServer);
		this.worldServer = par1WorldServer;
		this.random = new Random(par1WorldServer.getSeed());
	}

	@Override
	public boolean makePortal(Entity par1Entity) {
		byte b0 = 16;
		double d0 = -1.0D;
		int i = MathHelper.floor_double(par1Entity.posX);
		int j = MathHelper.floor_double(par1Entity.posY);
		int k = MathHelper.floor_double(par1Entity.posZ);
		int l = i;
		int i1 = j;
		int j1 = k;
		int k1 = 0;
		int l1 = this.random.nextInt(4);

		for (int i2 = i - b0; i2 <= i + b0; i2++) {
			double d1 = i2 + 0.5D - par1Entity.posX;

			for (int k2 = k - b0; k2 <= k + b0; k2++) {
				double d2 = k2 + 0.5D - par1Entity.posZ;

				label1274:

				for (int i3 = this.worldServer.getActualHeight() - 1; i3 >= 0; i3--) {
					if (this.worldServer.isAirBlock(i2, i3, k2)) {
						while ((i3 > 0)
								&& (this.worldServer.isAirBlock(i2, i3 - 1, k2))) {
							i3--;
						}

						for (int j3 = l1; j3 < l1 + 4; j3++) {
							int k3 = j3 % 2;
							int l3 = 1 - k3;

							if (j3 % 4 >= 2) {
								k3 = -k3;
								l3 = -l3;
							}

							for (int i4 = 0; i4 < 3; i4++) {
								for (int j4 = 0; j4 < 4; j4++) {
									for (int k4 = -1; k4 < 4; k4++) {
										int l4 = i2 + (j4 - 1) * k3 + i4 * l3;
										int i5 = i3 + k4;
										int j5 = k2 + (j4 - 1) * l3 - i4 * k3;

										if (((k4 < 0) && (!this.worldServer
												.getBlock(l4, i5, j5)
												.getMaterial().isSolid()))
												|| ((k4 >= 0) && (!this.worldServer
														.isAirBlock(l4, i5, j5)))) {
											continue label1274;
										}
									}
								}
							}

							double d7 = i3 + 0.5D - par1Entity.posY;
							double d8 = d1 * d1 + d7 * d7 + d2 * d2;

							if ((d0 < 0.0D) || (d8 < d0)) {
								d0 = d8;
								l = i2;
								i1 = i3;
								j1 = k2;
								k1 = j3 % 4;
							}
						}
					}
				}
			}
		}

		if (d0 < 0.0D) {
			for (int i2 = i - b0; i2 <= i + b0; i2++) {
				double d1 = i2 + 0.5D - par1Entity.posX;

				for (int k2 = k - b0; k2 <= k + b0; k2++) {
					double d2 = k2 + 0.5D - par1Entity.posZ;

					label222:

					for (int i3 = this.worldServer.getActualHeight() - 1; i3 >= 0; i3--) {
						if (this.worldServer.isAirBlock(i2, i3, k2)) {
							while ((i3 > 0)
									&& (this.worldServer.isAirBlock(i2, i3 - 1,
											k2))) {
								i3--;
							}

							for (int j3 = l1; j3 < l1 + 2; j3++) {
								int k3 = j3 % 2;
								int l3 = 1 - k3;

								for (int i4 = 0; i4 < 4; i4++) {
									for (int j4 = -1; j4 < 4; j4++) {
										int k4 = i2 + (i4 - 1) * k3;
										int l4 = i3 + j4;
										int i5 = k2 + (i4 - 1) * l3;

										if (((j4 < 0) && (!this.worldServer
												.getBlock(k4, l4, i5)
												.getMaterial().isSolid()))
												|| ((j4 >= 0) && (!this.worldServer
														.isAirBlock(k4, l4, i5)))) {
											continue label222;
										}
									}
								}

								double d3 = i3 + 0.5D - par1Entity.posY;
								double d4 = d1 * d1 + d3 * d3 + d2 * d2;

								if ((d0 < 0.0D) || (d4 < d0)) {
									d0 = d4;
									l = i2;
									i1 = i3;
									j1 = k2;
									k1 = j3 % 2;
								}
							}
						}
					}
				}
			}
		}

		int k5 = l;
		int j2 = i1;
		int k2 = j1;
		int l5 = k1 % 2;
		int l2 = 1 - l5;

		if (k1 % 4 >= 2) {
			l5 = -l5;
			l2 = -l2;
		}

		if (d0 < 0.0D) {
			if (i1 < 70) {
				i1 = 70;
			}

			if (i1 > this.worldServer.getActualHeight() - 10) {
				i1 = this.worldServer.getActualHeight() - 10;
			}

			j2 = i1;

			for (int i3 = -1; i3 <= 1; i3++) {
				for (int j3 = 1; j3 < 3; j3++) {
					for (int k3 = -1; k3 < 3; k3++) {
						int l3 = k5 + (j3 - 1) * l5 + i3 * l2;
						int i4 = j2 + k3;
						int j4 = k2 + (j3 - 1) * l2 - i3 * l5;
						boolean flag = k3 < 0;
						this.worldServer.setBlock(l3, i4, j4,
								flag ? Blocks.stone : Blocks.air);
					}
				}
			}
		}

		for (int i3 = 0; i3 < 4; i3++) {
			for (int j3 = 0; j3 < 4; j3++) {
				for (int k3 = -1; k3 < 4; k3++) {
					int l3 = k5 + (j3 - 1) * l5;
					int i4 = j2 + k3;
					int j4 = k2 + (j3 - 1) * l2;
					boolean flag = (j3 == 0) || (j3 == 3) || (k3 == -1)
							|| (k3 == 3);
					this.worldServer.setBlock(l3, i4, j4, flag ? Blocks.stone
							: Blocks.portal);
				}

			}

			for (int j3 = 0; j3 < 4; j3++) {
				for (int k3 = -1; k3 < 4; k3++) {
					int l3 = k5 + (j3 - 1) * l5;
					int i4 = j2 + k3;
					int j4 = k2 + (j3 - 1) * l2;

					this.worldServer.setBlock(l3, i4, j4,
							this.worldServer.getBlock(l3, i4, j4));
				}
			}
		}

		return true;
	}

	@Override
	public void placeInPortal(Entity par1Entity, double par2, double par4,
			double par6, float par8) {
		int x = MathHelper.floor_double(par1Entity.posX);
		int y = MathHelper.floor_double(par1Entity.posY);
		int z = MathHelper.floor_double(par1Entity.posZ);

		par1Entity.setLocationAndAngles(x, y, z, par1Entity.rotationYaw, 0.0F);
		par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
	}

	@Override
	public void removeStalePortalLocations(long p_85189_1_) {
		if (p_85189_1_ % 100L == 0L) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = this.destinationCoordinateKeys.iterator();
			long j = p_85189_1_ - 600L;

			while (iterator.hasNext()) {
				Long olong = (Long) iterator.next();
				Teleporter.PortalPosition portalposition = (Teleporter.PortalPosition) this.destinationCoordinateCache
						.getValueByKey(olong.longValue());

				if (portalposition == null || portalposition.lastUpdateTime < j) {
					iterator.remove();
					this.destinationCoordinateCache.remove(olong.longValue());
				}
			}
		}
	}
}