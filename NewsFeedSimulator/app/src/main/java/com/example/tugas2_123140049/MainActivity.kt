package com.example.tugas2_123140049

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugas2_123140049.ui.theme.Tugas2_123140049Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Tugas2_123140049Theme {
                val viewModel: NewsViewModel = viewModel()
                NewsScreen(viewModel)
            }
        }
    }
}
