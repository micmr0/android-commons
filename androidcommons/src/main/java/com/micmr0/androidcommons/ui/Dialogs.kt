package com.micmr0.androidcommons.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.micmr0.androidcommons.R
import java.util.Calendar
import java.util.Locale

@Composable
fun InfoDialog(
    @StringRes applicationName : Int,
    @DrawableRes applicationIcon : Int,
    @StringRes applicationDescription : Int,
    onAppInfoDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onAppInfoDialogDismiss,
        title = {
            Text(text = stringResource(R.string.app_info_title))
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(applicationIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(applicationName),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                val context = LocalContext.current
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val versionName = packageInfo.versionName

                Text(
                    text = versionName.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%d",
                        Calendar.getInstance().get(Calendar.YEAR)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(applicationDescription),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onAppInfoDialogDismiss
            ) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}

@Composable
fun LanguagePickerDialog(
    currentLanguage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "pl", // Polish
        "en", // English
        "de", // German
        "fr", // French
        "es", // Spanish
        "pt", // Portuguese
        "cs", // Czech
        "uk", // Ukrainian
        "ar", // Arabic
        "hi", // Hindi
        "bn", // Bengali
        "zh", // Chinese
        "ja"  // Japanese
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                languages.forEach { lang ->
                    val label = when (lang) {
                        "pl" -> stringResource(R.string.polish_lang)
                        "en" -> stringResource(R.string.english_lang)
                        "de" -> stringResource(R.string.deutsch_lang)
                        "fr" -> stringResource(R.string.french_lang)
                        "es" -> stringResource(R.string.espanol_lang)
                        "pt" -> stringResource(R.string.portugues_lang)
                        "cs" -> stringResource(R.string.czech_lang)
                        "uk" -> stringResource(R.string.ukraine_lang)
                        "ar" -> stringResource(R.string.arabic_lang)
                        "hi" -> stringResource(R.string.hindu_lang)
                        "bn" -> stringResource(R.string.bengali_lang)
                        "zh" -> stringResource(R.string.chinese_lang)
                        "ja" -> stringResource(R.string.japanese_lang)
                        else -> lang
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang) }
                            .padding(4.dp)
                    ) {
                        RadioButton(
                            selected = currentLanguage == lang,
                            onClick = { onSelect(lang) }
                        )
                        Text(
                            text = label,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}


@Composable
fun RateAppDialog(
    onDismiss: () -> Unit,
    onRemindLater: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.rate_app_dialog_title))
        },
        text = {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.rate_app_dialog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onRemindLater
                ) {
                    Text(stringResource(R.string.remind_later))
                }

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.no))
                }
            }
        }
    )
}