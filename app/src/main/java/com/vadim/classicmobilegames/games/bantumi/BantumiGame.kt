package com.vadim.classicmobilegames.games.bantumi

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.vadim.classicmobilegames.ui.components.DpadControls
import com.vadim.classicmobilegames.ui.components.PhoneFrame
import com.vadim.classicmobilegames.ui.theme.RetroColors

@Composable
fun BantumiGameScreen(onBack: () -> Unit) {
    var pits by remember { mutableStateOf(IntArray(14) { if (it == 6 || it == 13) 0 else 4 }) }
    var cursor by remember { mutableIntStateOf(0) }
    var currentPlayer by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Ваш ход") }

    fun opponentPit(p: Int) = 12 - p

    fun checkEnd(): Boolean {
        val playerTotal = (0..5).sumOf { pits[it] }
        val oppTotal = (7..12).sumOf { pits[it] }
        if (playerTotal == 0 || oppTotal == 0) {
            pits[6] += playerTotal
            pits[13] += oppTotal
            for (i in 0..5) pits[i] = 0
            for (i in 7..12) pits[i] = 0
            message = when {
                pits[6] > pits[13] -> "Вы победили!"
                pits[6] < pits[13] -> "Вы проиграли"
                else -> "Ничья"
            }
            currentPlayer = -1
            return true
        }
        return false
    }

    fun sowFrom(startPit: Int, isPlayer: Boolean) {
        var stones = pits[startPit]
        pits[startPit] = 0
        var pit = startPit
        var lastPit = -1

        while (stones > 0) {
            pit = (pit + 1) % 14
            if (pit == 6 || pit == 13) continue
            pits[pit]++
            stones--
            lastPit = pit
        }

        if (isPlayer && lastPit in 0..5 && pits[lastPit] == 1) {
            val opp = opponentPit(lastPit)
            if (pits[opp] > 0) {
                pits[6] += pits[lastPit] + pits[opp]
                pits[lastPit] = 0
                pits[opp] = 0
            }
        } else if (!isPlayer && lastPit in 7..12 && pits[lastPit] == 1) {
            val opp = opponentPit(lastPit)
            if (pits[opp] > 0) {
                pits[13] += pits[lastPit] + pits[opp]
                pits[lastPit] = 0
                pits[opp] = 0
            }
        }
    }

    fun aiMove() {
        val options = (7..12).filter { pits[it] > 0 }
        if (options.isEmpty()) return
        val best = options.maxByOrNull { pits[it] } ?: return
        sowFrom(best, isPlayer = false)
        if (checkEnd()) return
        currentPlayer = 0
        message = "Ваш ход"
    }

    fun playerMove(pit: Int) {
        if (currentPlayer != 0 || pit !in 0..5 || pits[pit] == 0) return
        sowFrom(pit, isPlayer = true)
        if (checkEnd()) return
        currentPlayer = 1
        message = "Ход соперника"
        Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
    }

    fun reset() {
        pits = IntArray(14) { if (it == 6 || it == 13) 0 else 4 }
        cursor = 0
        currentPlayer = 0
        message = "Ваш ход"
    }

    PhoneFrame(
        title = "Bantumi",
        score = "${pits[6]} : ${pits[13]}"
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(pits) {
                    detectTapGestures { offset ->
                        val pitW = size.width / 6
                        if (offset.y > size.height * 0.55f) {
                            val idx = (offset.x / pitW).toInt().coerceIn(0, 5)
                            playerMove(idx)
                        }
                    }
                }
        ) {
            val pitW = size.width / 6
            val pitH = size.height * 0.35f

            drawRect(RetroColors.ScreenBg, size = size)

            for (i in 7..12) {
                val displayIdx = 12 - i
                val x = displayIdx * pitW
                val y = size.height * 0.05f
                drawOval(
                    color = RetroColors.ScreenDark,
                    topLeft = Offset(x + 4, y),
                    size = Size(pitW - 8, pitH)
                )
                drawStoneCount(pits[i], x + pitW / 2, y + pitH / 2, pitW)
            }

            drawOval(
                color = RetroColors.ScreenDark,
                topLeft = Offset(0f, size.height * 0.42f),
                size = Size(pitW - 4, size.height * 0.16f)
            )
            drawStoneCount(pits[13], pitW / 2, size.height * 0.5f, pitW)

            drawOval(
                color = RetroColors.ScreenDark,
                topLeft = Offset(size.width - pitW + 4, size.height * 0.42f),
                size = Size(pitW - 4, size.height * 0.16f)
            )
            drawStoneCount(pits[6], size.width - pitW / 2, size.height * 0.5f, pitW)

            for (i in 0..5) {
                val x = i * pitW
                val y = size.height * 0.62f
                val selected = cursor == i
                drawOval(
                    color = if (selected) RetroColors.Highlight else RetroColors.PixelLight.copy(alpha = 0.5f),
                    topLeft = Offset(x + 4, y),
                    size = Size(pitW - 8, pitH)
                )
                drawStoneCount(pits[i], x + pitW / 2, y + pitH / 2, pitW)
            }

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#0F380F")
                    textSize = 20f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(message, size.width / 2, size.height * 0.4f, paint)
            }
        }
    }

    DpadControls(
        onLeft = { if (cursor > 0) cursor-- },
        onRight = { if (cursor < 5) cursor++ },
        onCenter = {
            if (currentPlayer == -1) reset()
            else playerMove(cursor)
        },
        onBack = onBack,
        centerLabel = if (currentPlayer == -1) "↻" else "OK"
    )
}

private fun DrawScope.drawStoneCount(count: Int, cx: Float, cy: Float, pitW: Float) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#0F380F")
            textSize = pitW * 0.35f
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
        drawText(count.toString(), cx, cy + pitW * 0.12f, paint)
    }
}
