package com.example.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainActivity
import com.example.ui.components.DisplaySection
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.MemoryToolbar
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.SettingsBottomSheet
import com.example.ui.components.StandardKeypad
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.DisplayResultColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (spokenText != null) {
                viewModel.onVoiceResult(spokenText)
            }
        }
        viewModel.onToggleVoiceListening(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(com.example.R.string.voice_listening))
            }
            viewModel.onToggleVoiceListening(true)
            voiceLauncher.launch(intent)
        } else {
            viewModel.setToastMessage(context.getString(com.example.R.string.permission_mic_denied))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp
            val isInPip = uiState.isInPipMode

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isInPip) 8.dp else 20.dp, vertical = if (isInPip) 4.dp else 12.dp)
            ) {
                // Display Section (Formulas & Results)
                DisplaySection(
                    uiState = uiState,
                    onToggleDegreeMode = { viewModel.onToggleDegreeMode() },
                    onToggleScientific = { viewModel.onToggleScientific() },
                    onOpenHistory = { viewModel.onShowHistory(true) },
                    onToggleVoice = {
                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    },
                    modifier = Modifier.weight(if (isInPip) 1f else 1.35f)
                )

                // Quick Actions Row (Integrated M3 bar)
                if (!isInPip) {
                    val accentColor = com.example.ui.theme.LocalAccentColor.current
                    val iconTint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { viewModel.onShowSettings(true) },
                                modifier = Modifier.testTag("btn_settings")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            IconButton(
                                onClick = { (context as? MainActivity)?.triggerPip() },
                                modifier = Modifier.testTag("btn_pip")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureInPicture,
                                    contentDescription = "Picture in Picture",
                                    tint = iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.onToggleScientific() },
                            modifier = Modifier.testTag("btn_toggle_scientific")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Scientific Keypad",
                                tint = if (uiState.isScientificExpanded) accentColor else iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                if (isWideScreen && !isInPip) {
                    // Landscape / Tablet layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2.5f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ScientificKeypad(
                            uiState = uiState,
                            onFunction = { viewModel.onFunction(it) },
                            onConstant = { viewModel.onConstant(it) },
                            onParenthesis = { viewModel.onParenthesis() },
                            onFactorial = { viewModel.onFactorial() },
                            onPower = { viewModel.onPower() },
                            onSquare = { viewModel.onSquare() },
                            onSqrt = { viewModel.onSqrt() },
                            onToggleInverse = { viewModel.onToggleInverse() },
                            onToggleHyp = { viewModel.onToggleHyp() },
                            modifier = Modifier.weight(1f).fillMaxSize()
                        )

                        StandardKeypad(
                            onDigit = { viewModel.onDigit(it) },
                            onOperator = { viewModel.onOperator(it) },
                            onDecimalPoint = { viewModel.onDecimalPoint() },
                            onClear = { viewModel.onClear() },
                            onBackspace = { viewModel.onBackspace() },
                            onToggleSign = { viewModel.onToggleSign() },
                            onPercentage = { viewModel.onPercentage() },
                            onEquals = { viewModel.onEquals() },
                            modifier = Modifier.weight(1.2f).fillMaxSize()
                        )
                    }
                } else {
                    // Portrait layout
                    if (!isInPip) {
                        AnimatedVisibility(
                            visible = uiState.isScientificExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                ScientificKeypad(
                                    uiState = uiState,
                                    onFunction = { viewModel.onFunction(it) },
                                    onConstant = { viewModel.onConstant(it) },
                                    onParenthesis = { viewModel.onParenthesis() },
                                    onFactorial = { viewModel.onFactorial() },
                                    onPower = { viewModel.onPower() },
                                    onSquare = { viewModel.onSquare() },
                                    onSqrt = { viewModel.onSqrt() },
                                    onToggleInverse = { viewModel.onToggleInverse() },
                                    onToggleHyp = { viewModel.onToggleHyp() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    StandardKeypad(
                        onDigit = { viewModel.onDigit(it) },
                        onOperator = { viewModel.onOperator(it) },
                        onDecimalPoint = { viewModel.onDecimalPoint() },
                        onClear = { viewModel.onClear() },
                        onBackspace = { viewModel.onBackspace() },
                        onToggleSign = { viewModel.onToggleSign() },
                        onPercentage = { viewModel.onPercentage() },
                        onEquals = { viewModel.onEquals() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (isInPip) 2.5f else 3f)
                    )
                }

                if (!isInPip) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // History Modal Bottom Sheet
        if (uiState.showHistory && !uiState.isInPipMode) {
            HistoryBottomSheet(
                historyList = uiState.historyList,
                searchQuery = uiState.historySearchQuery,
                onSearchQueryChange = { viewModel.onHistorySearchQueryChange(it) },
                onSelectHistory = { viewModel.onSelectHistory(it) },
                onDeleteItem = { viewModel.onDeleteHistoryItem(it) },
                onClearAll = { viewModel.onClearAllHistory() },
                onDismiss = { viewModel.onShowHistory(false) },
                onToast = { viewModel.setToastMessage(it) }
            )
        }

        // Settings Modal Bottom Sheet
        if (uiState.showSettings && !uiState.isInPipMode) {
            SettingsBottomSheet(
                selectedAccentIndex = uiState.accentColorIndex,
                accentColors = viewModel.availableAccentColors,
                backgroundTheme = uiState.backgroundTheme,
                isPowerSavingEnabled = uiState.isPowerSavingEnabled,
                onSelectColor = { viewModel.onSelectAccentColor(it) },
                onSelectBackgroundTheme = { viewModel.onSelectBackgroundTheme(it) },
                onTogglePowerSaving = { viewModel.onTogglePowerSavingEnabled(it) },
                onDismiss = { viewModel.onShowSettings(false) }
            )
        }

        if (!uiState.isInPipMode) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun TopHeaderBar(
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onTriggerPip: () -> Unit,
    onShare: () -> Unit,
    isScientificExpanded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.app_name),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DisplayResultColor
                )
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.scientific_precision_instant),
                    fontSize = 11.sp,
                    color = DisplayFormulaColor.copy(alpha = 0.7f)
                )
            }
        }

        Row {
            IconButton(
                onClick = onTriggerPip,
                modifier = Modifier.testTag("header_pip")
            ) {
                Icon(
                    imageVector = Icons.Default.PictureInPicture,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.floating_window),
                    tint = DisplayFormulaColor
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("header_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.settings),
                    tint = DisplayFormulaColor
                )
            }

            IconButton(
                onClick = onShare,
                modifier = Modifier.testTag("header_share")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.share_result),
                    tint = DisplayFormulaColor
                )
            }

            IconButton(
                onClick = onToggleScientific,
                modifier = Modifier.testTag("header_toggle_scientific")
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.scientific_operations),
                    tint = if (isScientificExpanded) NeonCyan else DisplayFormulaColor
                )
            }

            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.testTag("header_history")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.history_title),
                    tint = DisplayFormulaColor
                )
            }
        }
    }
}
