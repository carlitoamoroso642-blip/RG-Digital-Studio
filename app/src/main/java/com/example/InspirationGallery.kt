package com.example

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class InspirationItem(
    val id: String,
    val title: String,
    val category: String, // "Anime", "Personagens de Filme", "Estilo 3D", "Mangá"
    val imageUrl: String
)

object InspirationData {
    val items = listOf(
        // Anime
        InspirationItem(
            id = "anime_1",
            title = "Cyberpunk Samurai Girl",
            category = "Anime",
            imageUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "anime_2",
            title = "Tokyo Future Street",
            category = "Anime",
            imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "anime_3",
            title = "Retro Synthwave Sunset",
            category = "Anime",
            imageUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "anime_4",
            title = "Fantasy Starry Sky",
            category = "Anime",
            imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800&auto=format&fit=crop"
        ),

        // Personagens de Filme
        InspirationItem(
            id = "movie_1",
            title = "Neon Dark Cosplay",
            category = "Personagens de Filme",
            imageUrl = "https://images.unsplash.com/photo-1620336655055-088d06e36bf0?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "movie_2",
            title = "Cosmic Astronaut Explorer",
            category = "Personagens de Filme",
            imageUrl = "https://images.unsplash.com/photo-1454789548928-9efd52dc4031?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "movie_3",
            title = "Shadow Phantom Mask",
            category = "Personagens de Filme",
            imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "movie_4",
            title = "Quantum Cyber Suit",
            category = "Personagens de Filme",
            imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=800&auto=format&fit=crop"
        ),

        // Estilo 3D
        InspirationItem(
            id = "3d_1",
            title = "Abstract Floating Spheres",
            category = "Estilo 3D",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "3d_2",
            title = "Vibrant Liquid Glass",
            category = "Estilo 3D",
            imageUrl = "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "3d_3",
            title = "Chrome Gold Waves",
            category = "Estilo 3D",
            imageUrl = "https://images.unsplash.com/photo-1614850523459-c2f4c699c52e?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "3d_4",
            title = "Infinite Cyber Cubes",
            category = "Estilo 3D",
            imageUrl = "https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=800&auto=format&fit=crop"
        ),

        // Mangá
        InspirationItem(
            id = "manga_1",
            title = "Ink Brush Boy",
            category = "Mangá",
            imageUrl = "https://images.unsplash.com/photo-1613376023733-0a73315d9b06?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "manga_2",
            title = "Classic Manga Panels",
            category = "Mangá",
            imageUrl = "https://images.unsplash.com/photo-1560942485-b2a11cc13456?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "manga_3",
            title = "Ink & Cloud Monochrome",
            category = "Mangá",
            imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?q=80&w=800&auto=format&fit=crop"
        ),
        InspirationItem(
            id = "manga_4",
            title = "Dynamic Stylized Ink Girl",
            category = "Mangá",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?q=80&w=800&auto=format&fit=crop"
        )
    )
}

@Composable
fun InspirationGalleryTab(
    viewModel: ThumbnailViewModel,
    onNavigateToEditor: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = viewModel.currentLanguage
    val scope = rememberCoroutineScope()

    var selectedCat by remember { mutableStateOf("Anime") }
    val categories = listOf("Anime", "Personagens de Filme", "Estilo 3D", "Mangá")

    val filteredItems = remember(selectedCat) {
        InspirationData.items.filter { it.category == selectedCat }
    }

    var activeDownloadingId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(16.dp)
    ) {
        Text(
            text = Translations.getString("inspiration_title", currentLang),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp).testTag("inspiration_gallery_title")
        )

        Text(
            text = Translations.getString("inspiration_subtitle", currentLang),
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Category Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val localizedCatName = when (cat) {
                    "Anime" -> Translations.getString("category_anime", currentLang)
                    "Personagens de Filme" -> Translations.getString("category_movie", currentLang)
                    "Estilo 3D" -> Translations.getString("category_3d", currentLang)
                    "Mangá" -> Translations.getString("category_manga", currentLang)
                    else -> cat
                }

                val isSelected = cat == selectedCat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) Color(0xFFFFD700) else Color(0xFF1B263B),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedCat = cat }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = localizedCatName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }

        // Grid of images
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {
            items(filteredItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Title badge overlay at bottom of image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Actions Row inside Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isThisDownloading = activeDownloadingId == item.id

                            // Button 1: Use in Editor
                            Button(
                                onClick = {
                                    if (activeDownloadingId == null) {
                                        activeDownloadingId = item.id
                                        scope.launch {
                                            val bitmap = downloadUrlToBitmap(context, item.imageUrl)
                                            if (bitmap != null) {
                                                val localPath = saveBitmapToLocalStorage(context, bitmap)
                                                viewModel.editorBackgroundImagePath = localPath
                                                viewModel.editorTitleText = "TITULO DA CAPA"
                                                Toast.makeText(
                                                    context,
                                                    "Template carregado no Estúdio!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onNavigateToEditor()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Erro ao carregar modelo.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            activeDownloadingId = null
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                if (isThisDownloading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = Color.Black,
                                        strokeWidth = 1.5.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        Translations.getString("use_in_editor", currentLang),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Button 2: Download to Gallery
                            OutlinedButton(
                                onClick = {
                                    if (activeDownloadingId == null) {
                                        activeDownloadingId = item.id
                                        scope.launch {
                                            val bitmap = downloadUrlToBitmap(context, item.imageUrl)
                                            if (bitmap != null) {
                                                val saved = saveBitmapToDeviceGallery(context, bitmap)
                                                if (saved) {
                                                    Toast.makeText(
                                                        context,
                                                        Translations.getString("download_success", currentLang),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        Translations.getString("download_error", currentLang),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    Translations.getString("download_error", currentLang),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            activeDownloadingId = null
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color.Gray)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    Translations.getString("download_to_gallery", currentLang),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to fetch bitmap from Coil Cache / URL
suspend fun downloadUrlToBitmap(context: Context, url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Safe for decoding and file writing
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// Helper to save downloaded bitmap to local app files for direct editing backgrounds
fun saveBitmapToLocalStorage(context: Context, bitmap: Bitmap): String? {
    return try {
        val file = File(context.filesDir, "inspiration_bg_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Helper to save downloaded bitmap to device Public gallery
fun saveBitmapToDeviceGallery(context: Context, bitmap: Bitmap): Boolean {
    val filename = "ClickBoost_Inspire_${System.currentTimeMillis()}.jpg"
    var success = false
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            resolver.openOutputStream(imageUri).use { out ->
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    success = true
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return success
}
