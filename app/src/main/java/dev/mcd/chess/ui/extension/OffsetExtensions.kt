package dev.mcd.chess.ui.extension

import androidx.compose.ui.geometry.Offset

fun Offset.orZero() = takeIf { it != Offset.Unspecified } ?: Offset.Zero
