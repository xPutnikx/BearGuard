@file:OptIn(ExperimentalComposeUiApi::class)

package com.bearminds.views.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bearminds.theme.ShapeTokens
import com.bearminds.architecture.ComposableData
import com.bearminds.architecture.modifiers.bottomBorder

data class FullScreenBottomSheetData(
    private val windowInsets: WindowInsets? = null,
    private val title: String? = null,
    private val iconColor: Color? = null,
    private val showClose: Boolean = true,
    private val closeIcon: ImageVector? = null,
    private val onClose: () -> Unit,
    private val trailingAction: ComposableData? = null,
    private val leadingAction: ComposableData? = null,
    private val content: ComposableData,
) : ComposableData {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(modifier: Modifier) = FullScreenBottomSheet(
        modifier = modifier,
        windowInsets = windowInsets ?: BottomSheetDefaults.windowInsets,
        title = title,
        iconColor = iconColor ?: LocalContentColor.current,
        showClose = showClose,
        closeIcon = closeIcon,
        onClose = onClose,
        trailingAction = trailingAction,
        leadingAction = leadingAction,
        content = { content.Composable() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenBottomSheet(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
    title: String? = null,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    showClose: Boolean = true,
    closeIcon: ImageVector? = null,
    onClose: () -> Unit,
    trailingAction: ComposableData? = null,
    leadingAction: ComposableData? = null,
    content: @Composable () -> Unit,
) {

    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = ShapeTokens.large,
        onDismissRequest = { onClose.invoke() },
        contentWindowInsets = { windowInsets },
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (title != null || trailingAction != null || leadingAction != null) {
                Row(
                    modifier = Modifier
                        .bottomBorder(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
                        )
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 24.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    leadingAction?.Composable(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) ?: Spacer(modifier = Modifier.width(48.dp))

                    if (title != null) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = title,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    trailingAction?.Composable(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )

                    if (showClose && closeIcon != null) {
                        IconButton(onClick = onClose) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = closeIcon,
                                contentDescription = null,
                                tint = iconColor
                            )
                        }
                    }
                }
            }

            content()
        }
    }
}
