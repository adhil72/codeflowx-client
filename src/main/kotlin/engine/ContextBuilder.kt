package engine

data class FrameWork(val name:String, val version: String, val buildFile: String)
data class FileDetails(val name: String, val path: String, val content: String)
data class Project(val frameWork: FrameWork, private val languages: LanguageDetector.Language, val projectFiles: MutableList<FileDetails>)
data class Context(val project: Project, val prompt: String)


class ContextBuilder(
    private val project: ProjectDetector.Project,
    private val prompt: String,
    private val files: MutableList<FileDetails> = mutableListOf(),
) {

    fun build(): Context {
        return Context(
            Project(
                FrameWork(project.frameWork.name.toString(), project.frameWork.version, project.frameWork.buildFile),
                project.language,
                files
            ),
            prompt
        )
    }
}