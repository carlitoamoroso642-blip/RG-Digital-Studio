package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Room Database, DAO and Repository
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = ThumbnailRepository(database.thumbnailHistoryDao())
    val factory = ViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[ThumbnailViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        MainStudioScreen(viewModel = viewModel)
      }
    }
  }
}
