/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed -> in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.konan.target

import org.jetbrains.kotlin.konan.properties.*

class LinuxConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : LinuxConfigurables, KonanPropertiesLoader(target, properties, baseDir)

class LinuxMIPSConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : LinuxMIPSConfigurables, KonanPropertiesLoader(target, properties, baseDir)

class AndroidConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : AndroidConfigurables, KonanPropertiesLoader(target, properties, baseDir)

class MingwConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : MingwConfigurables, KonanPropertiesLoader(target, properties, baseDir)

class WasmConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : WasmConfigurables, KonanPropertiesLoader(target, properties, baseDir)

class PsvitaConfigurablesImpl(override val target: KonanTarget, properties: Properties, baseDir: String?)
    : Configurables, KonanPropertiesLoader(target, properties, baseDir) {
    override val dependencies get() = ArrayList<String>()
    val directory = ""

    override fun targetString(key: String): String? {
        return when (key) {
            "targetSysRoot" -> "targetSysRoot"
            "quadruple" -> "arm-vita-eabi"
            "arch" -> "arm"
            else -> "psvita-targetString-$key"
        }
    }

    override fun targetList(key: String): List<String> {
        return when (key) {
            else -> ArrayList(listOf("---psvita-targetList-$key"))
        }
    }

    override fun hostString(key: String): String? {
        return when (key) {
            "llvmHome" -> "/usr/local/vitasdk/arm-vita-eabi"
            else -> "$key"
        }
    }

    override fun hostList(key: String): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hostTargetString(key: String): String? {
        return "PSVITA_TARGET_TOOLCHAIN"
    }

    override fun hostTargetList(key: String): List<String> {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return listOf()
    }

    override fun absolute(value: String?): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return value ?: ""
    }

    override fun downloadDependencies() {
    }
}

class ZephyrConfigurablesImpl(target: KonanTarget, properties: Properties, baseDir: String?)
    : ZephyrConfigurables, KonanPropertiesLoader(target, properties, baseDir)


fun loadConfigurables(target: KonanTarget, properties: Properties, baseDir: String?) = when (target)  {
        KonanTarget.LINUX_X64, KonanTarget.LINUX_ARM32_HFP ->
            LinuxConfigurablesImpl(target, properties, baseDir)
        KonanTarget.LINUX_MIPS32, KonanTarget.LINUX_MIPSEL32 ->
            LinuxMIPSConfigurablesImpl(target, properties, baseDir)
        KonanTarget.MACOS_X64, KonanTarget.IOS_ARM64, KonanTarget.IOS_X64 ->
            AppleConfigurablesImpl(target, properties, baseDir)
        KonanTarget.ANDROID_ARM32, KonanTarget.ANDROID_ARM64 ->
            AndroidConfigurablesImpl(target, properties, baseDir)
        KonanTarget.MINGW_X64 ->
            MingwConfigurablesImpl(target, properties, baseDir)
        KonanTarget.WASM32 ->
            WasmConfigurablesImpl(target, properties, baseDir)
        KonanTarget.PSVITA_ARM32 ->
            PsvitaConfigurablesImpl(target, properties, baseDir)
        is KonanTarget.ZEPHYR ->
            ZephyrConfigurablesImpl(target, properties, baseDir)
    }

