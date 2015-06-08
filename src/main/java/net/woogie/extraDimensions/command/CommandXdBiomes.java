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

		// if (args.length > 0) {
		// int page = parseIntWithMin(agent, args[0], 1);
		// page--;
		// BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		// String biomeNames = "";
		// boolean[] table = new boolean[256];
		//
		// int total = 0;
		// for (int i = 0; i < biomes.length; i++) {
		// if (biomes[i] != null) {
		// table[i] = true;
		// total++;
		// if ((total >= page * 10) && (total <= 10 + page * 10)) {
		// biomeNames = biomeNames + i + "=" + biomes[i].biomeName
		// + " ";
		// }
		// }
		// }
		// if (total % 10 > 0) {
		// total = total - total % 10 + 10;
		// }
		// agent.addChatMessage(new ChatComponentText("Biomes (" + (page + 1)
		// + "/" + total / 10 + "): " + biomeNames));
		// return;
		// }

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