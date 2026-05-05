package com.example.tugas1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import tugas1.composeapp.generated.resources.Res
import tugas1.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        val platformName = remember { getPlatform().name }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painterResource(Res.drawable.compose_multiplatform), null)
            Text(
                text = "Halo, Nahli Saud Ramdani!",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "123140049",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Platform: $platformName",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}