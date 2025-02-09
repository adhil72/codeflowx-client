package dto

data class RequiredFiles(
    val required: List<String>
)

data class TerminalCommands(
    val commands: List<String>,
    val message: String
)