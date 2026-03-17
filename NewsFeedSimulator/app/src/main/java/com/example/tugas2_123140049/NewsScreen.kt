package com.example.tugas2_123140049

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel) {

    val newsList by viewModel.newsList.collectAsState()
    val readCount by viewModel.readCount.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 News Feed Simulator") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            CounterCard(readCount)

            Spacer(modifier = Modifier.height(16.dp))

            CategoryChips(selectedCategory) { category ->
                viewModel.changeCategory(category)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(newsList) { news ->
                    NewsCard(news)
                }
            }
        }
    }
}

@Composable
fun CounterCard(count: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Total Dibaca: $count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(selected: String, onSelected: (String) -> Unit) {

    val categories = listOf("Tech", "Sports", "Finance")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->

            FilterChip(
                selected = category == selected,
                onClick = { onSelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun NewsCard(news: News) {

    val categoryColor = when (news.category) {
        "Tech" -> Color(0xFF2196F3)
        "Sports" -> Color(0xFF4CAF50)
        "Finance" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        categoryColor.copy(alpha = 0.15f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = news.category,
                    color = categoryColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
