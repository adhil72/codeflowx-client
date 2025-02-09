package engine

import com.google.gson.Gson
import dto.Content
import dto.GeminiResponse
import dto.Part
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class GeminiConfig(
    var temperature: Double = 1.0,
    var topP: Double = 0.95,
    var topK: Int = 40,
    var maxOutputTokens: Int = 8192,
    var responseMimeType: String = "text/plain"
)

enum class GeminiModel(val modelName: String) {
    FLASH_2("gemini-2.0-flash"),
    FLASH_THINK(" gemini-2.0-flash-thinking-exp-01-21"),
    PRO_2("gemini-2.0-pro-exp-02-05")
}

class Gemini(
    private val apiKey: String = System.getenv("GEMINI_API_KEY"),
    val generationConfig: GeminiConfig = GeminiConfig(),
    model: GeminiModel = GeminiModel.FLASH_2,
    modelInstructions: String = "normal chat"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
        .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
        .writeTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()

    private val baseUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/${model.modelName}:generateContent?key=${apiKey}"

    val headers = Headers.Builder()
        .add("content-type", "application/json")

    val contents = mutableListOf<Content>()
    val modelInstruction = Content(
        role = "user",
        parts = listOf(
            Part(modelInstructions)
        )
    )

    private fun countTokens(input: String): Int {
        val regex = Regex("""\w+|[^\w\s]""")
        val tokens = regex.findAll(input).map { it.value }
        return tokens.count()
    }

    fun generate(prompt: String): String {

        println("tokens : ${countTokens(prompt)}")

        contents.add(
            Content(
                "user",
                listOf(
                    Part(prompt)
                )
            )
        )

        val body = """
            {
                "generationConfig": ${Gson().toJson(generationConfig)},
                "contents": ${Gson().toJson(contents)},
                "systemInstruction": ${Gson().toJson(modelInstruction)}
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(baseUrl)
            .headers(headers.build())
            .post(body.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val res = client.newCall(request).execute()
        val resString = res.body!!.string()
        val responseBody = Gson().fromJson(resString, GeminiResponse::class.java)

        println(resString)

        contents.add(responseBody.candidates[0].content)
        return responseBody.candidates[0].content.parts[0].text
    }

}
