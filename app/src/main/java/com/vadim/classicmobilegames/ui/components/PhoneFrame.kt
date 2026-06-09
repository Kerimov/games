package com.vadim.classicmobilegames.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vadim.classicmobilegames.ui.theme.RetroColors
import com.vadim.classicmobilegames.ui.theme.RetroTypography

@Composable
fun PhoneFrame(
    title: String,
    score: String = "",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RetroColors.PhoneBody)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RETRO",
            style = RetroTypography.Title.copy(color = RetroColors.TextOnPhone),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .border(3.dp, RetroColors.PhoneBorder, RoundedCornerShape(4.dp))
                .background(RetroColors.ScreenBg)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (title.isNotEmpty() || score.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        if (title.isNotEmpty()) {
                            Text(
                                text = title,
                                style = RetroTypography.Score,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        if (score.isNotEmpty()) {
                            Text(
                                text = score,
                                style = RetroTypography.Score,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }
}
