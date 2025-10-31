package com.pine.pinedroid.utils

import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.loge
import java.io.BufferedReader
import java.io.InputStreamReader
fun adbShell(cmd: String): String? {
    try {
        val process = Runtime.getRuntime().exec(cmd)

        // 读取标准输出
        val inputReader = BufferedReader(InputStreamReader(process.inputStream))
        // 读取错误输出
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))

        val output = StringBuilder()
        var line: String?

        // 读取标准输出
        while (inputReader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }

        // 读取错误输出（用于调试）
        val errorOutput = StringBuilder()
        while (errorReader.readLine().also { line = it } != null) {
            errorOutput.append(line).append("\n")
        }

        inputReader.close()
        errorReader.close()

        val exitCode = process.waitFor()

        val outputString = output.toString()
        logd("Command: $cmd", "Exit Code: $exitCode")
        logd("Output", outputString)

        if (errorOutput.isNotEmpty()) {
            loge("Error Output", errorOutput.toString())
        }

        return outputString.ifEmpty { null }

    } catch (e: Exception) {
        loge("Error executing command", "Cmd: $cmd, Error: ${e.message}")
        return null
    }
}