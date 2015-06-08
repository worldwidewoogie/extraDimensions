package net.woogie.extraDimensions.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.woogie.extraDimensions.ExtraDimensionsUtil;

public class CommandXdList extends CommandBase {

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender agent) {
		return true;
	}

	@Override
	public String getCommandName() {
		return "xdlist";
	}

	@Override
	public String getCommandUsage(ICommandSender agent) {
		return "/" + getCommandName() + "<Dimension name or ID or '.'>";
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

		if (args.length > 0) {

			if (args[0].equals(".")) {
				World world = agent.getEntityWorld();

				int dim = world.provider.dimensionId;
				WorldType worldType = null;
				if (ExtraDimensionsUtil.getDimensionIds().contains(
						Integer.valueOf(dim))) {
					worldType = ExtraDimensionsUtil
							.getWorldTypeForDimension(dim);
				}
				if (worldType == null) {
					worldType = world.provider.terrainType;
				}

				ChunkCoordinates location = agent.getPlayerCoordinates();
				BiomeGenBase biome = world.getBiomeGenForCoords(location.posX,
						location.posZ);
				String biomeName = biome.biomeName;

				BiomeGenBase[] biomes = ExtraDimensionsUtil.getBiomesList(dim);
				List<String> biomeNames = new ArrayList<String>();

				if (biomes != null) {
					String currentBiomes = "";
					for (int i = 0; i < biomes.length; i++) {
						if (currentBiomes.equals("")) {
							currentBiomes = biomes[i].biomeName;
						} else {
							currentBiomes += ", " + biomes[i].biomeName;
						}
						if (currentBiomes.length() >= 35) {
							biomeNames.add(currentBiomes);
							currentBiomes = "";
						}
					}
					if (!currentBiomes.equals("")) {
						biomeNames.add(currentBiomes);
					}
				}
				agent.addChatMessage(new ChatComponentText("Dimension "
						+ ExtraDimensionsUtil.getDimensionName(dim)));
				agent.addChatMessage(new ChatComponentText("  Dimension ID: "
						+ dim));
				agent.addChatMessage(new ChatComponentText("  World Type: "
						+ worldType.getWorldTypeName()));
				agent.addChatMessage(new ChatComponentText("  Biomes: "
						+ biomeNames.get(0)));
				for (int i = 1; i < biomeNames.size(); i++) {
					agent.addChatMessage(new ChatComponentText("          "
							+ biomeNames.get(i)));
				}
				agent.addChatMessage(new ChatComponentText("  CurrentBiome: "
						+ biomeName));
			} else {

				int dim = 0;

				try {
					Integer.parseInt(args[0]);
					dim = parseIntBounded(agent, args[0], -256, 256);
				} catch (NumberFormatException e) {
					dim = ExtraDimensionsUtil.getDimensionId(args[0]);
				} catch (NullPointerException e) {
					dim = 0;
				}

				if (!ExtraDimensionsUtil.getDimensionIds().contains(
						Integer.valueOf(dim))) {
					agent.addChatMessage(new ChatComponentText(
							"Dimension does not exist."));
					return;
				}

				WorldType worldType = ExtraDimensionsUtil
						.getWorldTypeForDimension(dim);
				BiomeGenBase[] biomes = ExtraDimensionsUtil.getBiomesList(dim);
				List<String> biomeNames = new ArrayList<String>();

				if (biomes != null) {
					String currentBiomes = "";
					for (int i = 0; i < biomes.length; i++) {
						if (currentBiomes.equals("")) {
							currentBiomes = biomes[i].biomeName;
						} else {
							currentBiomes += ", " + biomes[i].biomeName;
						}
						if (currentBiomes.length() >= 35) {
							biomeNames.add(currentBiomes);
							currentBiomes = "";
						}
					}
					if (!currentBiomes.equals("")) {
						biomeNames.add(currentBiomes);
					}
				}

				agent.addChatMessage(new ChatComponentText("Dimension "
						+ ExtraDimensionsUtil.getDimensionName(dim)));
				agent.addChatMessage(new ChatComponentText("  Dimension ID: "
						+ dim));
				agent.addChatMessage(new ChatComponentText("  Biomes: "
						+ biomeNames.get(0)));
				for (int i = 1; i < biomeNames.size(); i++) {
					agent.addChatMessage(new ChatComponentText("          "
							+ biomeNames.get(i)));
				}
			}

		} else {

			World world = agent.getEntityWorld();
			for (int i : ExtraDimensionsUtil.getDimensionIds()) {
				String currentFlag = "";
				if (world.provider.dimensionId == i) {
					currentFlag = " *";
				}
				agent.addChatMessage(new ChatComponentText(i + " "
						+ ExtraDimensionsUtil.getDimensionName(i) + currentFlag));
			}
		}

	}

}