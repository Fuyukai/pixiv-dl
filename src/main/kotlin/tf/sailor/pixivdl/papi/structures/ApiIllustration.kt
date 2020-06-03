/*
 * This file is part of pixiv-dl.
 *
 * pixiv-dl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pixiv-dl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pixiv-dl.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package tf.sailor.pixivdl.papi.structures

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.ZonedDateTime
import tf.sailor.pixivdl.papi.IllustrationType
import tf.sailor.pixivdl.papi.LewdLevel

/**
 * Represents an illustration from the pixiv API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiIllustration(
    /** The ID of this illustration. */
    val id: Int,
    /** The title of this illustration. */
    val title: String,
    /** The caption for this illustration. */
    val caption: String? = null,
    /** The type of this illustration. */
    @param:JsonDeserialize(using = TypeDeserialiser::class)
    val type: IllustrationType,
    /** The creation date for this illustration. */
    @param:JsonProperty("create_date")
    @param:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssVV")
    val creationDate: ZonedDateTime,
    /** The page count for this illustration. */
    val pageCount: Int,
    /** The lewd level for this illustration. */
    @param:JsonProperty("sanity_level")
    @param:JsonDeserialize(using = SanityLevelDeserialiser::class)
    val lewdLevel: LewdLevel,
    /** If this illustration is marked R-18. */
    @param:JsonProperty("x_restrict") val r18: Boolean,
    /** If this illustration is marked R-18G. */
    @param:JsonProperty("restrict") val r18g: Boolean,
    /** The list of tags for this illustration. */
    val tags: List<ApiTag>,
    /** The author of this illustration. */
    @param:JsonProperty("user") val author: ApiUser,

    /** The number of bookmarks this artwork has. */
    @param:JsonProperty("total_bookmarks") val bookmarks: Int,
    /** The number of comments this artwork has. */
    @param:JsonProperty("total_comments") val commentCount: Int,
    /** The number of views this artwork has. */
    @param:JsonProperty("total_view") val views: Int,

    // page shenanigans
    @param:JsonProperty("meta_single_page")
    @param:JsonDeserialize(using = SinglePageDeserialiser::class)
    private val metaSinglePage: MetaSinglePage?,
    private val imageUrls: ImageUrls,
    @param:JsonProperty("meta_pages") private val _pages: List<MetaMultiplePage>
) {
    /** Custom type property deserialiser. */
    private class TypeDeserialiser : JsonDeserializer<IllustrationType>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): IllustrationType {
            val next = p.readValueAsTree<JsonNode>()
            return when (val it = next.asText()) {
                IllustrationType.ILLUSTRATION.apiName -> IllustrationType.ILLUSTRATION
                IllustrationType.MANGA.apiName -> IllustrationType.MANGA
                IllustrationType.UGOIRA.apiName -> IllustrationType.UGOIRA
                else -> error("Unknown type $it")
            }
        }
    }

    /** Custom sanity level enum deserialiser. */
    private class SanityLevelDeserialiser : JsonDeserializer<LewdLevel>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LewdLevel {
            val next = p.readValueAsTree<JsonNode>()
            return when (next.asInt()) {
                2 -> LewdLevel.LOW
                4 -> LewdLevel.MED
                6 -> LewdLevel.HIGH
                else -> LewdLevel.MED
            }
        }
    }

    /** Custom deserialiser used to turn the single page into a null value, if applicable. */
    private class SinglePageDeserialiser : JsonDeserializer<MetaSinglePage?>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MetaSinglePage? {
            val node = p.readValueAsTree<JsonNode>()
            if (node.isEmpty) {
                return null
            }
            val ogUrl = node.get("original_image_url").asText()
            return MetaSinglePage(ogUrl)
        }
    }

    /** Single page metadata. */
    data class MetaSinglePage(val originalImageUrl: String)

    /** Multiple-page metadata. */
    data class MetaMultiplePage(val imageUrls: ImageUrls)

    /**
     * The list of pages for this illustration.
     */
    val pages: List<ImagePage> by lazy {
        // single page images
        if (metaSinglePage != null) {
            val urls = imageUrls.copy(original = metaSinglePage.originalImageUrl)
            return@lazy listOf(ImagePage(0, urls))
        }

        // multiple page images
        return@lazy _pages.mapIndexed {
            idx, page -> ImagePage(idx, page.imageUrls)
        }
    }

    /** The main page for this illustration. */
    val mainPage: ImagePage @JsonIgnore get() = pages.first()

    /** If this is a single-page illustration. */
    val isSinglePage: Boolean @JsonIgnore get() = pages.size == 1

    /** The pixiv URL for this artwork. */
    val pixivUrl: String @JsonIgnore get() = "https://www.pixiv.net/artworks/$id"
}
