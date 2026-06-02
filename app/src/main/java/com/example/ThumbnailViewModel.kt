package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.util.Base64
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ThumbnailViewModel(private val repository: ThumbnailRepository) : ViewModel() {

    // --- Localization State ---
    var currentLanguage by mutableStateOf(Language.PT)
        private set

    // --- Premium & Usage States ---
    var isPremium by mutableStateOf(false)
        private set

    var styleCopyUsesToday by mutableStateOf(0)
        private set

    // --- Loading & Error States ---
    var isGenerating by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- Create Tab Active State ---
    var selectedPlatform by mutableStateOf("YouTube") // "YouTube" or "Facebook"
    var selectedCategory by mutableStateOf("Tech") // "Tech", "Gaming", "Lifestyle", "Business", "Vlog", "Motivational", "Editorial"
    var videoTitle by mutableStateOf("")
    var videoDescription by mutableStateOf("")
    var styleReferenceBase64 by mutableStateOf<String?>(null) // holds custom uploaded reference string
    var styleReferenceName by mutableStateOf<String?>(null)
    var selectedPresetStyle by mutableStateOf<String?>("preset_flat") // default preset style

    // AI suggestions returned by gemini-3.5-flash
    var aiSuggestions = mutableStateOf<List<String>>(emptyList())

    // --- Editor Tab Active State ---
    var editorBackgroundImagePath by mutableStateOf<String?>(null)
    var editorTitleText by mutableStateOf("TITULO DO VÍDEO")
    var textOverlaySize by mutableStateOf(42f)
    var textOverlayColor by mutableStateOf("#FFFF00") // yellow
    var textOutlineColor by mutableStateOf("#000000") // black
    var textOffsetY by mutableStateOf(0f)
    var textOffsetX by mutableStateOf(0f)
    var filterBrightness by mutableStateOf(1.0f)
    var filterContrast by mutableStateOf(1.0f)
    var filterSaturation by mutableStateOf(1.0f)
    var filterBlur by mutableStateOf(0f)

    // --- History Database List ---
    private val _historyList = MutableStateFlow<List<ThumbnailHistory>>(emptyList())
    val historyList: StateFlow<List<ThumbnailHistory>> = _historyList.asStateFlow()

    // --- Initialization & Local Settings Loading ---
    fun initPreferences(context: Context) {
        val prefs = context.getSharedPreferences("ai_thumbnail_studio_prefs", Context.MODE_PRIVATE)
        
        // Load language preference
        val langCode = prefs.getString("selected_lang", Language.PT.code) ?: Language.PT.code
        currentLanguage = Language.values().firstOrNull { it.code == langCode } ?: Language.PT

        // Load premium status
        isPremium = prefs.getBoolean("is_premium_active", false)

        // Reset limits based on date change
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val savedDate = prefs.getString("limit_date", todayStr) ?: todayStr
        if (savedDate != todayStr) {
            prefs.edit()
                .putString("limit_date", todayStr)
                .putInt("uses_count", 0)
                .apply()
            styleCopyUsesToday = 0
        } else {
            styleCopyUsesToday = prefs.getInt("uses_count", 0)
        }

        // Fetch Saved History from Room
        viewModelScope.launch {
            repository.allHistory.collect { list ->
                _historyList.value = list
            }
        }
    }

    fun setLanguage(context: Context, language: Language) {
        currentLanguage = language
        val prefs = context.getSharedPreferences("ai_thumbnail_studio_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_lang", language.code).apply()
    }

    fun togglePremium(context: Context) {
        isPremium = !isPremium
        val prefs = context.getSharedPreferences("ai_thumbnail_studio_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_premium_active", isPremium).apply()
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    // --- Action: Increment usage log ---
    private fun incrementUsage(context: Context): Boolean {
        if (isPremium) return true
        if (styleCopyUsesToday >= 2) {
            return false
        }
        styleCopyUsesToday++
        val prefs = context.getSharedPreferences("ai_thumbnail_studio_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("uses_count", styleCopyUsesToday).apply()
        return true
    }

    // --- Action: Select custom image as style reference ---
    fun setUserStyleReference(base64: String, fileName: String) {
        styleReferenceBase64 = base64
        styleReferenceName = fileName
        selectedPresetStyle = null
    }

    fun selectPresetStyle(presetKey: String) {
        selectedPresetStyle = presetKey
        styleReferenceBase64 = null
        styleReferenceName = null
    }

    // --- Loading saved thumbnail back into Studio Editor ---
    fun loadHistoryToEditor(history: ThumbnailHistory) {
        editorBackgroundImagePath = history.imagePath
        editorTitleText = history.title
        textOverlaySize = history.textOverlaySize
        textOverlayColor = history.textOverlayColor
        textOutlineColor = history.textOutlineColor
        textOffsetY = history.textOffsetY
        textOffsetX = history.textOffsetX
        filterBrightness = history.filterBrightness
        filterContrast = history.filterContrast
        filterSaturation = history.filterSaturation
        filterBlur = history.filterBlur
    }

    fun deleteHistoryItem(context: Context, history: ThumbnailHistory) {
        viewModelScope.launch {
            // Delete local file to free up cache space
            try {
                val file = File(history.imagePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) {}
            
            repository.deleteById(history.id)
        }
    }

    // --- Action: Generate Thumbnail with AI ---
    fun generateAIThumbnail(context: Context, onShowLimitDialog: () -> Unit) {
        // Validation check for style reference limits
        val isStyleReferenceActive = (styleReferenceBase64 != null) || (selectedPresetStyle != null && selectedPresetStyle != "preset_flat")
        if (isStyleReferenceActive && !isPremium && styleCopyUsesToday >= 2) {
            onShowLimitDialog()
            return
        }

        isGenerating = true
        errorMessage = null

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

            if (isStyleReferenceActive) {
                // Style copy is chosen, consume 1 usage check
                withContext(Dispatchers.Main) {
                    incrementUsage(context)
                }
            }

            if (!hasApiKey) {
                // NO API KEY: Execute procedural fallback designer and load immediately
                simulateOfflineFallback(context)
                return@launch
            }

            try {
                // 1. Analyze and suggest optimization ideas via gemini-3.5-flash
                val promptInstruction = Translations.getSystemPromptInstruction(currentLanguage)
                val analysisPrompt = """
                    You are a visual design expert. The creator is preparing a video with:
                    Platform: $selectedPlatform
                    Category: $selectedCategory
                    Title: $videoTitle
                    Description: $videoDescription
                    
                    Provide exactly 3 high-impact thumbnail title overlay suggestions (compelling & short!) and 3 major color palette recommendations that maximize click-through rate (CTR). Keep it concise.
                    $promptInstruction
                """.trimIndent()

                val analysisRequest = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = analysisPrompt))))
                )

                val analysisResponse = RetrofitClient.service.generateProposal(apiKey, analysisRequest)
                val suggestionText = analysisResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Visual suggested styling ideas loaded."

                withContext(Dispatchers.Main) {
                    aiSuggestions.value = suggestionText.lineSequence().filter { it.isNotBlank() }.take(6).toList()
                }

                // 2. Generate descriptive illustration prompt based on Style Copy
                var backgroundPrompt = "A professional graphic design layout optimized for a $selectedPlatform background, context $selectedCategory, high-contrast, modern clean look, space for text on center, professional cinematography."
                
                if (styleReferenceBase64 != null) {
                    // Custom style reference analysis
                    val refAnalysisPrompt = """
                        Analyze the visual style of this uploaded cover image template.
                        Extract its background details, textures, visual elements, and composition.
                        Write a highly descriptive prompt of 50-80 words to generate a completely new, original background image in the same exact artistic style, colors, and layout structure (without copying text or identical actors). Respond ONLY with the prompt.
                    """.trimIndent()

                    val refRequest = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(
                            Part(text = refAnalysisPrompt),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = styleReferenceBase64!!))
                        )))
                    )

                    val refResponse = RetrofitClient.service.generateProposal(apiKey, refRequest)
                    val generatedPrompt = refResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!generatedPrompt.isNullOrBlank()) {
                        backgroundPrompt = generatedPrompt
                    }
                } else if (!selectedPresetStyle.isNullOrEmpty()) {
                    // Use a preset-guided visual styling prompt
                    backgroundPrompt = when (selectedPresetStyle) {
                        "preset_neon" -> "Vibrant retro-futurism cyberpunk background, digital lights, sleek laser lines, deep purples and neon blues, empty space in the center, professional 1K resolution."
                        "preset_clean" -> "An elegant clean corporate presentation style background, minimalist aesthetic, subtle modern gradients, professional executive background."
                        "preset_warm" -> "Organic warm travel vlog style background, golden hour sunset overlay, soft bokeh lights, beautiful natural aesthetic, plenty of text area."
                        else -> backgroundPrompt
                    }
                }

                // Append Category specifics
                backgroundPrompt += " Optimized for category: $selectedCategory. Ensure high contrast and professional aesthetic."

                // Append high-quality artwork triggers automatically for requested genres (Anime, Movie Characters, 3D and Manga)
                val inputLower = "${videoTitle} ${videoDescription} ${backgroundPrompt} ${selectedCategory}".lowercase(java.util.Locale.ROOT)
                val isAnime = inputLower.contains("anime") || inputLower.contains("otaku") || inputLower.contains("japanese animation")
                val isMovieChar = inputLower.contains("personagem") || inputLower.contains("character") || inputLower.contains("filme") || inputLower.contains("movie") || inputLower.contains("cinema")
                val is3D = inputLower.contains("3d") || inputLower.contains("render") || inputLower.contains("three-dimensional") || inputLower.contains("tridimensional") || inputLower.contains("computação gráfica") || inputLower.contains("cgi")
                val isManga = inputLower.contains("manga") || inputLower.contains("mangá") || inputLower.contains("comic book art") || inputLower.contains("desenho") || inputLower.contains("quadrinho")

                if (isAnime || isMovieChar || is3D || isManga) {
                    backgroundPrompt += ", hyperrealistic, 8k resolution, cinematic lighting, masterwork"
                }

                // 3. Call Image generation model gemini-2.5-flash-image
                val aspect = if (selectedPlatform == "YouTube") "16:9" else "4:3"
                val imageRequest = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = backgroundPrompt)))),
                    generationConfig = GenerationConfig(
                        imageConfig = ImageConfig(aspectRatio = aspect, imageSize = "1K"),
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )

                val imageResponse = RetrofitClient.service.generateImage(apiKey, imageRequest)
                
                // Parse image inlineData
                val base64Data = imageResponse.candidates?.firstOrNull()?.content?.parts
                    ?.firstOrNull { it.inlineData != null }?.inlineData?.data

                if (!base64Data.isNullOrBlank()) {
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    
                    if (bitmap != null) {
                        val path = saveBitmapToStorage(context, bitmap)
                        withContext(Dispatchers.Main) {
                            editorBackgroundImagePath = path
                            editorTitleText = if (videoTitle.isNotBlank()) videoTitle else "TITULO DO VÍDEO"
                            // Move to Editor tab automatically
                            selectedPresetStyle = "preset_flat"
                        }
                    } else {
                        throw IOException("Could not parse generated image bytes.")
                    }
                } else {
                    // Fallback to beautiful background if the image data isn't returned
                    simulateOfflineFallback(context, "API Key success, image limits exceeded. Local generator applied!")
                }

            } catch (e: Exception) {
                // If anything fails in network, fallback procedural
                errorMessage = e.localizedMessage ?: e.message
                simulateOfflineFallback(context, "Erro na API: ${e.localizedMessage}. Gerador local ativado!")
            } finally {
                withContext(Dispatchers.Main) {
                    isGenerating = false
                }
            }
        }
    }

    // --- Procedural Fallback Designer ---
    private suspend fun simulateOfflineFallback(context: Context, statusMessage: String? = null) {
        val messageToShow = statusMessage ?: "Usando gerador de design alternativo (Local). Configure sua chave de API Gemini no AI Studio!"
        withContext(Dispatchers.Main) {
            errorMessage = messageToShow
            aiSuggestions.value = listOf(
                "🎨 Paleta: Contraste Néon para Cliques",
                "📈 Título alternativo sugerido pelo Designer local",
                "⚡ Dica: Insira textos grandes e centralizados!"
            )
        }

        // Generate a beautiful programmatic Bitmap contextually based on selected style & category
        val width = 1280
        val height = 720
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // 1. Draw a gorgeous modern gradient background
        val startColor = when (selectedCategory) {
            "Tech" -> AndroidColor.parseColor("#0F2027")
            "Gaming" -> AndroidColor.parseColor("#430932")
            "Lifestyle" -> AndroidColor.parseColor("#12c2e9")
            "Business" -> AndroidColor.parseColor("#2C3E50")
            "Vlog" -> AndroidColor.parseColor("#e65c00")
            "Motivational" -> AndroidColor.parseColor("#d38312")
            else -> AndroidColor.parseColor("#1f4037")
        }

        val endColor = when (selectedCategory) {
            "Tech" -> AndroidColor.parseColor("#203A43")
            "Gaming" -> AndroidColor.parseColor("#0C061C")
            "Lifestyle" -> AndroidColor.parseColor("#c471ed")
            "Business" -> AndroidColor.parseColor("#000000")
            "Vlog" -> AndroidColor.parseColor("#F9D423")
            "Motivational" -> AndroidColor.parseColor("#a83279")
            else -> AndroidColor.parseColor("#99f2c8")
        }

        // Simple gradient fill
        paint.isAntiAlias = true
        for (i in 0 until height) {
            val ratio = i.toFloat() / height
            val r = (AndroidColor.red(startColor) * (1 - ratio) + AndroidColor.red(endColor) * ratio).toInt()
            val g = (AndroidColor.green(startColor) * (1 - ratio) + AndroidColor.green(endColor) * ratio).toInt()
            val b = (AndroidColor.blue(startColor) * (1 - ratio) + AndroidColor.blue(endColor) * ratio).toInt()
            paint.color = AndroidColor.rgb(r, g, b)
            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), paint)
        }

        // Draw geometric background designs based on Category
        paint.color = AndroidColor.WHITE
        paint.alpha = 25

        if (selectedCategory == "Tech" || selectedCategory == "Business") {
            // Draw clean Grid lines
            for (x in 0..width step 80) {
                canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), paint)
            }
            for (y in 0..height step 80) {
                canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), paint)
            }
        } else if (selectedCategory == "Gaming") {
            // Draw diagonal visual retro laser lines
            paint.strokeWidth = 4f
            for (x in -height..width step 150) {
                canvas.drawLine(x.toFloat(), 0f, (x + height).toFloat(), height.toFloat(), paint)
            }
        } else {
            // Draw smooth soft circles (Bokeh style)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(300f, 250f, 150f, paint)
            canvas.drawCircle(950f, 500f, 200f, paint)
            canvas.drawCircle(700f, 150f, 100f, paint)
        }

        // 2. Save the constructed bitmap locally
        val path = saveBitmapToStorage(context, bitmap)
        withContext(Dispatchers.Main) {
            editorBackgroundImagePath = path
            editorTitleText = if (videoTitle.isNotBlank()) videoTitle else "TITULO DE VÍDEO"
        }
    }

    private fun saveBitmapToStorage(context: Context, bitmap: Bitmap): String {
        val file = File(context.filesDir, "thumbnail_${System.currentTimeMillis()}.jpg")
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (_: Exception) {}
        }
        return file.absolutePath
    }

    // --- Action: Save state to persistent history ---
    fun saveThumbnailToHistory(context: Context, finalBitmap: Bitmap) {
        viewModelScope.launch {
            // Save final rendering image to gallery cache
            val savedPath = saveBitmapToStorage(context, finalBitmap)
            val historyItem = ThumbnailHistory(
                platform = selectedPlatform,
                category = selectedCategory,
                title = editorTitleText,
                textOverlaySize = textOverlaySize,
                textOverlayColor = textOverlayColor,
                textOutlineColor = textOutlineColor,
                textOffsetY = textOffsetY,
                textOffsetX = textOffsetX,
                filterBrightness = filterBrightness,
                filterContrast = filterContrast,
                filterSaturation = filterSaturation,
                filterBlur = filterBlur,
                imagePath = savedPath
            )

            val newId = repository.insert(historyItem)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context, 
                    Translations.getString("save_success", currentLanguage), 
                    Toast.LENGTH_LONG
                ).show()
                // Take back to History Gallery
            }
        }
    }
}

class ViewModelFactory(private val repository: ThumbnailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThumbnailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThumbnailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
