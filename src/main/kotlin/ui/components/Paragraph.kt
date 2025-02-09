package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle

@Composable
fun Paragraph(text: String): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        text.split("\n").forEachIndexed { index, line ->
            if (index > 0) {
                append("\n")
            }
            withStyle(style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace).toSpanStyle()) {
                append(line)
            }
        }
    }

    return annotatedString
}