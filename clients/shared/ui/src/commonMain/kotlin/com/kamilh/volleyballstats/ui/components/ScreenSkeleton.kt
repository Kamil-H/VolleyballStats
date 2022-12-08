package com.kamilh.volleyballstats.ui.components

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.kamilh.volleyballstats.presentation.features.ScreenState
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.Icon
import com.kamilh.volleyballstats.ui.extensions.isScrollingUp
import com.kamilh.volleyballstats.ui.extensions.toPainter
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun ScreenSkeleton(
    state: ScreenState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    fabPadding: Dp = 0.dp,
    onFabButtonClicked: () -> Unit = {},
    onActionButtonClicked: () -> Unit = {},
    onNavigationButtonClicked: () -> Unit = {},
    onMessageButtonClicked: () -> Unit = {},
    onMessageDismissed: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    AdjustBarsColor(lightStatusBar = state.topBarState.background.lightStatusBar())

    if (state.loadingState.showFullScreenLoading) {
        FullScreenLoadingView(text = state.loadingState.text)
    } else {
        ScreenSkeleton(
            state = state,
            modifier = modifier,
            listState = listState,
            fabPadding = fabPadding,
            onFabButtonClicked = onFabButtonClicked,
            onActionButtonClicked = onActionButtonClicked,
            onNavigationButtonClicked = onNavigationButtonClicked,
            onMessageButtonClicked = onMessageButtonClicked,
            onMessageDismissed = onMessageDismissed,
        ) { paddingValues ->
            ScreenContent(
                state = state,
                paddingValues = paddingValues,
                content = content,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenSkeleton(
    state: ScreenState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    fabPadding: Dp = 0.dp,
    onFabButtonClicked: () -> Unit = {},
    onActionButtonClicked: () -> Unit = {},
    onNavigationButtonClicked: () -> Unit = {},
    onMessageButtonClicked: () -> Unit = {},
    onMessageDismissed: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message.text,
                actionLabel = message.buttonText,
                withDismissAction = false,
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.Dismissed -> onMessageDismissed()
                SnackbarResult.ActionPerformed -> onMessageButtonClicked()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val topBarState = state.topBarState
            if (topBarState.showToolbar) {
                TopAppBar(
                    title = {
                        topBarState.title?.let { title ->
                            Text(text = title)
                        }
                    },
                    actions = {
                        IconButtonView(
                            icon = topBarState.actionButtonIcon,
                            onClicked = onActionButtonClicked,
                        )
                    },
                    navigationIcon = {
                        IconButtonView(
                            icon = topBarState.navigationButtonIcon,
                            onClicked = onNavigationButtonClicked,
                        )
                    },
                    colors = if (topBarState.background == TopBarState.Color.Default) {
                        TopAppBarDefaults.smallTopAppBarColors()
                    } else {
                        with(topBarState.background) {
                            TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = containerColor,
                                scrolledContainerColor = containerColor,
                                navigationIconContentColor = onContainerColor,
                                titleContentColor = containerColor,
                                actionIconContentColor = onContainerColor,
                            )
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = fabPadding),
                onClick = { onFabButtonClicked() },
                visible = listState.isScrollingUp() && state.actionButton.show,
            ) {
                IconView(state.actionButton.icon)
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
private fun ScreenContent(
    state: ScreenState,
    paddingValues: PaddingValues,
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Column(
        modifier = Modifier.padding(
            top = if (state.topBarState.showToolbar) paddingValues.calculateTopPadding() else 0.dp,
            start = paddingValues.calculateLeftPadding(layoutDirection),
            end = paddingValues.calculateRightPadding(layoutDirection),
        )
    ) {
        if (!state.topBarState.showToolbar) {
            Spacer(
                modifier = Modifier.fillMaxWidth()
                    .height(paddingValues.calculateTopPadding())
                    .background(color = state.topBarState.background.containerColor),
            )
        }
        Box {
            content()
            if (state.loadingState.showSmallLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    trackColor = if (state.topBarState.background == TopBarState.Color.Default) {
                        ProgressIndicatorDefaults.linearTrackColor
                    } else {
                        state.topBarState.background.linearTrackColor
                    },
                    color = if (state.topBarState.background == TopBarState.Color.Default) {
                        ProgressIndicatorDefaults.linearColor
                    } else {
                        state.topBarState.background.linearColor
                    },
                )
            }
        }
    }
}

@Composable
private fun AdjustBarsColor(lightStatusBar: Boolean) {
    val darkTheme: Boolean = isSystemInDarkTheme()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window

        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val windowsInsetsController = WindowCompat.getInsetsController(window, view)

        windowsInsetsController.isAppearanceLightStatusBars = lightStatusBar
        windowsInsetsController.isAppearanceLightNavigationBars = !darkTheme
    }
}

@Composable
private fun TopBarState.Color.lightStatusBar(): Boolean {
    val darkMode = isSystemInDarkTheme()
    return when (this) {
        TopBarState.Color.Primary, TopBarState.Color.Secondary -> true
        TopBarState.Color.Default -> !darkMode
    }
}

private val TopBarState.Color.containerColor: Color
    @Composable
    get() = when (this) {
        TopBarState.Color.Primary -> MaterialTheme.colorScheme.primary
        TopBarState.Color.Secondary -> MaterialTheme.colorScheme.secondary
        TopBarState.Color.Default -> primaryColorError()
    }

private val TopBarState.Color.onContainerColor: Color
    @Composable
    get() = when (this) {
        TopBarState.Color.Primary -> MaterialTheme.colorScheme.onPrimary
        TopBarState.Color.Secondary -> MaterialTheme.colorScheme.onSecondary
        TopBarState.Color.Default -> primaryColorError()
    }

private fun primaryColorError(): Nothing = error("Primary should be handled separately")

private val TopBarState.Color.linearColor: Color
    @Composable
    get() = when (this) {
        TopBarState.Color.Primary -> MaterialTheme.colorScheme.primaryContainer
        TopBarState.Color.Secondary -> MaterialTheme.colorScheme.onSecondary
        TopBarState.Color.Default -> primaryColorError()
    }

private val TopBarState.Color.linearTrackColor: Color
    @Composable
    get() = when (this) {
        TopBarState.Color.Primary -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        TopBarState.Color.Secondary -> MaterialTheme.colorScheme.primary
        TopBarState.Color.Default -> primaryColorError()
    }

@Composable
private fun IconButtonView(
    icon: Icon?,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    icon?.let {
        IconButton(
            modifier = modifier,
            onClick = { onClicked() },
        ) {
            Icon(painter = icon.toPainter(), contentDescription = null)
        }
    }
}

@Composable
private fun IconView(
    icon: Icon?,
    modifier: Modifier = Modifier,
) {
    icon?.let {
        Icon(
            modifier = modifier,
            painter = icon.toPainter(),
            contentDescription = null,
        )
    }
}

@Composable
private fun FullScreenLoadingView(
    text: String?,
    modifier: Modifier = Modifier,
) {
    if (text != null) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(Dimens.MarginExtraLarge)
        ) {
            PulsingDots()
            Spacer(modifier = Modifier.height(Dimens.MarginMedium))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
