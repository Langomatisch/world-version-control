package de.langomatisch.wvc.command;

import de.langomatisch.wvc.WVCPlugin;
import de.langomatisch.wvc.git.GitWorldManager;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WVCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            help(commandSender);
            return false;
        }
        if (strings[0].equalsIgnoreCase("create")) {
            if (commandSender instanceof Player player) {
                create(player);
            } else {
                commandSender.sendMessage("You must be a player to execute this command");
            }
            return true;
        }
        return false;
    }

    private void help(CommandSender commandSender) {
        commandSender.sendMessage("/wvc create");
    }

    private void create(Player player) {
        World world = player.getWorld();
        player.sendMessage("Creating repository for world " + world.getName());
        GitRepository repository = GitWorldManager.getInstance().createRepository(world);
        if (repository == null) {
            player.sendMessage("Repository already exists");
        } else {
            player.sendMessage("Repository created");
        }
    }

}
