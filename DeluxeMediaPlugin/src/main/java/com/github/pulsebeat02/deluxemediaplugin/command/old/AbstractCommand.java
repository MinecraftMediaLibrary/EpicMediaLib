/*
 * ============================================================================
 * Copyright (C) PulseBeat_02 - All Rights Reserved
 *
 * This file is part of MinecraftMediaLibrary
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Brandon Li <brandonli2006ma@gmail.com>, 2/11/2021
 * ============================================================================
 */

package com.github.pulsebeat02.deluxemediaplugin.command.old;

import com.github.pulsebeat02.deluxemediaplugin.DeluxeMediaPlugin;
import com.github.pulsebeat02.deluxemediaplugin.utility.ChatUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbstractCommand implements CommandExecutor, TabCompleter {

  private final DeluxeMediaPlugin plugin;
  private final String name;
  private final AbstractCommand parent;
  private final Map<String, AbstractCommand> children;
  private final List<String> history;

  public AbstractCommand(
      @NotNull final DeluxeMediaPlugin plugin,
      @Nullable final AbstractCommand parent,
      @NotNull final String name,
      @NotNull final String suggestion) {
    this.plugin = plugin;
    this.parent = parent;
    history = new ArrayList<>();
    children = new HashMap<>();
    this.name = name;
    history.add(suggestion);
  }

  @Override
  public boolean onCommand(
      @NotNull final CommandSender sender,
      @NotNull final Command command,
      @NotNull final String s,
      final @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          ChatUtilities.formatMessage(ChatColor.RED + "You must be a player to use this command!"));
    }
    performCommandTask(sender, command, s, args);
    return true;
  }

  @Override
  public List<String> onTabComplete(
      @NotNull final CommandSender sender,
      @NotNull final Command command,
      @NotNull final String s,
      final String[] args) {
    if (children.values().isEmpty()) {
      return history;
    }
    return children.values().stream().map(AbstractCommand::getName).collect(Collectors.toList());
  }

  public void performCommandTask(
      @NotNull final CommandSender sender,
      @NotNull final Command command,
      @NotNull final String s,
      final @NotNull String[] args) {}

  public void addHistoryEntry(@NotNull final String autofill) {
    history.add(autofill);
  }

  public void addChild(@NotNull final AbstractCommand child) {
    children.put(child.getName(), child);
  }

  public DeluxeMediaPlugin getPlugin() {
    return plugin;
  }

  public List<String> getHistory() {
    return history;
  }

  public String getName() {
    return name;
  }

  public Map<String, AbstractCommand> getChildren() {
    return children;
  }
}