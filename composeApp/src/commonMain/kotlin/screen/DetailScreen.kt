package screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import data.StickerPack
import data.StickerType
import data.allStickerPacks
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperBottomSheet
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import util.copyToClipboard

@Composable
fun DetailScreen(
    itemIndex: Int?,
    isWideScreen: Boolean = false,
    onBack: (() -> Unit)? = null,
    onStickerClick: ((StickerPack, Int) -> Unit)? = null
) {
    Scaffold(
        topBar = {
        SmallTopAppBar(
            title = if (itemIndex != null) allStickerPacks[itemIndex].name else "YumeFuwa", navigationIcon = {
                if (!isWideScreen) {
                    IconButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = { onBack?.invoke() }) {
                        Icon(MiuixIcons.Useful.Back, contentDescription = "返回")
                    }
                }
            })
    },

        popupHost = {}, content = { paddingValues ->
            if (itemIndex == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        uri = "https://avatars.githubusercontent.com/u/125112916?v=4",
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                }
            } else {
                remember(itemIndex) { allStickerPacks[itemIndex] }
                StickerGrid(
                    itemIndex = itemIndex, paddingValues = paddingValues, onStickerClick = onStickerClick
                )
            }
        })
}

@Composable
private fun StickerGrid(
    itemIndex: Int, paddingValues: PaddingValues, onStickerClick: ((StickerPack, Int) -> Unit)?
) {
    val gridState = rememberLazyGridState()
    val stickerPack = remember(itemIndex) { allStickerPacks[itemIndex] }

    val stickerUrls by remember(itemIndex) {
        derivedStateOf {
            stickerPack.getAllUrls(listOf(StickerType.PNG, StickerType.WEBP, StickerType.GIF))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        state = gridState,
        modifier = Modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            count = stickerUrls.size, key = { index -> stickerUrls[index] }) { index ->
            StickerGridItem(
                stickerUrl = stickerUrls[index], onClick = {
                    onStickerClick?.invoke(stickerPack, index)
                })
        }
    }
}

@Composable
private fun StickerGridItem(
    stickerUrl: String, onClick: () -> Unit
) {
    val rememberedUrl = remember(stickerUrl) { stickerUrl }

    Card(
        modifier = Modifier.aspectRatio(1f),
        pressFeedbackType = PressFeedbackType.Sink,
        showIndication = true,
        onClick = onClick,
        onLongPress = {}) {
        Box(
            modifier = Modifier.fillMaxSize().padding(4.dp), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                uri = rememberedUrl, contentDescription = null, modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun StickerDetailContent(
    stickerPack: StickerPack, stickerIndex: Int, previewSize: Int, onCopy: (String) -> Unit
) {
    var selectedTypeIndex by remember(stickerPack, stickerIndex) {
        mutableIntStateOf(stickerPack.types.indexOf(StickerType.WEBP).coerceAtLeast(0))
    }
    var selectedSizeIndex by remember(stickerPack, stickerIndex) { mutableIntStateOf(0) }
    var selectedFormatIndex by remember(stickerPack, stickerIndex) { mutableIntStateOf(0) }

    val typeOptions = remember(stickerPack) { stickerPack.types.map { it.displayName } }
    val sizeOptions = listOf("64px", "128px", "256px", "512px")
    val sizeValues = listOf(64, 128, 256, 512)
    val formatOptions = listOf("HTML", "Markdown", "URL")

    val currentUrl = remember(selectedTypeIndex, stickerPack, stickerIndex) {
        stickerPack.getUrl(stickerIndex, listOf(stickerPack.types[selectedTypeIndex]))
    }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(previewSize.dp), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                uri = currentUrl, contentDescription = "贴纸预览", modifier = Modifier.size((previewSize - 20).dp)
            )
        }

        Card {
            SuperDropdown(
                title = "图片格式",
                items = typeOptions,
                selectedIndex = selectedTypeIndex,
                onSelectedIndexChange = { selectedTypeIndex = it })
            SuperDropdown(
                title = "图片尺寸",
                items = sizeOptions,
                selectedIndex = selectedSizeIndex,
                onSelectedIndexChange = { selectedSizeIndex = it })
            SuperDropdown(
                title = "复制格式",
                items = formatOptions,
                selectedIndex = selectedFormatIndex,
                onSelectedIndexChange = { selectedFormatIndex = it })
        }

        Button(
            onClick = {
                val size = sizeValues[selectedSizeIndex]
                val text = when (selectedFormatIndex) {
                    0 -> """<img src="$currentUrl" width="$size" />"""
                    1 -> "![sticker]($currentUrl)"
                    else -> currentUrl.orEmpty()
                }
                onCopy(text)
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("复制")
        }
    }
}

@Composable
fun StickerDetailBottomSheet(
    show: MutableState<Boolean>, stickerPack: StickerPack, stickerIndex: Int, onDismissRequest: () -> Unit
) {
    SuperBottomSheet(
        show = show, title = "贴纸详情", onDismissRequest = onDismissRequest
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            StickerDetailContent(
                stickerPack = stickerPack, stickerIndex = stickerIndex, previewSize = 200, onCopy = { text ->
                    copyToClipboard(text)
                    onDismissRequest()
                })
        }
    }
}

@Composable
fun StickerDetailDialog(
    show: MutableState<Boolean>, stickerPack: StickerPack, stickerIndex: Int, onDismissRequest: () -> Unit
) {
    SuperDialog(
        show = show, title = "贴纸详情", onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.widthIn(min = 280.dp, max = 520.dp).padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                StickerDetailContent(
                    stickerPack = stickerPack, stickerIndex = stickerIndex, previewSize = 240, onCopy = { text ->
                        copyToClipboard(text)
                        onDismissRequest()
                    })
            }
        }
    }
}