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

package com.github.pulsebeat02.minecraftmedialibrary.logger;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  /**
   * The constant WRITER.
   */
  public static volatile BufferedWriter WRITER;
  /** The constant VERBOSE. */
  public static boolean VERBOSE;

  static {
    try {
      final File f = new File("mml.log");
      System.out.println(f.getAbsolutePath());
      if (f.createNewFile()) {
        System.out.println("File Created (" + f.getName() + ")");
      } else {
        System.out.println("Log File Exists Already");
      }
      WRITER = new BufferedWriter(new FileWriter(f, false));
    } catch (final IOException exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Info.
   *
   * @param info the info
   */
  public static void info(@NotNull final String info) {
    directPrint(System.currentTimeMillis() + ": [INFO] " + info + "\n");
  }

  /**
   * Warn.
   *
   * @param warning the warning
   */
  public static void warn(@NotNull final String warning) {
    directPrint(System.currentTimeMillis() + ": [WARN] " + warning + "\n");
  }

  /**
   * Error.
   *
   * @param error the error
   */
  public static void error(@NotNull final String error) {
    directPrint(System.currentTimeMillis() + ": [ERROR] " + error + "\n");
  }

  private static void directPrint(@NotNull final String line) {
    if (VERBOSE) {
      try {
        WRITER.write(line);
        WRITER.flush();
      } catch (final IOException exception) {
        exception.printStackTrace();
      }
    }
  }

  /**
   * Sets verbose.
   *
   * @param verbose the verbose
   */
  public static void setVerbose(final boolean verbose) {
    VERBOSE = verbose;
  }
}
