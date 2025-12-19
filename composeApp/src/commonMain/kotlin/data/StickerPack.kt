package data

data class StickerPack(
    val name: String,
    val baseUrl: String,
    val count: Int,
    val startIndex: Int = 0,
    val types: List<StickerType> = listOf(StickerType.WEBP),
    val subPath: String = ""
) {
    private val extractedName: String = baseUrl.split("/").lastOrNull() ?: name

    fun getUrl(index: Int, preferredTypes: List<StickerType> = types): String? {
        if (index !in startIndex until startIndex + count) return null

        return preferredTypes.firstNotNullOfOrNull { type ->
            buildUrl(index, type)
        }
    }

    private fun buildUrl(index: Int, type: StickerType): String {
        val fileName = "$index.${type.ext}"
        val typeDir = type.ext.lowercase()
        val extraPath = if (subPath.isEmpty()) "" else "$subPath/"
        return "$baseUrl/$extraPath$typeDir/$fileName"
    }
    
    fun getAllUrls(preferredTypes: List<StickerType> = types): List<String> {
        return (startIndex until startIndex + count).mapNotNull { index ->
            getUrl(index, preferredTypes)
        }
    }
    
    val totalCount: Int = count
    val typesDisplay: String = types.joinToString { it.displayName }
}

enum class StickerType(val ext: String, val displayName: String) {
    WEBP("webp", "WebP"),
    PNG("png", "PNG"),
    GIF("gif", "GIF");
    
    companion object {
        fun fromExtension(ext: String): StickerType? {
            val targetExt = ext.lowercase()
            return entries.find { it.ext == targetExt }
        }
    }
}
