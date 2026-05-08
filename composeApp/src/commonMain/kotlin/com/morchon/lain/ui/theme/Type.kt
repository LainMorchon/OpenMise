package com.morchon.lain.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.GoogleSans_VariableFont
import openmise.composeapp.generated.resources.NunitoSans_VariableFont
import org.jetbrains.compose.resources.Font

@Composable
fun getTypography(): Typography {
    val nunitoSans = FontFamily(
        Font(Res.font.NunitoSans_VariableFont, FontWeight.Normal)
    )

    val googleSans = FontFamily(
        Font(Res.font.GoogleSans_VariableFont, FontWeight.Normal)
    )

    return Typography(
        displayMedium = TextStyle(
            fontFamily = nunitoSans,
            fontWeight = FontWeight.Bold,
            fontSize = 47.sp,
            lineHeight = 54.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = nunitoSans,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 42.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = nunitoSans,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = nunitoSans,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = googleSans,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = googleSans,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.25.sp
        ),
        labelLarge = TextStyle(
            fontFamily = googleSans,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.1.sp
        )
    )
}
