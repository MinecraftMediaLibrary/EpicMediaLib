/*
 * ============================================================================
 * Copyright (C) PulseBeat_02 - All Rights Reserved
 *
 * This file is part of MinecraftMediaLibrary
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Brandon Li <brandonli2006ma@gmail.com>, 3/3/2021
 * ============================================================================
 */
package com.github.pulsebeat02.minecraftmedialibrary.test.dependency.vlc

import com.github.pulsebeat02.minecraftmedialibrary.dependency.vlc.EnchancedNativeDiscovery
import com.github.pulsebeat02.minecraftmedialibrary.dependency.vlc.VLCNativeDependencyFetcher
import java.io.File

fun main(args: Array<String>) {
    val path = File(System.getProperty("user.dir") + "/vlc").absolutePath
    val fetcher = VLCNativeDependencyFetcher(path)
    fetcher.downloadLibraries()
    val enchancedNativeDiscovery = EnchancedNativeDiscovery(path)
    println(enchancedNativeDiscovery.discover())
}