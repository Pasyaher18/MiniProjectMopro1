package com.pasya0118.miniproject.screens

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pasya0118.miniproject.R
import com.pasya0118.miniproject.ui.theme.MiniProjectMobpro1PasyaTheme

@Composable
fun DisplayAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.pesan_hapus)) },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = stringResource(R.string.tombol_hapus))
            }
        },
        dismissButton = {
            TextButton(onClick = {onDismissRequest() }) {
                Text(text = stringResource(R.string.tombol_batal))
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DialogPreview() {
    MiniProjectMobpro1PasyaTheme {
        DisplayAlertDialog(
            onDismissRequest = {},
            onConfirmation = {}
        )
    }
}
