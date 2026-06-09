package com.vadim.retrophonegames.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vadim.retrophonegames.ui.components.DpadControls
import com.vadim.retrophonegames.ui.components.PhoneFrame
import com.vadim.retrophonegames.ui.theme.RetroColors
import com.vadim.retrophonegames.ui.theme.RetroTypography

data class GameEntry(val id: String, val name: String, val description: String)

val gameList = listOf(
    GameEntry("snake", "Snake II", "Классическая змейка"),
    GameEntry("spaceimpact", "Space Impact", "Космический шутер"),
    GameEntry("pairs", "Pairs II", "Найди пары"),
    GameEntry("bantumi", "Bantumi", "Африканские шашки"),
    GameEntry("racing", "Racing", "Гонки на выживание"),
    GameEntry("bounce", "Bounce", "Прыгающий мячик"),
)

@Composable
fun GameMenuScreen(onGameSelected: (String) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroColors.PhoneBody)
    ) {
        PhoneFrame(title = "Игры", score = "${selectedIndex + 1}/${gameList.size}") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                gameList.forEachIndexed { index, game ->
                    val isSelected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) RetroColors.Highlight else RetroColors.ScreenDark.copy(alpha = 0.3f)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedIndex = index
                                onGameSelected(game.id)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isSelected) "► ${game.name}" else "  ${game.name}",
                            style = RetroTypography.Body.copy(
                                color = if (isSelected) RetroColors.Pixel else RetroColors.PixelLight
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = gameList[selectedIndex].description,
                        style = RetroTypography.Score,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        DpadControls(
            onUp = { if (selectedIndex > 0) selectedIndex-- },
            onDown = { if (selectedIndex < gameList.size - 1) selectedIndex++ },
            onCenter = { onGameSelected(gameList[selectedIndex].id) },
            onBack = { },
            centerLabel = "OK",
            showDpad = true
        )
    }
}
