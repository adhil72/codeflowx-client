package engine

import com.google.gson.Gson
import java.io.File

class ProjectDetector(private val folder: File) {
    data class Project(val frameWork: FrameWorkDetector.FrameWork, val language: LanguageDetector.Language)

    fun detect(): Project? {
        val frameWork = FrameWorkDetector(folder).detect() ?: return null
        val language = LanguageDetector(frameWork, folder).detect(folder) ?: return null
        return Project(frameWork, language)
    }
}

class FrameWorkDetector(private val folder: File) {

    enum class FrameWorks { EXPRESS, NEXT, REACT }
    data class FrameWork(val name: FrameWorks, val version: String, val buildFile: String, val excludeFolders:List<File>)

    fun detect(): FrameWork? {
        return detectNext() ?: detectReact() ?: detectExpress()
    }

    private fun detectExpress(): FrameWork? {
        val packageJsonFile = folder.resolve("package.json")
        if (packageJsonFile.exists()) {
            val packageJson = Gson().toJsonTree(packageJsonFile.readText()).asJsonObject
            val dependencies = packageJson.get("dependencies")?.asJsonObject

            val expressVersion = dependencies?.get("express")?.asString
            if (expressVersion != null) {
                val framework = FrameWork(FrameWorks.EXPRESS, expressVersion, packageJsonFile.readText(), listOf(folder.resolve("node_modules")))
                return framework
            }

        }
        return null
    }

    private fun detectNext(): FrameWork? {
        val packageJsonFile = folder.resolve("package.json")
        if (packageJsonFile.exists()) {
            val packageJson = Gson().toJsonTree(packageJsonFile.readText()).asJsonObject
            val dependencies = packageJson.get("dependencies")?.asJsonObject

            val nextVersion = dependencies?.get("next")?.asString

            val nextJsOutFolder:String = Gson().toJsonTree(folder.resolve("next.config.js").readText()).asJsonObject.get("outDir")?.asString?:".next"
            if (nextVersion != null) {
                val framework = FrameWork(FrameWorks.NEXT, nextVersion, packageJsonFile.readText(), listOf(folder.resolve("node_modules"), folder.resolve(nextJsOutFolder)))
                return framework
            }

        }
        return null
    }

    private fun detectReact(): FrameWork? {
        val packageJsonFile = folder.resolve("package.json")
        if (packageJsonFile.exists()) {
            val packageJson = Gson().toJsonTree(packageJsonFile.readText()).asJsonObject
            val dependencies = packageJson.get("dependencies")?.asJsonObject

            val reactVersion = dependencies?.get("react")?.asString
            if (reactVersion != null) {
                val framework = FrameWork(FrameWorks.REACT, reactVersion, packageJsonFile.readText(), listOf(folder.resolve("node_modules")))
                return framework
            }

        }
        return null
    }
}


class LanguageDetector(private val frameWork: FrameWorkDetector.FrameWork, private val folder: File) {

    enum class Languages {
        JAVASCRIPT, TYPESCRIPT
    }

    data class Language(val name: String, val exts: List<String>)

    fun detect(folder: File): Language? {
        return detectJavaScript() ?: detectTypeScript()
    }

    private fun detectJavaScript(): Language? {
        val excludedFolders = frameWork.excludeFolders
        var jsFiles = folder.walkTopDown().filter { file ->
            excludedFolders.none { excludeFolder ->
                file.canonicalFile.startsWith(excludeFolder)
            }
        }.toList()
        jsFiles = jsFiles.filter { it.extension == "js" }
        if(jsFiles.isNotEmpty()){
            return Language(Languages.JAVASCRIPT.name, listOf("js","jsx"))
        }
        return null
    }

    private fun detectTypeScript(): Language? {
        val excludedFolders = frameWork.excludeFolders
        var tsFiles = folder.walkTopDown().filter { file ->
            excludedFolders.none { excludeFolder ->
                file.canonicalFile.startsWith(excludeFolder)
            }
        }.toList()
        tsFiles = tsFiles.filter { it.extension == "ts" }
        if(tsFiles.isNotEmpty()){
            return Language(Languages.TYPESCRIPT.name, listOf("ts","tsx"))
        }
        return null
    }
}