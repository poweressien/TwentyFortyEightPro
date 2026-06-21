package com.twentyfortyeightpro.ui.theme

import androidx.compose.ui.graphics.Color

// Mirrors static/css/main.css :root tokens so the native app and web app
// share one visual language.
val BgDeep = Color(0xFF0A0E1A)
val BgPanel = Color(0xFF141B2E)
val BgPanelRaised = Color(0xFF1C2540)
val BoardWell = Color(0xFF0D1220)
val CellEmpty = Color(0xFF1A2138)
val BorderSubtle = Color(0x14FFFFFF)
val BorderStrong = Color(0x29FFFFFF)

val Gold = Color(0xFFFFC857)
val GoldDeep = Color(0xFFE8A93D)
val GoldSoft = Color(0x29FFC857)
val Magenta = Color(0xFFFF4D9E)
val MagentaDeep = Color(0xFFD62F7D)
val Cyan = Color(0xFF4DD9E8)
val Success = Color(0xFF3ED9A4)
val Danger = Color(0xFFFF5C5C)

val TextPrimary = Color(0xFFF2EFFA)
val TextMuted = Color(0xFF8A90AC)
val TextFaint = Color(0xFF565D7A)

// Tile rarity tiers — same vocabulary as the Django Achievement.rarity field
// and the web CSS. This is the one place tile colors are defined; the game
// board reads from here so web and native always agree on what each tier
// looks like.
object TileTiers {
    val CommonStart = Color(0xFF2C3A66)
    val CommonEnd = Color(0xFF3A5A8C)
    val RareStart = Color(0xFF2F5FB8)
    val RareEnd = Color(0xFF6A4FD6)
    val EpicStart = Color(0xFF9B3FD6)
    val EpicEnd = Color(0xFFFF4D9E)
    val Legendary = Color(0xFFFFC857)
    val LegendaryGlow = Color(0xFFFFE8A3)
    val MythicStart = Color(0xFFFF2E63)
    val MythicEnd = Color(0xFFFF8A3D)
    val GalaxyA = Color(0xFFFF4D9E)
    val GalaxyB = Color(0xFF4DD9E8)
    val GalaxyC = Color(0xFFFFC857)
    val GalaxyD = Color(0xFF9B3FD6)
}
