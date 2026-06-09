package com.vadim.retrophonegames.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object RetroColors {
    val ScreenBg = Color(0xFF9BBC0F)
    val ScreenDark = Color(0xFF8BAC0F)
    val Pixel = Color(0xFF0F380F)
    val PixelLight = Color(0xFF306230)
    val PhoneBody = Color(0xFF1A3A1A)
    val PhoneBorder = Color(0xFF2D5A2D)
    val ButtonBg = Color(0xFF3D6B3D)
    val ButtonPressed = Color(0xFF2A4A2A)
    val Highlight = Color(0xFFCBE870)
    val TextOnPhone = Color(0xFFCBE870)
}

object RetroTypography {
    val Title = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = RetroColors.Pixel
    )
    val Body = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = RetroColors.Pixel
    )
    val Score = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = RetroColors.Pixel
    )
}
