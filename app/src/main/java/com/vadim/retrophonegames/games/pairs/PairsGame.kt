package com.vadim.retrophonegames.games.pairs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.vadim.retrophonegames.ui.components.DpadControls
import com.vadim.retrophonegames.ui.components.PhoneFrame
import com.vadim.retrophonegames.ui.theme.RetroColors
import kotlinx.coroutines.delay

@Composable
fun PairsGameScreen(onBack: () -> Unit) {
    val symbols = listOf('♠', '♥', '♦', '♣', '★', '●', '▲', '■')
    val cards = remember {
        (symbols + symbols).shuffled().mapIndexed { i, s -> i to s }
    }
    val cols = 4
    val rows = 4

    var selected by remember { mutableStateOf(-1) }
    var matched by remember { mutableStateOf(setOf<Int>()) }
    var flipped by remember { mutableStateOf(setOf<Int>()) }
    var cursor by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(0) }
    var lock by remember { mutableStateOf(false) }
    var won by remember { mutableStateOf(false) }

    fun reset() {
        selected = -1
        matched = setOf()
        flipped = setOf()
        cursor = 0
        moves = 0
        lock = false
        won = false
    }

    fun flipCard(index: Int) {
        if (lock || index in matched || index in flipped) return
        if (selected == -1) {
            selected = index
            flipped = flipped + index
        } else if (selected != index) {
            flipped = flipped + index
            moves++
            val first = cards[selected].second
            val second = cards[index].second
            if (first == second) {
                matched = matched + selected + index
                selected = -1
                if (matched.size == cards.size) won = true
            } else {
                lock = true
            }
        }
    }

    LaunchedEffect(lock) {
        if (lock) {
            delay(800)
            flipped = flipped - selected - flipped.last()
            selected = -1
            lock = false
        }
    }

    PhoneFrame(
        title = "Pairs II",
        score = "Ходы: $moves"
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val cellW = size.width / cols
                        val cellH = size.height / rows
                        val col = (offset.x / cellW).toInt().coerceIn(0, cols - 1)
                        val row = (offset.y / cellH).toInt().coerceIn(0, rows - 1)
                        flipCard(row * cols + col)
                    }
                }
        ) {
            val cellW = size.width / cols
            val cellH = size.height / rows

            for (i in cards.indices) {
                val col = i % cols
                val row = i / cols
                val x = col * cellW
                val y = row * cellH
                val isFlipped = i in flipped || i in matched
                val isCursor = i == cursor

                drawRect(
                    color = if (isFlipped) RetroColors.Highlight else RetroColors.ScreenDark,
                    topLeft = Offset(x + 2, y + 2),
                    size = Size(cellW - 4, cellH - 4)
                )

                if (isCursor) {
                    drawRect(
                        color = RetroColors.Pixel,
                        topLeft = Offset(x + 1, y + 1),
                        size = Size(cellW - 2, cellH - 2),
                        style = Stroke(width = 2f)
                    )
                }

                if (isFlipped) {
                    val symbol = cards[i].second.toString()
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#0F380F")
                            textSize = cellH * 0.4f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            symbol,
                            x + cellW / 2,
                            y + cellH / 2 + cellH * 0.15f,
                            paint
                        )
                    }
                }
            }
        }
    }

    DpadControls(
        onUp = { if (cursor >= cols) cursor -= cols },
        onDown = { if (cursor < cols * (rows - 1)) cursor += cols },
        onLeft = { if (cursor % cols > 0) cursor-- },
        onRight = { if (cursor % cols < cols - 1) cursor++ },
        onCenter = {
            if (won) reset()
            else flipCard(cursor)
        },
        onBack = onBack,
        centerLabel = if (won) "↻" else "OK"
    )
}
