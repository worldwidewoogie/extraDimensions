package net.woogie.extraDimensions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public class CommandXdRename extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender agent) {

		if (MinecraftServer.getServer().isSinglePlayer()
				|| MinecraftServer
						.getServer()
						.getConfigurationManager()
						.func_152596_g(
								getPlayer(agent, agent.getCommandSenderName())
										.getGameProfile())) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "xdrename";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		return "/" + getCommandName()
				+ " <Dimension Name or ID> <New Dimension Name>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return MinecraftServer.getServer().getOpPermissionLevel();
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {

		if (args.length == 2) {
			int dim = 0;
			try {
				dim = parseIntBounded(agent, args[0], -256, 256);
			} catch (Exception e) {
			}
			if (dim != 0) {
				if (!ExtraDimensionsUtil.getDimensionIds().contains(
						Integer.valueOf(dim))) {
					throw new WrongUsageException("No dimension with ID '"
							+ args[0] + "'");
				}
				ExtraDimensionsUtil.renameDimension(dim, args[1]);
			} else {
				if (!ExtraDimensionsUtil.dimensionNameExists(args[0])) {
					throw new WrongUsageException("No dimension named '"
							+ args[0] + "'");
				}
				ExtraDimensionsUtil.renameDimension(args[0], args[1]);
			}
			agent.addChatMessage(new ChatComponentText("Renamed dimension '"
					+ args[0] + "' to '" + args[1] + "'"));
		} else {
			throw new WrongUsageException("Usage: " + getCommandUsage(agent),
					new Object[0]);
		}
	}
}