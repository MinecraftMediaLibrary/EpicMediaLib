/*............................................................................................
 . Copyright © 2021 Brandon Li                                                               .
 .                                                                                           .
 . Permission is hereby granted, free of charge, to any person obtaining a copy of this      .
 . software and associated documentation files (the “Software”), to deal in the Software     .
 . without restriction, including without limitation the rights to use, copy, modify, merge, .
 . publish, distribute, sublicense, and/or sell copies of the Software, and to permit        .
 . persons to whom the Software is furnished to do so, subject to the following conditions:  .
 .                                                                                           .
 . The above copyright notice and this permission notice shall be included in all copies     .
 . or substantial portions of the Software.                                                  .
 .                                                                                           .
 . THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,                           .
 .  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF                       .
 .   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                                   .
 .   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                     .
 .   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN                      .
 .   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN                       .
 .   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                        .
 .   SOFTWARE.                                                                               .
 ............................................................................................*/
package io.github.pulsebeat02.minecraftmedialibrary.dependency.vlc

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.pulsebeat02.minecraftmedialibrary.vlc.os.linux.LinuxOSPackages
import io.github.pulsebeat02.minecraftmedialibrary.vlc.os.linux.LinuxPackage
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

object LinuxPackageJSONTest {

    private var MAP_STRING_LIST_LINUX_PACKAGE_TYPE_TOKEN: TypeToken<Map<String, List<LinuxPackage>>>? = null
    private var MAP_STRING_LINUX_OS_PACKAGE_TYPE_TOKEN: TypeToken<Map<String, LinuxOSPackages>>? = null
    private var GSON: Gson? = null

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val packages = GSON!!.fromJson<Map<String, List<LinuxPackage>>>(
            fileContents,
            MAP_STRING_LINUX_OS_PACKAGE_TYPE_TOKEN!!.type
        )
    }

    @get:Throws(IOException::class)
    private val fileContents: String
        private get() {
            val name = "linux-package-installation.json"
            val loader = LinuxPackageJSONTest::class.java.classLoader
            val input = loader.getResourceAsStream(name)
            return if (input == null) {
                throw IllegalArgumentException("file not found! $name")
            } else {
                IOUtils.toString(input, StandardCharsets.UTF_8.name())
            }
        }

    private class LinuxOSPackagesAdapter : TypeAdapter<LinuxOSPackages>() {
        override fun write(out: JsonWriter, linuxOSPackages: LinuxOSPackages) {
            GSON!!.toJson(
                linuxOSPackages.links.asMap(),
                MAP_STRING_LIST_LINUX_PACKAGE_TYPE_TOKEN!!.type,
                out
            )
        }

        override fun read(`in`: JsonReader): LinuxOSPackages {
            val map = GSON!!.fromJson<Map<String, Collection<LinuxPackage>>>(
                `in`,
                MAP_STRING_LIST_LINUX_PACKAGE_TYPE_TOKEN!!.type
            )
            val multimap: ListMultimap<String, LinuxPackage> = ArrayListMultimap.create()
            map.forEach { (k: String?, iterable: Collection<LinuxPackage>?) -> multimap.putAll(k, iterable) }
            return LinuxOSPackages(multimap)
        }
    }

    init {
        MAP_STRING_LIST_LINUX_PACKAGE_TYPE_TOKEN = object : TypeToken<Map<String, List<LinuxPackage>>>() {}
        MAP_STRING_LINUX_OS_PACKAGE_TYPE_TOKEN = object : TypeToken<Map<String, LinuxOSPackages>>() {}
        GSON = GsonBuilder()
            .registerTypeAdapter(LinuxOSPackages::class.java, LinuxOSPackagesAdapter())
            .setPrettyPrinting()
            .create()
    }
}