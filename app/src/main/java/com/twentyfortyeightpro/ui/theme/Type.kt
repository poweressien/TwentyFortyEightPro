package com.twentyfortyeightpro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Using system sans-serif for now (no bundled .ttf files yet). The web app
 * uses Space Grotesk (display) + Inter (body) — to match exactly, drop
 * matching .ttf files into res/font/ and swap FontFamily.Default below for
 * FontFamily(Font(R.font.space_grotesk_bold)), etc.
 */
private val DisplayFont = FontFamily.SansSerif
private val BodyFont = FontFamily.Default

val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = DisplayFont, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = BodyFont, fontWeight = FontWeight.Bold, fontSize = 11.sp),
)
