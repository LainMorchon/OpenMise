package com.morchon.lain.ui.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.morchon.lain.ui.theme.MiseOrange
import com.morchon.lain.ui.theme.OpenGreen
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.ic_logo_app
import org.jetbrains.compose.resources.painterResource

@Composable
fun OpenMiseLogo(
    modifier: Modifier = Modifier,
    style: TextStyle,
    showIcon: Boolean = false,
    iconSize: androidx.compose.ui.unit.Dp = 32.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showIcon) {
            Icon(
                painter = painterResource(Res.drawable.ic_logo_app),
                contentDescription = null,
                modifier = Modifier.size(iconSize).padding(end = 8.dp),
                tint = Color.Unspecified
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MiseOrange)) {
                    append("Open")
                }
                withStyle(style = SpanStyle(color = OpenGreen)) {
                    append("Mise")
                }
            },
            style = style
        )
    }
}
