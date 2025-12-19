package screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import data.StickerPack
import data.StickerType
import data.allStickerPacks
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.other.GitHub

@Composable
fun HomeScreen(
    onDetailItemClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "YumeFuwa",
                navigationIcon = {
                    val uriHandler = LocalUriHandler.current
                    Row(
                        modifier = Modifier.padding(start = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/YumeYuka/YumeFuwa")
                            }
                        ) {
                            Icon(
                                MiuixIcons.Other.GitHub,
                                contentDescription = "GitHub"
                            )
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding() + 20.dp, start = 8.dp, end = 8.dp)
            ) {
                items(
                    count = allStickerPacks.size,
                    key = { index -> allStickerPacks[index].name }
                ) { index ->
                    val pack = allStickerPacks[index]
                    Card(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        SuperArrow(
                            title = pack.name,
                            summary = "共 ${pack.totalCount} 个贴纸 • ${pack.typesDisplay}",
                            leftAction = {
                                StickerPreview(pack = pack)
                            },
                            onClick = { onDetailItemClick(index) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun StickerPreview(pack: StickerPack) {
    val firstStickerUrl = remember(pack) {
        pack.getUrl(0, listOf(StickerType.PNG, StickerType.WEBP, StickerType.GIF))
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        firstStickerUrl?.let { url ->
            AsyncImage(
                uri = url,
                contentDescription = "${pack.name} 预览",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}