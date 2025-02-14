package engine

import com.google.gson.Gson
import java.io.File

class Engine(private val folder: File) {

    var projectDetected = false
    var project = ProjectDetector(folder).detect().apply {
        if (this != null) {
            projectDetected = true
        }
    }

    fun execute(command: String, onUpdate: (String) -> Unit) {
        val files = folder.walk().filter {
            val excludedPaths = project!!.frameWork.excludeFolders
            !excludedPaths.any { excludedPath -> it.startsWith(excludedPath) } && project!!.language.exts.contains(it.extension)
        }.toList()

        val filesData: List<FileDetails> = files.map { FileDetails(it.name, it.path, it.readText()) }
        val contextBuilder = ContextBuilder(project!!, command, filesData.toMutableList())
        val context = contextBuilder.build()

        File("test","models.isnt").writeText(ModelInstructions.buildInstruction)
        val response = Gemini(
            modelInstructions = ModelInstructions.buildInstruction,
            model = GeminiModel.PRO_2
        ).generate(Gson().toJson(context).replace("\n","")).replace("```json", "").replace("```", "").replace("\n","")
        File("test","response.json").writeText(response)
        val result = Gson().fromJson(response, List::class.java)
        result.forEach {
            onUpdate(it.toString())
        }
    }
}