package engine

import java.io.File
import java.nio.file.Files

class Terminal(dir:File) {
    private var currentDirectory = dir

    fun executeCommand(command: String, onLog:(log:String)->Unit): String {
        return runScript(command, onLog)
    }

    private fun runScript(script: String, onLog: (log: String) -> Unit): String {
        val tempScript = Files.createTempFile("script", ".sh").toFile()
        tempScript.writeText(script)
        tempScript.setExecutable(true)
        return try {
            val process = ProcessBuilder(tempScript.absolutePath)
                .directory(currentDirectory)
                .redirectErrorStream(true)
                .start()
            process.inputStream.bufferedReader().forEachLine {
                onLog(it)
            }
            val result = process.inputStream.bufferedReader().readText()
            process.waitFor()
            result
        } catch (e: Exception) {
            "Error: ${e.message}"
        } finally {
            tempScript.delete()
        }
    }

    fun changeDirectory(path: String?): String {
        if (path == null) return "Path not specified"
        val newDirectory = File(path).canonicalFile
        return if (newDirectory.exists() && newDirectory.isDirectory) {
            currentDirectory = newDirectory
            "Changed directory to: ${currentDirectory.path}"
        } else {
            "No such directory: $path"
        }
    }

    private fun runCommand(command: String): String {
        return try {
            val process = ProcessBuilder(command.split(" "))
                .directory(currentDirectory)
                .redirectErrorStream(true)
                .start()
            val result = process.inputStream.bufferedReader().readText()
            process.waitFor()
            result
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun getCurrentDirectory(): String = currentDirectory.path
}