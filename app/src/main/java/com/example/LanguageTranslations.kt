package com.example

enum class Language(val code: String, val displayName: String, val flag: String) {
    PT("pt", "Português", "🇧🇷"),
    EN("en", "English", "🇺🇸"),
    ES("es", "Español", "🇪🇸"),
    FR("fr", "Français", "🇫🇷")
}

object Translations {
    private val ptStrings = mapOf(
        "app_title" to "ClickBoost AI",
        "app_subtitle" to "Miniaturas e capas profissionais otimizadas",
        "tab_create" to "Criar",
        "tab_editor" to "Estúdio / Editor",
        "tab_history" to "Histórico",
        "tab_premium" to "Premium & Idioma",
        
        "platform_title" to "1. Escolha a Plataforma",
        "platform_youtube" to "YouTube Thumbnail",
        "platform_facebook" to "Facebook Cover",
        "category_title" to "2. Categoria de Vídeo",
        "details_title" to "3. Ideia ou Título do Vídeo",
        "title_placeholder" to "Ex: Como criar um app em 10 minutos!",
        "desc_placeholder" to "Descreva sobre o que é o vídeo, cores desejadas ou atmosfera...",
        "style_reference_title" to "4. Referência de Estilo / Style Copy (IA)",
        "upload_custom" to "Enviar Imagem de Referência",
        "select_reference" to "Selecionar Estilo Sugerido",
        "free_banner_title" to "Função Style Copy (IA Referenciada):",
        "free_uses_left" to "Utilizações restantes hoje: %d / 2",
        "upgrade_full" to "Desbloquear ilimitado por $2/mês",
        "generate_button" to "Gerar Capa com Inteligência Artificial",
        "generating" to "Gerando imagens incríveis com Gemini...",
        
        "style_tech" to "Tecnologia & Programação",
        "style_gaming" to "Games & Esports",
        "style_lifestyle" to "Lifestyle & Viagem",
        "style_business" to "Negócios & Progresso",
        "style_vlog" to "Vlog & Ação",
        "style_motivational" to "Motivacional & Sucesso",
        "style_editorial" to "Editorial & Notícias",
        
        "editor_canvas_title" to "Personalização & Filtros",
        "edit_text_overlay" to "Configurações de Texto",
        "color_labels" to "Cor de Preenchimento",
        "color_outline" to "Cor de Contorno / Sombra",
        "size_label" to "Tamanho do Texto",
        "offset_y" to "Posição Vertical",
        "offset_x" to "Posição Horizontal",
        "filters_title" to "Ajustes de Filtros de Cor",
        "filter_brightness" to "Brilho",
        "filter_contrast" to "Contraste",
        "filter_saturation" to "Saturação",
        "filter_blur" to "Desfoque",
        "save_to_gallery" to "Exportar Thumbnail (Galeria)",
        "save_success" to "Capa exportada com sucesso para Downloads!",
        "canvas_no_image" to "Nenhuma imagem ativa. Crie uma na aba 'Criar' ou envie uma imagem de base abaixo!",
        "upload_base" to "Carregar Imagem de Fundo customizada",
        
        "history_empty" to "Nenhuma thumbnail salva ainda. Suas criações salvas aparecerão aqui!",
        "load_to_editor" to "Editar capa selecionada",
        "delete" to "Excluir",
        "created_at" to "Gerado em: %s",
        "history_title" to "Minha Galeria de Capas",
        
        "premium_card_title" to "Plano Creator Premium ✨",
        "premium_price" to "$2.00 / mês",
        "premium_benefit1" to "✓ Geração ilimitada de imagens e layouts",
        "premium_benefit2" to "✓ Uso ILIMITADO da IA de cópia de estilo (Style Reference)",
        "premium_benefit3" to "✓ IA Avançada + Alta Resolução (Textos nítidos)",
        "premium_benefit4" to "✓ Renderização e download ultra-rápidos",
        "is_premium_user" to "Você tem o Plano Premium ativo!",
        "subscribe_now" to "Assinar Plano Premium ($2/mês)",
        "unsubscribe" to "Mudar para Plano Gratuito (Simular)",
        "language_section" to "Preferência de Idioma",
        "security_warning" to "Aviso de Segurança: Chave Gemini obtida via BuildConfig.",
        
        "preset_flat" to "Estilo Moderno Minimalista",
        "preset_neon" to "Cyberpunk Neon Vibrante",
        "preset_clean" to "Corporativo Clean elegante",
        "preset_warm" to "Vlog Tons Quentes Orgânico",
        
        "limit_reached_title" to "Limite diário atingido!",
        "limit_reached_desc" to "Você atingiu o limite de 2 utilizações diárias de Cópia de Estilo (Style Copy) para usuários gratuitos. Faça o upgrade para o Plano Premium por apenas $2/mês para uso ilimitado!",
        "close" to "Fechar",
        "upgrade" to "Faça o Upgrade",
        "tab_inspiration" to "Inspiração",
        "inspiration_title" to "Galeria de Inspiração",
        "inspiration_subtitle" to "Explore os melhores designs e baixe ou use direto no Estúdio",
        "category_anime" to "Anime",
        "category_movie" to "Personagens de Filme",
        "category_3d" to "Estilo 3D",
        "category_manga" to "Mangá",
        "use_in_editor" to "Usar no Estúdio",
        "download_to_gallery" to "Salvar na Galeria",
        "download_success" to "Imagem salva com sucesso na galeria do celular!",
        "download_error" to "Erro ao salvar imagem."
    )

    private val enStrings = mapOf(
        "app_title" to "AI Thumbnail Studio",
        "app_subtitle" to "High-engagement covers & thumbnails optimized",
        "tab_create" to "Create",
        "tab_editor" to "Studio / Editor",
        "tab_history" to "History",
        "tab_premium" to "Premium & Lang",
        
        "platform_title" to "1. Select Platform",
        "platform_youtube" to "YouTube Thumbnail",
        "platform_facebook" to "Facebook Cover",
        "category_title" to "2. Video Category",
        "details_title" to "3. Video Title or Concept",
        "title_placeholder" to "E.g.: How I built an app in 10 minutes!",
        "desc_placeholder" to "Describe what the video is about, desired atmosphere or colors...",
        "style_reference_title" to "4. Style Reference / Style Copy (AI)",
        "upload_custom" to "Upload Reference Image",
        "select_reference" to "Select Suggested Style",
        "free_banner_title" to "Style Copy Feature (AI Referenced):",
        "free_uses_left" to "Daily uses left: %d / 2",
        "upgrade_full" to "Unlock unlimited for $2/month",
        "generate_button" to "Generate Cover with AI",
        "generating" to "Generating incredible visuals with Gemini...",
        
        "style_tech" to "Technology & Coding",
        "style_gaming" to "Gaming & Esports",
        "style_lifestyle" to "Lifestyle & Travel",
        "style_business" to "Business & Finance",
        "style_vlog" to "Vlog & Action",
        "style_motivational" to "Motivational & Success",
        "style_editorial" to "Editorial & News",
        
        "editor_canvas_title" to "Customization & Filters",
        "edit_text_overlay" to "Text Overlay Settings",
        "color_labels" to "Text Fill Color",
        "color_outline" to "Outline / Shadow Color",
        "size_label" to "Text Size",
        "offset_y" to "Vertical Position",
        "offset_x" to "Horizontal Position",
        "filters_title" to "Color Filter Adjustments",
        "filter_brightness" to "Brightness",
        "filter_contrast" to "Contrast",
        "filter_saturation" to "Saturation",
        "filter_blur" to "Blur / Soften",
        "save_to_gallery" to "Export Thumbnail (Gallery)",
        "save_success" to "Cover successfully exported to Downloads!",
        "canvas_no_image" to "No active image background. Create one in the 'Create' tab or upload custom background below!",
        "upload_base" to "Upload Custom Background Image",
        
        "history_empty" to "No thumbnails saved yet. Your saved creations will show up here!",
        "load_to_editor" to "Load into Studio",
        "delete" to "Delete",
        "created_at" to "Created at: %s",
        "history_title" to "My Cover Gallery",
        
        "premium_card_title" to "Creator Premium Plan ✨",
        "premium_price" to "$2.00 / month",
        "premium_benefit1" to "✓ Unlimited image and layout generation",
        "premium_benefit2" to "✓ UNLIMITED Style Copy / Reference replication",
        "premium_benefit3" to "✓ Advanced high-res IA engine for crisp overlay textures",
        "premium_benefit4" to "✓ Ultra-fast render and instant generation priority",
        "is_premium_user" to "You have active Premium Plan!",
        "subscribe_now" to "Subscribe to Premium ($2/mo)",
        "unsubscribe" to "Switch to Free Plan (Simulate)",
        "language_section" to "Language Preferences",
        "security_warning" to "Security Warning: Gemini credentials loaded securely via BuildConfig.",
        
        "preset_flat" to "Modern Minimalist Style",
        "preset_neon" to "Vibrant Cyberpunk Neon",
        "preset_clean" to "Sleek Corporate Clean",
        "preset_warm" to "Organic Vlog Warm Vibe",
        
        "limit_reached_title" to "Daily limit reached!",
        "limit_reached_desc" to "You have reached your 2 daily Free Style Copy occurrences today. Upgrade to the Premium Plan for just $2/month for unrestricted generation!",
        "close" to "Close",
        "upgrade" to "Upgrade Now",
        "tab_inspiration" to "Inspiration",
        "inspiration_title" to "Inspiration Gallery",
        "inspiration_subtitle" to "Explore outstanding designs and download or use them value-added",
        "category_anime" to "Anime",
        "category_movie" to "Movie Characters",
        "category_3d" to "3D Style",
        "category_manga" to "Manga",
        "use_in_editor" to "Use in Studio",
        "download_to_gallery" to "Save to Gallery",
        "download_success" to "Image successfully saved to device gallery!",
        "download_error" to "Download error."
    )

    private val esStrings = mapOf(
        "app_title" to "Creador de Miniaturas IA",
        "app_subtitle" to "Miniaturas y portadas de video profesionales y optimizadas",
        "tab_create" to "Crear",
        "tab_editor" to "Estudio / Editor",
        "tab_history" to "Historial",
        "tab_premium" to "Premium e Idioma",
        
        "platform_title" to "1. Elige Plataforma",
        "platform_youtube" to "YouTube Thumbnail",
        "platform_facebook" to "Facebook Cover",
        "category_title" to "2. Categoría de Video",
        "details_title" to "3. Idea o Título de Video",
        "title_placeholder" to "Ej: ¡Cómo crear una app en 10 minutos!",
        "desc_placeholder" to "Describe el tema del video, atmósfera o colores deseados...",
        "style_reference_title" to "4. Referencia de Estilo / Style Copy (IA)",
        "upload_custom" to "Subir Imagen de Referencia",
        "select_reference" to "Seleccionar Estilo Sugerido",
        "free_banner_title" to "Función Style Copy (IA Referenciada):",
        "free_uses_left" to "Usos restantes hoy: %d / 2",
        "upgrade_full" to "Desbloquear ilimitado por $2/mes",
        "generate_button" to "Generar Portada con IA",
        "generating" to "Generando imágenes increíbles con Gemini...",
        
        "style_tech" to "Tecnología y Programación",
        "style_gaming" to "Juegos & Esports",
        "style_lifestyle" to "Estilo de Vida y Viaje",
        "style_business" to "Negocios y Progreso",
        "style_vlog" to "Vlog y Acción",
        "style_motivational" to "Motivacional y Éxito",
        "style_editorial" to "Editorial y Noticias",
        
        "editor_canvas_title" to "Personalización y Filtros",
        "edit_text_overlay" to "Configuración del Texto",
        "color_labels" to "Color del Texto",
        "color_outline" to "Color de Contorno / Sombra",
        "size_label" to "Tamaño del Texto",
        "offset_y" to "Posición Vertical",
        "offset_x" to "Posición Horizontal",
        "filters_title" to "Ajustes de Filtros de Color",
        "filter_brightness" to "Brillo",
        "filter_contrast" to "Contraste",
        "filter_saturation" to "Saturación",
        "filter_blur" to "Borroso (Desenfoque)",
        "save_to_gallery" to "Exportar Miniatura (Galería)",
        "save_success" to "¡Portada exportada con éxito en Descargas!",
        "canvas_no_image" to "Ninguna imagen de fondo activa. ¡Cree una en el panel 'Crear' o suba una personalizada!",
        "upload_base" to "Subir Imagen de Fondo Personalizada",
        
        "history_empty" to "Aún no hay miniaturas guardadas. ¡Tus creaciones aparecerán aquí!",
        "load_to_editor" to "Cargar en el Estudio",
        "delete" to "Eliminar",
        "created_at" to "Creado en: %s",
        "history_title" to "Mi Galería de Portadas",
        
        "premium_card_title" to "Plan Creador Premium ✨",
        "premium_price" to "$2.00 / mes",
        "premium_benefit1" to "✓ Generación ilimitada de imágenes y diseños",
        "premium_benefit2" to "✓ Uso ILIMITADO de la réplica de estilo (Style Copy)",
        "premium_benefit3" to "✓ Motor IA Avanzado de alta resolución",
        "premium_benefit4" to "✓ Descarga y renderizado ultra rápidos",
        "is_premium_user" to "¡Tienes activo el Plan Premium!",
        "subscribe_now" to "Suscribirse a Premium ($2/mes)",
        "unsubscribe" to "Cambiar a Plan Gratuito (Simular)",
        "language_section" to "Preferencia de Idioma",
        "security_warning" to "Seguridad: Clave de acceso cargada mediante BuildConfig.",
        
        "preset_flat" to "Estilo Moderno Minimalista",
        "preset_neon" to "Neon Cyberpunk Vibrante",
        "preset_clean" to "Estilo Corporativo Limpio",
        "preset_warm" to "Estilos Vlog Tonos Cálidos",
        
        "limit_reached_title" to "¡Límite diario alcanzado!",
        "limit_reached_desc" to "Has alcanzado tus 2 réplicas diarias (Style Copy) del plan gratuito. ¡Actualízate al Plan Premium por solo $2 al mes para generación ilimitada sin límites!",
        "close" to "Cerrar",
        "upgrade" to "Actualizar ahora"
    )

    private val frStrings = mapOf(
        "app_title" to "AI Thumbnail Studio",
        "app_subtitle" to "Miniatures et couvertures optimisées de haute qualité",
        "tab_create" to "Créer",
        "tab_editor" to "Studio / Éditeur",
        "tab_history" to "Historique",
        "tab_premium" to "Premium & Langue",
        
        "platform_title" to "1. Choisir la plateforme",
        "platform_youtube" to "YouTube Thumbnail",
        "platform_facebook" to "Facebook Cover",
        "category_title" to "2. Catégorie de vidéo",
        "details_title" to "3. Idée ou titre de la vidéo",
        "title_placeholder" to "Ex : Comment créer une appli en 10 minutes !",
        "desc_placeholder" to "Décrivez le sujet de la vidéo, les couleurs ou l'atmosphère...",
        "style_reference_title" to "4. Style de Référence / Style Copy (IA)",
        "upload_custom" to "Uploader une image modèle",
        "select_reference" to "Sélectionner un style prédéfini",
        "free_banner_title" to "Fonction Style Copy (Référence IA):",
        "free_uses_left" to "Occurrences restantes aujourd'hui: %d / 2",
        "upgrade_full" to "Débloquer l'illimité pour $2/mois",
        "generate_button" to "Générer la couverture avec l'IA",
        "generating" to "Génération en cours avec Gemini...",
        
        "style_tech" to "Technologie & Code",
        "style_gaming" to "Jeux Vidéo & Esports",
        "style_lifestyle" to "Lifestyle & Voyage",
        "style_business" to "Business & Finance",
        "style_vlog" to "Vlog & Action",
        "style_motivational" to "Motivation & Succès",
        "style_editorial" to "Éditorial & Actu",
        
        "editor_canvas_title" to "Personnalisation & Filtres",
        "edit_text_overlay" to "Paramètres de superposition du texte",
        "color_labels" to "Couleur du texte",
        "color_outline" to "Couleur du contour / ombre",
        "size_label" to "Taille de police",
        "offset_y" to "Position verticale",
        "offset_x" to "Position horizontale",
        "filters_title" to "Réglages des filtres de couleur",
        "filter_brightness" to "Luminosité",
        "filter_contrast" to "Contraste",
        "filter_saturation" to "Saturation",
        "filter_blur" to "Flou",
        "save_to_gallery" to "Exporter la miniature",
        "save_success" to "Image exportée dans le dossier Téléchargements !",
        "canvas_no_image" to "Aucun arrière-plan actif. Créez-en un sur 'Créer' ou importez ci-dessous !",
        "upload_base" to "Importer un arrière-plan personnalisé",
        
        "history_empty" to "Aucune création enregistrée. Vos miniatures apparaîtront ici !",
        "load_to_editor" to "Charger dans l'Éditeur",
        "delete" to "Supprimer",
        "created_at" to "Créé le : %s",
        "history_title" to "Ma Galerie de Créations",
        
        "premium_card_title" to "Forfait Créateur Premium ✨",
        "premium_price" to "$2.00 / mois",
        "premium_benefit1" to "✓ Générations illimitées d'illustrations et compositions",
        "premium_benefit2" to "✓ Utilisation ILLIMITÉE de la réplication de style (Style Copy)",
        "premium_benefit3" to "✓ Moteur de génération IA Advanced haute résolution",
        "premium_benefit4" to "✓ Rendu et téléchargement rapides prioritaires",
        "is_premium_user" to "Le Forfait Premium est actif !",
        "subscribe_now" to "S'abonner à Premium ($2/mois)",
        "unsubscribe" to "Simuler le plan gratuit",
        "language_section" to "Mes Préférences de Langue",
        "security_warning" to "Avertissement de Sécurité : Clés de connexion obtenues via BuildConfig.",
        
        "preset_flat" to "Style Moderne Minimaliste",
        "preset_neon" to "Cyberpunk Néon Éclatant",
        "preset_clean" to "Style Business Propre",
        "preset_warm" to "Vibe Vlog tons chaleureux",
        
        "limit_reached_title" to "Limite quotidienne atteinte !",
        "limit_reached_desc" to "Vous avez utilisé vos 2 copies de style gratuites aujourd'hui. Passez au Forfait Premium pour seulement $2/mois pour profiter d'un usage illimité !",
        "close" to "Fermer",
        "upgrade" to "Mettre à niveau"
    )

    fun getString(key: String, lang: Language, vararg args: Any): String {
        val map = when (lang) {
            Language.PT -> ptStrings
            Language.EN -> enStrings
            Language.ES -> esStrings
            Language.FR -> frStrings
        }
        val raw = map[key] ?: enStrings[key] ?: key
        return try {
            String.format(raw, *args)
        } catch (_: Exception) {
            raw
        }
    }

    /**
     * Instill translation rules inside AI prompts dynamically based on selected language
     */
    fun getSystemPromptInstruction(lang: Language): String {
        return when (lang) {
            Language.PT -> "Você é um especialista em engajamento visual. Gere retornos de layout em Português."
            Language.EN -> "You are a visual engagement developer. Respond in English."
            Language.ES -> "Eres un diseñador experto en engagement visual. Responde en Español."
            Language.FR -> "Vous êtes un designer expert en engagement visuel. Veuillez répondre en Français."
        }
    }
}
