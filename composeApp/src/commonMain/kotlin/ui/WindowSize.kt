package ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

enum class WindowSize {
    Compact, Medium, Expanded
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSize {
    val windowInfo = LocalWindowInfo.current
    val width = windowInfo.containerSize.width

    val density = androidx.compose.ui.platform.LocalDensity.current
    val widthDp = with(density) { width.toDp() }
    
    return when {
        widthDp < 600.dp -> WindowSize.Compact
        widthDp < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}
