@file:OptIn(ExperimentalTextApi::class)

package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.File
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.LocalBoardTheme
import dev.mcd.chess.ui.extension.topLeft

@Composable
fun Squares(
    perspective: Side,
    squareSize: Float,
) {
    val textMeasurer = rememberTextMeasurer()
    val boardTheme = LocalBoardTheme.current
    val darkSquareColor = boardTheme.squareDark
    val lightSquareColor = boardTheme.squareLight
    val squareLabelStyleLight = boardTheme.squareTextStyle.copy(color = darkSquareColor)
    val squareLabelStyleDark = boardTheme.squareTextStyle.copy(color = lightSquareColor)

    Canvas(
        Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Board" },
    ) {
        for (square in Square.values()) {
            if (square != Square.NONE) {
                val color = if (square.isLightSquare) lightSquareColor else darkSquareColor
                drawRect(
                    color = color,
                    topLeft = square.topLeft(perspective, squareSize),
                    size = Size(squareSize, squareSize),
                )
                drawRanks(textMeasurer, square, perspective, squareSize, squareLabelStyleLight, squareLabelStyleDark)
                drawFiles(textMeasurer, square, perspective, squareSize, squareLabelStyleLight, squareLabelStyleDark)
            }
        }
    }
}

context(DrawScope)
private fun drawRanks(
    textMeasurer: TextMeasurer,
    square: Square,
    perspective: Side,
    squareSize: Float,
    squareLabelStyleLight: TextStyle,
    squareLabelStyleDark: TextStyle,
) {
    if (
        (perspective == Side.WHITE && square.file == File.FILE_A) ||
        (perspective == Side.BLACK && square.file == File.FILE_H)
    ) {
        val style = if (square.isLightSquare) squareLabelStyleLight else squareLabelStyleDark
        drawText(
            textMeasurer = textMeasurer,
            style = style,
            text = square.rank.notation,
            topLeft = square.topLeft(perspective, squareSize).plus(Offset(2.dp.toPx(), 2.dp.toPx())),
        )
    }
}

context(DrawScope)
private fun drawFiles(
    textMeasurer: TextMeasurer,
    square: Square,
    perspective: Side,
    squareSize: Float,
    squareLabelStyleLight: TextStyle,
    squareLabelStyleDark: TextStyle,
) {
    if (
        (perspective == Side.WHITE && square.rank.ordinal == 0) ||
        (perspective == Side.BLACK && square.rank.ordinal == 8)
    ) {
        val style = if (square.isLightSquare) squareLabelStyleLight else squareLabelStyleDark
        val label = square.file.notation.lowercase()
        val offset = Offset(
            x = squareSize - textMeasurer.measure(label).size.width - 1.dp.toPx(),
            y = squareSize - style.lineHeight.roundToPx() - 4.dp.toPx(),
        )
        drawText(
            textMeasurer = textMeasurer,
            style = style,
            text = label,
            topLeft = square.topLeft(perspective, squareSize).plus(offset),
        )
    }
}


