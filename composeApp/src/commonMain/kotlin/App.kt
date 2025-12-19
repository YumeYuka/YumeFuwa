import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage
import data.StickerPack
import data.StickerType
import data.allStickerPacks
import kotlinx.browser.window
import screen.DetailScreen
import screen.HomeScreen
import screen.StickerDetailBottomSheet
import screen.StickerDetailDialog
import theme.AppTheme
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.other.GitHub
import top.yukonga.miuix.kmp.utils.Platform
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.platform

@Composable
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    AppTheme(
        isDarkTheme = isDarkTheme
    ) {
        if (platform() != Platform.IOS && platform() != Platform.Android) {
            DisposableEffect(Unit) {
                val onError = { e: Any ->
                    val err = e.asDynamic()
                    val msg = err?.message?.toString() ?: ""
                    if (msg.contains("BodyStreamBuffer") && msg.contains("aborted")) {
                        err.preventDefault()
                    }
                }
                val onRejection = { e: Any ->
                    val rej = e.asDynamic()
                    val reason = rej?.reason?.toString() ?: ""
                    if (reason.contains("BodyStreamBuffer") && reason.contains("aborted")) {
                        rej.preventDefault()
                    }
                }
                window.addEventListener("error", onError)
                window.addEventListener("unhandledrejection", onRejection)
                onDispose {
                    window.removeEventListener("error", onError)
                    window.removeEventListener("unhandledrejection", onRejection)
                }
            }
        }
        var selectedDetailItem by remember { mutableStateOf<Int?>(null) }

        val showDialog = remember { mutableStateOf(false) }
        var selectedStickerPack by remember { mutableStateOf<StickerPack?>(null) }
        var selectedStickerIndex by remember { mutableStateOf(0) }

        val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
        val focusManager = LocalFocusManager.current
        val showMenuPopup = remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(), contentWindowInsets = WindowInsets(0)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth >= 768.dp

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isWideScreen) {
                        LandscapeAppView(
                            scrollBehavior = scrollBehavior,
                            focusManager = focusManager,
                            showMenuPopup = showMenuPopup,
                            selectedDetailItem = selectedDetailItem,
                            onDetailItemClick = { selectedDetailItem = it },
                            onStickerClick = { pack, index ->
                                selectedStickerPack = pack
                                selectedStickerIndex = index
                                showDialog.value = true
                            })
                    } else {
                        PortraitAppView(
                            scrollBehavior = scrollBehavior,
                            focusManager = focusManager,
                            showMenuPopup = showMenuPopup,
                            selectedDetailItem = selectedDetailItem,
                            onDetailItemClick = { selectedDetailItem = it },
                            onStickerClick = { pack, index ->
                                selectedStickerPack = pack
                                selectedStickerIndex = index
                                showDialog.value = true
                            })
                    }

                    selectedStickerPack?.let { pack ->
                        if (isWideScreen) {
                            StickerDetailDialog(
                                show = showDialog,
                                stickerPack = pack,
                                stickerIndex = selectedStickerIndex,
                                onDismissRequest = {
                                    showDialog.value = false
                                    selectedStickerPack = null
                                })
                        } else {
                            StickerDetailBottomSheet(
                                show = showDialog,
                                stickerPack = pack,
                                stickerIndex = selectedStickerIndex,
                                onDismissRequest = {
                                    showDialog.value = false
                                    selectedStickerPack = null
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuActions(
    showMenuPopup: MutableState<Boolean>, focusManager: FocusManager
) {
    IconButton(
        modifier = Modifier.padding(end = if (platform() != Platform.IOS && platform() != Platform.Android) 10.dp else 20.dp)
            .size(40.dp), onClick = {
            showMenuPopup.value = !showMenuPopup.value
            focusManager.clearFocus()
        }, holdDownState = showMenuPopup.value
    ) {
        Icon(
            imageVector = MiuixIcons.Other.GitHub,
            tint = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onBackground,
            contentDescription = "Menu"
        )
    }
}

@Composable
private fun PortraitAppView(
    scrollBehavior: ScrollBehavior,
    focusManager: FocusManager,
    showMenuPopup: MutableState<Boolean>,
    selectedDetailItem: Int?,
    onDetailItemClick: (Int?) -> Unit,
    onStickerClick: (StickerPack, Int) -> Unit
) {
    if (selectedDetailItem != null) {
        DetailScreen(
            itemIndex = selectedDetailItem,
            isWideScreen = false,
            onBack = { onDetailItemClick(null) },
            onStickerClick = onStickerClick
        )
    } else {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                color = Color.Transparent, title = "YumeFuwa", scrollBehavior = scrollBehavior, actions = {
                    MenuActions(
                        showMenuPopup = showMenuPopup, focusManager = focusManager
                    )
                })
        }, popupHost = {}) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues,
                overscrollEffect = null
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(
                    count = allStickerPacks.size, key = { index -> allStickerPacks[index].name }) { index ->
                    val pack = allStickerPacks[index]
                    Card(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        SuperArrow(
                            title = pack.name,
                            summary = "共 ${pack.totalCount} 个贴纸 • ${pack.typesDisplay}",
                            leftAction = {
                                StickerPreview(pack = pack)
                            },
                            onClick = { onDetailItemClick(index) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LandscapeAppView(
    scrollBehavior: ScrollBehavior,
    focusManager: FocusManager,
    showMenuPopup: MutableState<Boolean>,
    selectedDetailItem: Int?,
    onDetailItemClick: (Int) -> Unit,
    onStickerClick: (StickerPack, Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    var leftPaneFraction by remember { mutableFloatStateOf(0.42f) }

    Scaffold(
        modifier = Modifier.fillMaxSize(), popupHost = {}) { scaffoldPaddingValues ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val containerWidthPx = with(density) { maxWidth.toPx().coerceAtLeast(1f) }
            val dragState = rememberDraggableState { deltaPx ->
                val signedDelta =
                    if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) -deltaPx else deltaPx
                val next = leftPaneFraction + signedDelta / containerWidthPx
                leftPaneFraction = next.coerceIn(0.25f, 0.65f)
            }

            Row(
                modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Start))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Start))
            ) {
                Column(
                    modifier = Modifier.weight(leftPaneFraction).fillMaxHeight()
                ) {
                    SmallTopAppBar(
                        title = "YumeFuwa",
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            Row(
                                modifier = Modifier.padding(start = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(onClick = {
                                    uriHandler.openUri("https://github.com/YumeYuka/YumeFuwa")
                                }) {
                                    Icon(
                                        MiuixIcons.Other.GitHub, contentDescription = "GitHub"
                                    )
                                }
                            }
                        },
                        defaultWindowInsetsPadding = false,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Start))
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Start))
                    )

                    HorizontalDivider(thickness = 1.dp)

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxHeight().overScrollVertical()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        overscrollEffect = null
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(
                            count = allStickerPacks.size, key = { index -> allStickerPacks[index].name }) { index ->
                            val pack = allStickerPacks[index]
                            Card(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                SuperArrow(
                                    title = pack.name,
                                    summary = "共 ${pack.totalCount} 个贴纸 • ${pack.typesDisplay}",
                                    leftAction = {
                                        StickerPreview(pack = pack)
                                    },
                                    onClick = { onDetailItemClick(index) })
                            }
                        }
                        item {
                            Spacer(
                                Modifier.height(
                                    WindowInsets.navigationBars.asPaddingValues()
                                        .calculateBottomPadding() + WindowInsets.captionBar.asPaddingValues()
                                        .calculateBottomPadding()
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxHeight().width(12.dp)
                        .draggable(state = dragState, orientation = Orientation.Horizontal)
                        .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                    contentAlignment = Alignment.Center
                ) {
                    VerticalDivider(
                        thickness = 1.dp, modifier = Modifier.fillMaxHeight()
                    )
                }

                Box(
                    modifier = Modifier.fillMaxHeight().weight(1f - leftPaneFraction)
                ) {
                    if (selectedDetailItem == null) {
                        Text(
                            text = "请选择一个贴纸包查看详情",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        DetailScreen(
                            itemIndex = selectedDetailItem, isWideScreen = true, onStickerClick = onStickerClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerPreview(pack: StickerPack) {
    val firstStickerUrl = remember(pack) {
        pack.getUrl(0, listOf(StickerType.PNG, StickerType.WEBP, StickerType.GIF))
    }

    Box(
        modifier = Modifier.size(48.dp).padding(end = 16.dp), contentAlignment = Alignment.Center
    ) {
        firstStickerUrl?.let { url ->
            AsyncImage(
                uri = url, contentDescription = "${pack.name} 预览", modifier = Modifier.size(40.dp)
            )
        }
    }
}