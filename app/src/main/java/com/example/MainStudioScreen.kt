package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudioScreen(viewModel: ThumbnailViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    
    var activeTab by remember { mutableStateOf("create") }
    var showPremiumUpsell by remember { mutableStateOf(false) }

    // Initialize Preferences
    LaunchedEffect(Unit) {
        viewModel.initPreferences(context)
    }

    val currentLang = viewModel.currentLanguage

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Translations.getString("app_title", currentLang),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = Translations.getString("app_subtitle", currentLang),
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                },
                actions = {
                    if (viewModel.isPremium) {
                        Surface(
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "PREMIUM ✨",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xD20D1B2A)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1B263B),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "create",
                    onClick = { activeTab = "create" },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                    label = { Text(Translations.getString("tab_create", currentLang), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE0E1DD),
                        selectedTextColor = Color(0xFFE0E1DD),
                        indicatorColor = Color(0xFF415A77),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_create_tab")
                )
                NavigationBarItem(
                    selected = activeTab == "inspiration",
                    onClick = { activeTab = "inspiration" },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(Translations.getString("tab_inspiration", currentLang), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE0E1DD),
                        selectedTextColor = Color(0xFFE0E1DD),
                        indicatorColor = Color(0xFF415A77),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_inspiration_tab")
                )
                NavigationBarItem(
                    selected = activeTab == "editor",
                    onClick = { activeTab = "editor" },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(Translations.getString("tab_editor", currentLang), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE0E1DD),
                        selectedTextColor = Color(0xFFE0E1DD),
                        indicatorColor = Color(0xFF415A77),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_editor_tab")
                )
                NavigationBarItem(
                    selected = activeTab == "history",
                    onClick = { activeTab = "history" },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(Translations.getString("tab_history", currentLang), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE0E1DD),
                        selectedTextColor = Color(0xFFE0E1DD),
                        indicatorColor = Color(0xFF415A77),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_history_tab")
                )
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = { activeTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(Translations.getString("tab_premium", currentLang), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE0E1DD),
                        selectedTextColor = Color(0xFFE0E1DD),
                        indicatorColor = Color(0xFF415A77),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_settings_tab")
                )
            }
        },
        containerColor = Color(0xFF0D1B2A)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "create" -> CreateTabContent(
                    viewModel = viewModel,
                    onStartGenerating = {
                        viewModel.generateAIThumbnail(context) {
                            showPremiumUpsell = true
                        }
                    },
                    onNavigateToEditor = { activeTab = "editor" }
                )
                "inspiration" -> InspirationGalleryTab(
                    viewModel = viewModel,
                    onNavigateToEditor = { activeTab = "editor" }
                )
                "editor" -> EditorTabContent(
                    viewModel = viewModel
                )
                "history" -> HistoryTabContent(
                    viewModel = viewModel,
                    onEditSelected = { activeTab = "editor" }
                )
                "settings" -> SettingsTabContent(
                    viewModel = viewModel
                )
            }

            // Global API Warning or Status Banner (at the top if error or fallback is active)
            viewModel.errorMessage?.let { errorMsg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .align(Alignment.TopCenter),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD90429)),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearErrorMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Absolute overlays: Generation Modal Spinner
            if (viewModel.isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xE20D1B2A))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFE0E1DD),
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = Translations.getString("generating", currentLang),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Gemini AI & Imagen models processing. Please do not close...",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Limited Style Copy Premium dialog popup
            if (showPremiumUpsell) {
                PremiumUpsellDialog(
                    viewModel = viewModel,
                    onDismiss = { showPremiumUpsell = false }
                )
            }
        }
    }
}

// ==========================================
// CREATE TAB PANEL
// ==========================================
@Composable
fun CreateTabContent(
    viewModel: ThumbnailViewModel,
    onStartGenerating: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = viewModel.currentLanguage
    val scrollState = rememberScrollState()

    // Activity launcher for choosing custom style reference image from device
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(selectedUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                
                if (originalBitmap != null) {
                    // Compress and scale down to keep memory/network footprint very safe (<= 800px width is perfect!)
                    val scaleFactor = 600f / Math.max(originalBitmap.width, originalBitmap.height).toFloat()
                    val targetBitmap = if (scaleFactor < 1f) {
                        Bitmap.createScaledBitmap(
                            originalBitmap,
                            (originalBitmap.width * scaleFactor).toInt(),
                            (originalBitmap.height * scaleFactor).toInt(),
                            true
                        )
                    } else {
                        originalBitmap
                    }
                    
                    val outputStream = ByteArrayOutputStream()
                    targetBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                    val bytes = outputStream.toByteArray()
                    val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
                    
                    // Set custom reference path in ViewModel
                    viewModel.setUserStyleReference(base64String, "ref_device_upload.jpg")
                    Toast.makeText(context, "Imagem de referência importada!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Falha ao ler imagem: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("create_tab_scroll")
    ) {
        // Platform Selection
        Text(
            text = Translations.getString("platform_title", currentLang),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlatformCard(
                name = Translations.getString("platform_youtube", currentLang),
                subtitle = "1280x720 (16:9)",
                isSelected = viewModel.selectedPlatform == "YouTube",
                icon = Icons.Default.PlayArrow,
                colorAccent = Color(0xFFFF0000),
                onClick = { viewModel.selectedPlatform = "YouTube" },
                modifier = Modifier
                    .weight(1f)
                    .testTag("platform_youtube_card")
            )
            PlatformCard(
                name = Translations.getString("platform_facebook", currentLang),
                subtitle = "1200x628 (~1.9:1)",
                isSelected = viewModel.selectedPlatform == "Facebook",
                icon = Icons.Default.Share,
                colorAccent = Color(0xFF1877F2),
                onClick = { viewModel.selectedPlatform = "Facebook" },
                modifier = Modifier
                    .weight(1f)
                    .testTag("platform_facebook_card")
            )
        }

        // Category selection
        Text(
            text = Translations.getString("category_title", currentLang),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf("Tech", "Gaming", "Lifestyle", "Business", "Vlog", "Motivational", "Editorial")
            categories.forEach { categoryKey ->
                val displayLabel = when (categoryKey) {
                    "Tech" -> Translations.getString("style_tech", currentLang)
                    "Gaming" -> Translations.getString("style_gaming", currentLang)
                    "Lifestyle" -> Translations.getString("style_lifestyle", currentLang)
                    "Business" -> Translations.getString("style_business", currentLang)
                    "Vlog" -> Translations.getString("style_vlog", currentLang)
                    "Motivational" -> Translations.getString("style_motivational", currentLang)
                    else -> Translations.getString("style_editorial", currentLang)
                }

                FilterChip(
                    selected = viewModel.selectedCategory == categoryKey,
                    onClick = { viewModel.selectedCategory = categoryKey },
                    label = { Text(displayLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF415A77),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1B263B),
                        labelColor = Color.Gray
                    )
                )
            }
        }

        // Text & Detail inputs
        Text(
            text = Translations.getString("details_title", currentLang),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = viewModel.videoTitle,
            onValueChange = { viewModel.videoTitle = it },
            label = { Text("Título da capa overlay") },
            placeholder = { Text(Translations.getString("title_placeholder", currentLang)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("input_video_title"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E1DD),
                unfocusedBorderColor = Color(0xFF415A77),
                focusedLabelColor = Color(0xFFE0E1DD),
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        OutlinedTextField(
            value = viewModel.videoDescription,
            onValueChange = { viewModel.videoDescription = it },
            label = { Text("Idéia ou descrição do vídeo (IA)") },
            placeholder = { Text(Translations.getString("desc_placeholder", currentLang)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(bottom = 16.dp)
                .testTag("input_video_description"),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E1DD),
                unfocusedBorderColor = Color(0xFF415A77),
                focusedLabelColor = Color(0xFFE0E1DD),
                unfocusedLabelColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Style Copy (Style reference replication) Configuration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            border = BorderStroke(1.dp, Color(0xFF415A77))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Translations.getString("style_reference_title", currentLang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Usage limitation status log
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (viewModel.isPremium) Color.Green else Color.Yellow, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (viewModel.isPremium) {
                            "Plano Premium: Cópia de estilo ILIMITADA"
                        } else {
                            Translations.getString("free_uses_left", currentLang, 2 - viewModel.styleCopyUsesToday)
                        },
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                // Reference style selection buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415A77)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Translations.getString("upload_custom", currentLang), fontSize = 10.sp)
                    }
                }

                // Show active reference feedback
                if (viewModel.styleReferenceBase64 != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Done, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Estilo de Referência carregado com sucesso!",
                                fontSize = 10.sp,
                                color = Color.Green,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Suggest static styles
                Text(
                    text = Translations.getString("select_reference", currentLang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf("preset_flat", "preset_neon", "preset_clean", "preset_warm")
                    presets.forEach { presetKey ->
                        val presetLabel = when (presetKey) {
                            "preset_neon" -> Translations.getString("preset_neon", currentLang).take(8) + ".."
                            "preset_clean" -> Translations.getString("preset_clean", currentLang).take(8) + ".."
                            "preset_warm" -> Translations.getString("preset_warm", currentLang).take(8) + ".."
                            else -> Translations.getString("preset_flat", currentLang).take(8) + ".."
                        }

                        val isSel = viewModel.selectedPresetStyle == presetKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF415A77) else Color(0xFF0D1B2A))
                                .border(1.dp, if (isSel) Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { viewModel.selectPresetStyle(presetKey) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = presetLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Generate Action button
        Button(
            onClick = {
                onStartGenerating()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("generate_thumbnail_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD700),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Translations.getString("generate_button", currentLang),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Quick helper bypass to editor if background exists
        if (viewModel.editorBackgroundImagePath != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onNavigateToEditor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "➜ Ir para o Estúdio de Edição",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlatformCard(
    name: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    colorAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (isSelected) colorAccent else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1B263B) else Color(0x761B263B)
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) colorAccent else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.LightGray,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// EDITOR / STUDIO TAB PANEL
// ==========================================
@Composable
fun EditorTabContent(viewModel: ThumbnailViewModel) {
    val context = LocalContext.current
    val currentLang = viewModel.currentLanguage
    val scrollState = rememberScrollState()

    var resizeWidthText by remember { mutableStateOf("") }
    var resizeHeightText by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.editorBackgroundImagePath) {
        viewModel.editorBackgroundImagePath?.let { path ->
            try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(path, options)
                if (options.outWidth > 0 && options.outHeight > 0) {
                    resizeWidthText = options.outWidth.toString()
                    resizeHeightText = options.outHeight.toString()
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            croppedUri?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val croppedBitmap = BitmapFactory.decodeStream(inputStream)
                    if (croppedBitmap != null) {
                        val file = File(context.filesDir, "cropped_bg_${System.currentTimeMillis()}.jpg")
                        val out = FileOutputStream(file)
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.close()
                        viewModel.editorBackgroundImagePath = file.absolutePath
                        Toast.makeText(context, "Imagem recortada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao carregar corte: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val error = result.error
            error?.let {
                Toast.makeText(context, "Erro ao cortar: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startCrop() {
        val path = viewModel.editorBackgroundImagePath
        if (path == null) {
            Toast.makeText(context, "Nenhuma imagem carregada para cortar!", Toast.LENGTH_SHORT).show()
            return
        }
        val fileUri = Uri.fromFile(File(path))
        val options = CropImageContractOptions(
            uri = fileUri,
            cropImageOptions = CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
            }
        )
        cropImageLauncher.launch(options)
    }

    fun applyResize(widthStr: String, heightStr: String) {
        val targetWidth = widthStr.toIntOrNull()
        val targetHeight = heightStr.toIntOrNull()
        if (targetWidth == null || targetWidth <= 0 || targetHeight == null || targetHeight <= 0) {
            Toast.makeText(context, "Dimensões inválidas!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentPath = viewModel.editorBackgroundImagePath
        if (currentPath == null) {
            Toast.makeText(context, "Nenhuma imagem carregada para redimensionar!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val originalBitmap = BitmapFactory.decodeFile(currentPath)
            if (originalBitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
                val file = File(context.filesDir, "resized_bg_${System.currentTimeMillis()}.jpg")
                val out = FileOutputStream(file)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.close()

                viewModel.editorBackgroundImagePath = file.absolutePath
                Toast.makeText(context, "Imagem redimensionada para ${targetWidth}x${targetHeight}!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Falha ao ler imagem.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao redimensionar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Activity launcher to upload a base background image manually if they want
    val backgroundUploader = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(fileUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val file = File(context.filesDir, "custom_bg_${System.currentTimeMillis()}.jpg")
                    val out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.close()
                    viewModel.editorBackgroundImagePath = file.absolutePath
                    Toast.makeText(context, "Imagem de fundo importada!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("editor_tab_scroll")
    ) {
        // Platform Aspect ratio Preview Screen Card
        val aspectPercentWidth = if (viewModel.selectedPlatform == "YouTube") 16f / 9f else 1200f / 628f

        Text(
            text = "Visualização da Capa (${viewModel.selectedPlatform})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Custom live-rendered preview canvas with color filter matrix bounds
        val matrix = remember(viewModel.filterBrightness, viewModel.filterContrast, viewModel.filterSaturation) {
            val cm = android.graphics.ColorMatrix()
            cm.setSaturation(viewModel.filterSaturation)
            
            val bValue = (viewModel.filterBrightness - 1.0f) * 255f
            val brightnessMatrix = floatArrayOf(
                1f, 0f, 0f, 0f, bValue,
                0f, 1f, 0f, 0f, bValue,
                0f, 0f, 1f, 0f, bValue,
                0f, 0f, 0f, 1f, 0f
            )
            val cmBrightness = android.graphics.ColorMatrix(brightnessMatrix)
            cm.postConcat(cmBrightness)
            
            val scale = viewModel.filterContrast
            val translate = 128f * (1.0f - scale)
            val contrastMatrix = floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
            val cmContrast = android.graphics.ColorMatrix(contrastMatrix)
            cm.postConcat(cmContrast)
            
            androidx.compose.ui.graphics.ColorMatrix(cm.array)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectPercentWidth)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F1E36))
                .border(2.dp, Color(0xFF415A77), RoundedCornerShape(12.dp))
                .testTag("thumbnail_live_canvas")
        ) {
            // Background Image
            if (viewModel.editorBackgroundImagePath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(viewModel.editorBackgroundImagePath!!))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(viewModel.filterBlur.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(matrix)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text(
                            text = Translations.getString("canvas_no_image", currentLang),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Real-time YouTube player Overlay Mock for realistic engagement audit testing!
            if (viewModel.selectedPlatform == "YouTube") {
                // Bottom right duration indicator
                Surface(
                    color = Color.Black.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "11:42",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                // Play indicator on hover center mockup
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Youtube Play Button",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(42.dp)
                )
            } else {
                // Facebook mock duration badge at top left
                Surface(
                    color = Color(0xFF1877F2).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(Color.White, RoundedCornerShape(3.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AO VIVO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Text Overlay containing dual outline + fill layer styling
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = viewModel.textOffsetX.dp, y = viewModel.textOffsetY.dp),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.editorTitleText.isNotBlank()) {
                    Box(contentAlignment = Alignment.Center) {
                        // 1. Text Outline layer (Thick black or custom stroke boundary)
                        Text(
                            text = viewModel.editorTitleText.uppercase(),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = viewModel.textOverlaySize.sp,
                            textAlign = TextAlign.Center,
                            style = LocalTextStyle.current.copy(
                                drawStyle = Stroke(
                                    width = (viewModel.textOverlaySize / 5).coerceIn(4f, 16f),
                                    join = StrokeJoin.Round
                                )
                            ),
                            color = try { Color(android.graphics.Color.parseColor(viewModel.textOutlineColor)) } catch (_: Exception) { Color.Black },
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        // 2. Text Fill layer
                        Text(
                            text = viewModel.editorTitleText.uppercase(),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = viewModel.textOverlaySize.sp,
                            textAlign = TextAlign.Center,
                            color = try { Color(android.graphics.Color.parseColor(viewModel.textOverlayColor)) } catch (_: Exception) { Color.Yellow },
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Title text modifier row
        OutlinedTextField(
            value = viewModel.editorTitleText,
            onValueChange = { viewModel.editorTitleText = it },
            label = { Text("Texto da Capa / Thumbnail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFFD700)
            )
        )

        // Text Overlay Customizer Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Translations.getString("edit_text_overlay", currentLang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Color selectors
                Text(Translations.getString("color_labels", currentLang), fontSize = 11.sp, color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = listOf("#FFFF00", "#FFFFFF", "#FF0000", "#00FF00", "#00FFFF", "#FF00FF")
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(
                                    width = if (viewModel.textOverlayColor == hex) 3.dp else 1.dp,
                                    color = if (viewModel.textOverlayColor == hex) Color.White else Color.Black,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.textOverlayColor = hex }
                        )
                    }
                }

                Text(Translations.getString("color_outline", currentLang), fontSize = 11.sp, color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val outlineColors = listOf("#000000", "#FFFFFF", "#111111", "#1B263B", "#333333")
                    outlineColors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(
                                    width = if (viewModel.textOutlineColor == hex) 3.dp else 1.dp,
                                    color = if (viewModel.textOutlineColor == hex) Color(0xFFFFD700) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.textOutlineColor = hex }
                        )
                    }
                }

                // Size Slider
                Text(
                    text = "${Translations.getString("size_label", currentLang)}: ${viewModel.textOverlaySize.toInt()} sp",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = viewModel.textOverlaySize,
                    onValueChange = { viewModel.textOverlaySize = it },
                    valueRange = 18f..80f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFD700),
                        activeTrackColor = Color(0xFFFFD700)
                    )
                )

                // Offsets Sliders
                Text(
                    text = "${Translations.getString("offset_y", currentLang)}: ${viewModel.textOffsetY.toInt()} dp",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Slider(
                    value = viewModel.textOffsetY,
                    onValueChange = { viewModel.textOffsetY = it },
                    valueRange = -180f..180f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.LightGray
                    )
                )

                Text(
                    text = "${Translations.getString("offset_x", currentLang)}: ${viewModel.textOffsetX.toInt()} dp",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Slider(
                    value = viewModel.textOffsetX,
                    onValueChange = { viewModel.textOffsetX = it },
                    valueRange = -180f..180f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.LightGray
                    )
                )
            }
        }

        // Color Filter slider controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Translations.getString("filters_title", currentLang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Brightness
                Text(text = "${Translations.getString("filter_brightness", currentLang)}: ${String.format("%.2f", viewModel.filterBrightness)}", fontSize = 11.sp, color = Color.LightGray)
                Slider(
                    value = viewModel.filterBrightness,
                    onValueChange = { viewModel.filterBrightness = it },
                    valueRange = 0.5f..1.5f,
                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFFE0E1DD))
                )

                // Contrast
                Text(text = "${Translations.getString("filter_contrast", currentLang)}: ${String.format("%.2f", viewModel.filterContrast)}", fontSize = 11.sp, color = Color.LightGray)
                Slider(
                    value = viewModel.filterContrast,
                    onValueChange = { viewModel.filterContrast = it },
                    valueRange = 0.5f..1.5f,
                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFFE0E1DD))
                )

                // Saturation
                Text(text = "${Translations.getString("filter_saturation", currentLang)}: ${String.format("%.2f", viewModel.filterSaturation)}", fontSize = 11.sp, color = Color.LightGray)
                Slider(
                    value = viewModel.filterSaturation,
                    onValueChange = { viewModel.filterSaturation = it },
                    valueRange = 0.0f..2.0f,
                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFFE0E1DD))
                )

                // Blur
                Text(text = "${Translations.getString("filter_blur", currentLang)}: ${viewModel.filterBlur.toInt()} dp", fontSize = 11.sp, color = Color.LightGray)
                Slider(
                    value = viewModel.filterBlur,
                    onValueChange = { viewModel.filterBlur = it },
                    valueRange = 0f..20f,
                    colors = SliderDefaults.colors(activeTrackColor = Color(0xFFE0E1DD))
                )
            }
        }

        // Toolset to crop, resize and adjust properties of the background image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ações & Edição Avançada da Imagem",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Upload/Change image
                    Button(
                        onClick = { backgroundUploader.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415A77)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Translations.getString("upload_base", currentLang), fontSize = 11.sp)
                    }

                    // Crop Background button
                    if (viewModel.editorBackgroundImagePath != null) {
                        Button(
                            onClick = { startCrop() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cortar Fundo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (viewModel.editorBackgroundImagePath != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Redimensionar Inputs
                    Text(
                        text = "Redimensionar Imagem (Atual: ${
                            try {
                                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                BitmapFactory.decodeFile(viewModel.editorBackgroundImagePath, opts)
                                "${opts.outWidth}x${opts.outHeight}"
                            } catch (_: Exception) { "Desconhecido" }
                        })",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = resizeWidthText,
                            onValueChange = { resizeWidthText = it },
                            label = { Text("Largura (px)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Text("x", color = Color.White)

                        OutlinedTextField(
                            value = resizeHeightText,
                            onValueChange = { resizeHeightText = it },
                            label = { Text("Altura (px)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700)
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Button(
                            onClick = { applyResize(resizeWidthText, resizeHeightText) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E1DD), contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aplicar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Standard presets for resizing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val resizePresets = listOf(
                            "YouTube" to (1280 to 720),
                            "Facebook" to (1200 to 628),
                            "Metade" to (-1 to -1), // dynamic half
                            "Dobro" to (-2 to -2) // dynamic double
                        )

                        resizePresets.forEach { (label, size) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF0D1B2A), RoundedCornerShape(6.dp))
                                    .clickable {
                                        if (size.first == -1) {
                                            val currentPath = viewModel.editorBackgroundImagePath
                                            if (currentPath != null) {
                                                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                                BitmapFactory.decodeFile(currentPath, opts)
                                                resizeWidthText = (opts.outWidth / 2).toString()
                                                resizeHeightText = (opts.outHeight / 2).toString()
                                            }
                                        } else if (size.first == -2) {
                                            val currentPath = viewModel.editorBackgroundImagePath
                                            if (currentPath != null) {
                                                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                                                BitmapFactory.decodeFile(currentPath, opts)
                                                resizeWidthText = (opts.outWidth * 2).toString()
                                                resizeHeightText = (opts.outHeight * 2).toString()
                                            }
                                        } else {
                                            resizeWidthText = size.first.toString()
                                            resizeHeightText = size.second.toString()
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Export Render image button
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                // Safeguard against missing background image
                if (viewModel.editorBackgroundImagePath == null) {
                    Toast.makeText(context, "Por favor, defina um fundo primeiro!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                scope.launch(Dispatchers.IO) {
                    try {
                        val baseBitmap = BitmapFactory.decodeFile(viewModel.editorBackgroundImagePath)
                        if (baseBitmap != null) {
                            // Scale up to platform crisp sizes (YouTube: 1280x720, Facebook: 1200x628)
                            val finalWidth = if (viewModel.selectedPlatform == "YouTube") 1280 else 1200
                            val finalHeight = if (viewModel.selectedPlatform == "YouTube") 720 else 628
                            
                            val outputBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(outputBitmap)

                            // Clean draw with applied filters
                            val paint = android.graphics.Paint()
                            paint.isAntiAlias = true
                            
                            // Adjust brightness, contrast, and saturation inside native Android canvas paint
                            val cm = android.graphics.ColorMatrix()
                            cm.setSaturation(viewModel.filterSaturation)
                            val bValue = (viewModel.filterBrightness - 1.0f) * 255f
                            val cmB = android.graphics.ColorMatrix(floatArrayOf(
                                1f, 0f, 0f, 0f, bValue,
                                0f, 1f, 0f, 0f, bValue,
                                0f, 0f, 1f, 0f, bValue,
                                0f, 0f, 0f, 1f, 0f
                            ))
                            cm.postConcat(cmB)
                            
                            val scale = viewModel.filterContrast
                            val translate = 128f * (1.0f - scale)
                            val cmC = android.graphics.ColorMatrix(floatArrayOf(
                                scale, 0f, 0f, 0f, translate,
                                0f, scale, 0f, 0f, translate,
                                0f, 0f, scale, 0f, translate,
                                0f, 0f, 0f, 1f, 0f
                            ))
                            cm.postConcat(cmC)
                            paint.colorFilter = android.graphics.ColorMatrixColorFilter(cm)

                            // Draw background resized
                            val srcRect = android.graphics.Rect(0, 0, baseBitmap.width, baseBitmap.height)
                            val dstRect = android.graphics.Rect(0, 0, finalWidth, finalHeight)
                            canvas.drawBitmap(baseBitmap, srcRect, dstRect, paint)

                            // Draw Custom Text overlays in high-res!
                            if (viewModel.editorTitleText.isNotBlank()) {
                                val textPaintFill = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    color = android.graphics.Color.parseColor(viewModel.textOverlayColor)
                                    textSize = viewModel.textOverlaySize * 2.5f // scale up text
                                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    style = android.graphics.Paint.Style.FILL
                                }

                                val textPaintOutline = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    color = android.graphics.Color.parseColor(viewModel.textOutlineColor)
                                    textSize = viewModel.textOverlaySize * 2.5f
                                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    style = android.graphics.Paint.Style.STROKE
                                    strokeWidth = (viewModel.textOverlaySize / 2f)
                                    strokeJoin = android.graphics.Paint.Join.ROUND
                                }

                                val xPos = (finalWidth / 2f) + (viewModel.textOffsetX * 2.5f)
                                val yPos = (finalHeight / 2f) + (viewModel.textOffsetY * 2.5f) - ((textPaintFill.descent() + textPaintFill.ascent()) / 2f)

                                canvas.drawText(viewModel.editorTitleText.uppercase(), xPos, yPos, textPaintOutline)
                                canvas.drawText(viewModel.editorTitleText.uppercase(), xPos, yPos, textPaintFill)
                            }

                            // Save to local Room history and write to downloads directory
                            viewModel.saveThumbnailToHistory(context, outputBitmap)
                            
                            // Save to shared environment download directory so user can easily grab it on their emulator!
                            try {
                                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                val publicFile = File(downloadDir, "Export_${viewModel.selectedPlatform}_${System.currentTimeMillis()}.jpg")
                                val fOut = FileOutputStream(publicFile)
                                outputBitmap.compress(Bitmap.CompressFormat.JPEG, 92, fOut)
                                fOut.close()
                                
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Salvo também nos downloads públicos!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (_: Exception) {}
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erro ao exportar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_and_export_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(Translations.getString("save_to_gallery", currentLang), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// HISTORY ARCHIVE PANEL
// ==========================================
@Composable
fun HistoryTabContent(
    viewModel: ThumbnailViewModel,
    onEditSelected: () -> Unit
) {
    val currentLang = viewModel.currentLanguage
    val historyItems by viewModel.historyList.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("history_tab_scroll")
    ) {
        Text(
            text = Translations.getString("history_title", currentLang),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Translations.getString("history_empty", currentLang),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("history_lazy_list"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historyItems.size) { index ->
                    val item = historyItems[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1B263B), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
                    ) {
                        Column {
                            // Header banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D1B2A))
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (item.platform == "YouTube") Icons.Default.PlayArrow else Icons.Default.Share,
                                        contentDescription = null,
                                        tint = if (item.platform == "YouTube") Color.Red else Color(0xFF1877F2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.platform,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                val formattedDate = remember(item.timestamp) {
                                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp))
                                }
                                Text(
                                    text = formattedDate,
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }

                            // Sub-card Preview representation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = File(item.imagePath),
                                    contentDescription = "Saved Thumbnail background",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Dynamic Actions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.loadHistoryToEditor(item)
                                        Toast.makeText(context, "Carregado no Estúdio!", Toast.LENGTH_SHORT).show()
                                        onEditSelected()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415A77)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(Translations.getString("load_to_editor", currentLang), fontSize = 10.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.deleteHistoryItem(context, item)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD90429)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SETTINGS & LANGUAGE PANEL
// ==========================================
@Composable
fun SettingsTabContent(viewModel: ThumbnailViewModel) {
    val currentLang = viewModel.currentLanguage
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_tab_scroll")
    ) {
        // Upper Theme Title
        Text(
            text = "Configurações Globais",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // VIP Premium Club Card Configuration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            border = BorderStroke(1.dp, Color(0xFFFFD700))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Translations.getString("premium_card_title", currentLang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = Translations.getString("premium_price", currentLang),
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Benefits listed elegantly
                Text(text = Translations.getString("premium_benefit1", currentLang), fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(bottom = 4.dp))
                Text(text = Translations.getString("premium_benefit2", currentLang), fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(bottom = 4.dp))
                Text(text = Translations.getString("premium_benefit3", currentLang), fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(bottom = 4.dp))
                Text(text = Translations.getString("premium_benefit4", currentLang), fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))

                Button(
                    onClick = {
                        viewModel.togglePremium(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isPremium) Color.Gray else Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (viewModel.isPremium) Translations.getString("unsubscribe", currentLang) else Translations.getString("subscribe_now", currentLang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Language Switches Section
        Text(
            text = Translations.getString("language_section", currentLang),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Language.values().forEach { lang ->
                    val isSelected = currentLang == lang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF415A77) else Color.Transparent)
                            .clickable { viewModel.setLanguage(context, lang) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = lang.flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = lang.displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFFFD700))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = Translations.getString("security_warning", currentLang),
            fontSize = 9.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==========================================
// ABSOLUTE PREMIUM LIMIT DIALOG POPUP
// ==========================================
@Composable
fun PremiumUpsellDialog(
    viewModel: ThumbnailViewModel,
    onDismiss: () -> Unit
) {
    val currentLang = viewModel.currentLanguage
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFFD700))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = Translations.getString("limit_reached_title", currentLang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Translations.getString("limit_reached_desc", currentLang),
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Premium action checkout
                Button(
                    onClick = {
                        viewModel.togglePremium(context)
                        onDismiss()
                        Toast.makeText(context, "Plano Creator Premium Ativado! Aproveite!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(Translations.getString("subscribe_now", currentLang), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(onClick = onDismiss) {
                    Text(Translations.getString("close", currentLang), color = Color.Gray)
                }
            }
        }
    }
}
