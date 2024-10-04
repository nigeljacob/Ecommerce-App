package com.nigel.ecommerce.activities.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = com.nigel.ecommerce.ui.theme.primary,
    secondary = com.nigel.ecommerce.ui.theme.secondary,
    background = com.nigel.ecommerce.ui.theme.black,
    secondaryContainer = com.nigel.ecommerce.ui.theme.lightBlack,
    onPrimary = com.nigel.ecommerce.ui.theme.white
)

private val LightColorScheme = lightColorScheme(
    primary = com.nigel.ecommerce.ui.theme.primary,
    secondary = com.nigel.ecommerce.ui.theme.secondary,
    background = com.nigel.ecommerce.ui.theme.white,
    secondaryContainer = com.nigel.ecommerce.ui.theme.lightWhite,
    onPrimary = com.nigel.ecommerce.ui.theme.black

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EcommerceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}