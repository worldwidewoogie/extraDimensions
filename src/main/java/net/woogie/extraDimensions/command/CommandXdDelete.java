package net.woogie.extraDimensions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public class CommandXdDelete extends CommandBase {

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
		return "xddelete";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		return "/" + getCommandName() + " <Dimension Name or ID>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return MinecraftServer.getServer().getOpPermissionLevel();
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {

		if (args.length == 1) {
			int dim = 0;

			try {
				dim = parseIntBounded(agent, args[0], -256, 256);
			} catch (Exception e) {
			}
			if (dim != 0) {
				if (ExtraDimensionsUtil.getDimensionIds().contains(dim)) {
					agent.addChatMessage(new ChatComponentText(
							ExtraDimensionsUtil.deleteDimension(dim)));
				} else {
					agent.addChatMessage(new ChatComponentText("Dimension "
							+ dim + "does not exist."));
				}
			} else {
				if (ExtraDimensionsUtil.dimensionNameExists(args[0])) {
					agent.addChatMessage(new ChatComponentText(
							ExtraDimensionsUtil.deleteDimension(args[0])));
				} else {
					agent.addChatMessage(new ChatComponentText("Dimension "
							+ args[0] + "does not exist."));
				}
			}
		} else {
			throw new WrongUsageException("Usage: " + getCommandUsage(agent),
					new Object[0]);
		}
	}
}