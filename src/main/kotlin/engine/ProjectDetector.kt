package engine

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

class ProjectDetector(private val folder: File) {
    data class Project(val frameWork: FrameWorkDetector.FrameWork, val language: LanguageDetector.Language)

    fun detect(): Project? {
        val frameWork = FrameWorkDetector(folder).detect() ?: return null
        val language = LanguageDetector(frameWork, folder).detect() ?: return null
        return Project(frameWork, language)
    }
}

class FrameWorkDetector(private val folder: File) {

    enum class FrameWorks { EXPRESS, NEXT, REACT, NA }
    data class FrameWork(
        val name: FrameWorks,
        val version: String,
        val buildFile: String,
        val excludeFolders: List<File>
    )

    fun detect(): FrameWork? {
        return detectNext() ?: detectReact() ?: detectExpress()
    }

    private fun parsePackageJson(): JsonObject? {
        val packageJsonFile = folder.resolve("package.json")
        return if (packageJsonFile.exists()) {
            JsonParser.parseString(packageJsonFile.readText()).asJsonObject
        } else {
            null
        }
    }

    private fun detectExpress(): FrameWork? {
        return try {
            val packageJson = parsePackageJson() ?: return null
            val dependencies = packageJson.getAsJsonObject("dependencies") ?: JsonObject()

            dependencies.get("express")?.asString?.let { expressVersion ->
                return FrameWork(
                    FrameWorks.EXPRESS,
                    expressVersion,
                    packageJson.toString(),
                    listOf(folder.resolve("node_modules"))
                )
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun detectNext(): FrameWork? {
        return try {
            val packageJson = parsePackageJson() ?: return null
            val dependencies = packageJson.getAsJsonObject("dependencies") ?: JsonObject()

            dependencies.get("next")?.asString?.let { nextVersion ->
                val nextJsOutFolder = folder.resolve("next.config.js")
                val outDir = if (nextJsOutFolder.exists()) {
                    val configJson = JsonParser.parseString(nextJsOutFolder.readText()).asJsonObject
                    configJson.get("outDir")?.asString ?: ".next"
                } else ".next"

                return FrameWork(
                    FrameWorks.NEXT,
                    nextVersion,
                    packageJson.toString(),
                    listOf(folder.resolve("node_modules"), folder.resolve(outDir))
                )
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun detectReact(): FrameWork? {
        return try {
            val packageJson = parsePackageJson() ?: return null
            val dependencies = packageJson.getAsJsonObject("dependencies") ?: JsonObject()

            dependencies.get("react")?.asString?.let { reactVersion ->
                return FrameWork(
                    FrameWorks.REACT,
                    reactVersion,
                    packageJson.toString(),
                    listOf(folder.resolve("node_modules"))
                )
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}

class LanguageDetector(private val frameWork: FrameWorkDetector.FrameWork, private val folder: File) {

    enum class Languages { JAVASCRIPT, TYPESCRIPT }

    data class Language(val name: String, val exts: List<String>)

    fun detect(): Language? {
        return detectTypeScript() ?: detectJavaScript()
    }

    private fun detectJavaScript(): Language? {
        val excludedFolders = frameWork.excludeFolders.map { it.canonicalFile }
        val jsFiles = folder.walkTopDown()
            .filter { file -> file.isFile && file.extension in listOf("js", "jsx") }
            .filter { file -> excludedFolders.none { file.canonicalFile.startsWith(it) } }
            .toList()

        return if (jsFiles.isNotEmpty()) Language(Languages.JAVASCRIPT.name, listOf("js", "jsx")) else null
    }

    private fun detectTypeScript(): Language? {
        val excludedFolders = frameWork.excludeFolders.map { it.canonicalFile }
        val tsFiles = folder.walkTopDown()
            .filter { file -> file.isFile && file.extension in listOf("ts", "tsx") }
            .filter { file -> excludedFolders.none { file.canonicalFile.startsWith(it) } }
            .toList()

        return if (tsFiles.isNotEmpty()) Language(Languages.TYPESCRIPT.name, listOf("ts", "tsx")) else null
    }
}
