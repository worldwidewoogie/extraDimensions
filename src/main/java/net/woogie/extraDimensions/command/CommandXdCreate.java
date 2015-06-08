package net.woogie.extraDimensions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public class CommandXdCreate extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender agent) {

		if (MinecraftServer.getServer().isSinglePlayer()
				|| agent.canCommandSenderUseCommand(2, "")) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "xdcreate";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		return "/" + getCommandName()
				+ " <Dimension Name or ID> <Biomes> <World Type>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return MinecraftServer.getServer().getOpPermissionLevel();
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {

		if (args.length > 0 && args.length < 4) {

			String dimensionName = args[0];
			String worldType = "xdMultiBiome";
			String allowedBiomes = "*";

			if (args.length > 1) {
				allowedBiomes = args[1];
			}

			if (args.length > 2) {
				worldType = args[2];
			}

			agent.addChatMessage(new ChatComponentText(ExtraDimensionsUtil
					.createDimension(dimensionName, worldType, allowedBiomes)));
		} else {
			throw new WrongUsageException("Usage: " + getCommandUsage(agent),
					new Object[0]);
		}
	}
}