package net.woogie.extraDimensions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.woogie.extraDimensions.ExtraDimensionsLogger;
import net.woogie.extraDimensions.ExtraDimensionsTeleporter;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommandXdTp extends CommandBase {
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender agent) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "xdtp";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		if (MinecraftServer.getServer().isSinglePlayer()
				|| !MinecraftServer
						.getServer()
						.getConfigurationManager()
						.func_152596_g(
								getPlayer(agent, agent.getCommandSenderName())
										.getGameProfile())) {
			return "/" + getCommandName() + " [destination player name]"
					+ " OR " + "/" + getCommandName()
					+ " [destination dimension (name or ID)] <x> <y> <z>";
		} else {
			return "/"
					+ getCommandName()
					+ " <target player name> [destination player name]"
					+ " OR "
					+ "/"
					+ getCommandName()
					+ " <target player name> [destination dimension (name or ID)] <x> <y> <z>";
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {

		if (args.length > 0) {
			int dim = 0;
			String dimArg;
			EntityPlayerMP player;

			if ((args.length == 2) || (args.length == 5)) {

				if (MinecraftServer.getServer().isSinglePlayer()
						|| !MinecraftServer
								.getServer()
								.getConfigurationManager()
								.func_152596_g(
										getPlayer(agent,
												agent.getCommandSenderName())
												.getGameProfile())) {

					throw new WrongUsageException("Usage: "
							+ getCommandUsage(agent), new Object[0]);

				}

				player = getPlayer(agent, args[0]);
				dimArg = args[1];

				try {
					Integer.parseInt(args[1]);
					dim = parseIntBounded(agent, args[1], -256, 256);
				} catch (NumberFormatException e) {
					dim = ExtraDimensionsUtil.getDimensionId(args[1]);
				} catch (NullPointerException e) {
					dim = 0;
				}

			} else {

				player = getPlayer(agent, agent.getCommandSenderName());
				dimArg = args[0];

				try {
					Integer.parseInt(args[0]);
					dim = parseIntBounded(agent, args[0], -256, 256);
				} catch (NumberFormatException e) {
					dim = ExtraDimensionsUtil.getDimensionId(args[0]);
				} catch (NullPointerException e) {
					dim = 0;
				}

			}

			if (dimArg.equals("0") || dimArg.equals("overworld")) {
				dim = 0;
			}

			if (!(ExtraDimensionsUtil.getDimensionIds().contains(
					Integer.valueOf(dim))
					|| dimArg.equals("0") || dimArg.equals("overworld"))) {
				throw new WrongUsageException("Dimension " + dimArg
						+ " does not exist!");
			}

			ChunkCoordinates spawn = new ChunkCoordinates();

			if (args.length < 3) {
				spawn = null;
			} else {
				int argOffset = -1;
				if (args.length == 5) {
					argOffset = 0;
				}
				spawn.posX = parseInt(agent, args[(2 + argOffset)]);
				spawn.posY = parseInt(agent, args[(3 + argOffset)]);
				spawn.posZ = parseInt(agent, args[(4 + argOffset)]);
			}

			teleportPlayer(agent, player, spawn, dim);
		} else {
			throw new WrongUsageException("Usage: " + getCommandUsage(agent),
					new Object[0]);
		}
	}

	private void teleportPlayer(ICommandSender par1ICommandSender,
			Entity player, ChunkCoordinates spawn, int nextDimension) {

		if (!(player instanceof EntityPlayer)) {
			par1ICommandSender.addChatMessage(new ChatComponentText(
					"Only players can teleport."));
			return;
		}

		if ((player.ridingEntity != null) || (player.riddenByEntity != null)) {
			par1ICommandSender.addChatMessage(new ChatComponentText(
					"Cannot Teleport while riding."));
			return;
		}

		if (player.dimension == nextDimension) {
			par1ICommandSender.addChatMessage(new ChatComponentText(
					"Already in dimension " + nextDimension
							+ ", teleport canceled."));
			return;
		}

		if (!DimensionManager.isDimensionRegistered(nextDimension)) {

			par1ICommandSender.addChatMessage(new ChatComponentText(
					"Dimension " + nextDimension
							+ " does not exist, teleport canceled."));
			return;
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {

			if ((player instanceof EntityPlayerMP)) {

				WorldServer nextWorld = MinecraftServer.getServer()
						.worldServerForDimension(nextDimension);

				if (nextWorld == null) {
					par1ICommandSender.addChatMessage(new ChatComponentText(
							"Problem with Dimension " + nextDimension
									+ ", teleport canceled."));
					return;
				}

				if (spawn == null) {
					spawn = nextWorld.getSpawnPoint();
				}

				while ((nextWorld.blockExists(spawn.posX, spawn.posY,
						spawn.posZ)) && (spawn.posY < 256)) {
					spawn.posY += 1;
				}
				if (!nextWorld.provider.isHellWorld) {
					spawn.posY = nextWorld.getTopSolidOrLiquidBlock(spawn.posX,
							spawn.posZ);
				}

				while ((nextWorld.isAirBlock(spawn.posX, spawn.posY - 1,
						spawn.posZ)) && (spawn.posY > 0)) {
					spawn.posY -= 1;
				}

				EntityPlayerMP playerMP = (EntityPlayerMP) player;

				ExtraDimensionsLogger.log(Level.INFO,
						"[ExtraDimensions] Teleporting "
								+ ((EntityPlayer) player).getDisplayName()
								+ " to "
								+ nextWorld.getWorldInfo().getWorldName()
								+ " (" + nextDimension + ")");

				playerMP.mcServer.getConfigurationManager()
						.transferPlayerToDimension(playerMP, nextDimension,
								new ExtraDimensionsTeleporter(nextWorld));
				playerMP.setPositionAndRotation(spawn.posX, spawn.posY,
						spawn.posZ, player.rotationYaw, 0.0F);
				playerMP.motionX = player.motionY = player.motionZ = 0.0D;

				((EntityLivingBase) player)
						.setPositionAndUpdate(spawn.posX + 0.5F,
								spawn.posY + 0.5F, spawn.posZ + 0.5F);

			}
		}
	}

}