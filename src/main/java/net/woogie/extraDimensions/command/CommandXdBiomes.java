package net.woogie.extraDimensions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.biome.BiomeGenBase;

public class CommandXdBiomes extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender agent) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "xdbiomes";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		return "/" + getCommandName();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender agent, String[] args) {
		if (args.length > 1) {
			throw new WrongUsageException("Usage: " + getCommandUsage(agent),
					new Object[0]);
		}

		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		String biomeNames = "";
		for (int i = 0; i < biomes.length; i++) {
			if (biomes[i] != null) {
				biomeNames = biomeNames + i + "=" + biomes[i].biomeName + " ";
			}
		}
		agent.addChatMessage(new ChatComponentText("Biomes: " + biomeNames));
	}

}