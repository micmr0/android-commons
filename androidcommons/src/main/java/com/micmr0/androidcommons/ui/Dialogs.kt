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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalLocale

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
                        LocalLocale.current.platformLocale,
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
fun languageDisplayName(language: String): String =
    when (language.lowercase()) {
        "pl" -> stringResource(R.string.polish_lang)
        "en" -> stringResource(R.string.english_lang)
        "de" -> stringResource(R.string.deutsch_lang)
        "fr" -> stringResource(R.string.french_lang)
        "es" -> stringResource(R.string.espanol_lang)
        "es-ar" -> stringResource(R.string.espanol_argentina_lang)
        "pt" -> stringResource(R.string.portugues_lang)
        "pt-br" -> stringResource(R.string.portugues_brasil_lang)
        "it" -> stringResource(R.string.italian_lang)
        "cs" -> stringResource(R.string.czech_lang)
        "uk" -> stringResource(R.string.ukraine_lang)
        "ar" -> stringResource(R.string.arabic_lang)
        "hi" -> stringResource(R.string.hindu_lang)
        "bn" -> stringResource(R.string.bengali_lang)
        "zh", "zh-cn" -> stringResource(R.string.chinese_lang)
        "ja" -> stringResource(R.string.japanese_lang)
        "ko" -> stringResource(R.string.korean_lang)
        "tr" -> stringResource(R.string.turkish_lang)
        else -> language.uppercase()
    }

@Composable
fun LanguagePickerDialog(
    currentLanguage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "pl",
        "en",
        "de",
        "fr",
        "es",
        "es-AR",
        "pt",
        "pt-BR",
        "it",
        "cs",
        "uk",
        "ar",
        "hi",
        "bn",
        "zh",
        "ja",
        "ko",
        "tr"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            LazyColumn {
                items(languages) { lang ->
                    val label = languageDisplayName(lang)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang) }
                            .padding(4.dp)
                    ) {
                        RadioButton(
                            selected = currentLanguage.equals(lang, ignoreCase = true),
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