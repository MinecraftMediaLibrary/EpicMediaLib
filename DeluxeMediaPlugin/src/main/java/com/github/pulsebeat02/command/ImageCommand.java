package com.github.pulsebeat02.command;

import com.github.pulsebeat02.DeluxeMediaPlugin;
import com.github.pulsebeat02.utility.ChatUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ImageCommand extends AbstractCommand implements CommandExecutor {

    private final Map<Integer, String> images;

    public ImageCommand(@NotNull DeluxeMediaPlugin plugin) {
        super(plugin);
        this.images = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtilities.formatMessage(ChatColor.RED + "You must be a player to use this command!"));
            return true;
        }
        if (args.length == 0) {

        }
        return true;
    }

}