package engine

import com.google.gson.Gson
import com.google.gson.JsonParser
import dto.Content
import dto.GeminiResponse
import dto.Part
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
        File("test", "prompt.txt").writeText(prompt)

        contents.add(
            Content(
                "user",
                listOf(
                    Part(prompt)
                )
            )
        )

        val gson = Gson().newBuilder().disableHtmlEscaping().create()
        val body = gson.toJson(
            mapOf(
                "generationConfig" to generationConfig,
                "contents" to contents,
                "systemInstruction" to modelInstruction
            )
        )

        val request = Request.Builder()
            .url(baseUrl)
            .headers(headers.build())
            .post(body.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val res = client.newCall(request).execute()
        val response = res.body?.string() ?: ""
        val text = formatGeminiResponse(response)
        return text
    }


    fun formatGeminiResponse(response: String): String {
        val responseBody = JsonParser.parseString(response).asJsonObject
        val candidates = responseBody["candidates"].asJsonArray
        val content = candidates[0].asJsonObject["content"].asJsonObject
        val parts = content["parts"].asJsonArray
        val text = parts[0].asJsonObject["text"].asString

        return text
    }

}
