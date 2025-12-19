package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AdaptiveLayout(
    showTwoPanes: Boolean,
    firstPane: @Composable () -> Unit,
    secondPane: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showTwoPanes) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                firstPane()
            }
            // 分割线
            Box(Modifier.fillMaxHeight().width(1.dp).background(MiuixTheme.colorScheme.outline))
            Box(modifier = Modifier.weight(2.5f).fillMaxHeight()) {
                secondPane()
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            firstPane()
        }
    }
}
