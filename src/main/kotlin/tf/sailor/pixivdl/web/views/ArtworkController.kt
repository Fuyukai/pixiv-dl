package tf.sailor.pixivdl.web.views

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import org.jooq.DSLContext
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.web.Templates
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Artwork view controller.
 */
@Controller("/views/artwork")
class ArtworkController {
    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var minio: MinioHandler

    /**
     * Represents a rendered artwork in the template.
     */
    data class RenderedArtwork(
        val id: Int,
        val title: String,
        val caption: String,
        val pageCount: Int,
        val pageUrls: List<String>,
        val uploadedAt: String,

        val bookmarked: Boolean,
        val bookmarks: Int,
        val views: Int,

        val authorId: Int,
        val authorName: String,

        val tags: List<RenderedTag>
    )

    data class RenderedTag(
        val name: String,
        val translatedName: String?
    )

    @Get("/{artworkId}")
    @Produces("text/html")
    fun renderArtworkPage(@PathVariable artworkId: Int): String {
        val join = ARTWORK
            .join(PIXIV_USER).on(ARTWORK.AUTHOR_ID.eq(PIXIV_USER.ID))

        val artworkResult = dsl.selectFrom(join)
            .where(ARTWORK.ID.eq(artworkId))
            .fetch()

        if (artworkResult.isEmpty()) {
            // TODO: Render 404
            error("404")
        }
        val artwork = artworkResult.first()

        // load tags
        val join2 = ARTWORK_TAG.join(TAG).on(ARTWORK_TAG.TAG_ID.eq(TAG.ID))
        val tags = dsl.selectFrom(join2)
            .where(ARTWORK_TAG.ARTWORK_ID.eq(artworkId))
            .fetch().toList()

        val tagObjects = tags.map {
            RenderedTag(it.get(TAG.ORIGINAL_NAME), it.get(TAG.TRANSLATED_NAME))
        }

        // get page URLs
        val pageCount = artwork.get(ARTWORK.PAGE_COUNT)
        val pageUrls = (0 until pageCount).map { page ->
            minio.getArtworkPage(artworkId, page)
        }

        // format uploaded date
        val uploadedAt = artwork.get(ARTWORK.UPLOADED_AT)
        val uploadedFmt = DateTimeFormatter.ISO_DATE_TIME.format(uploadedAt)

        // check for any bookmarks
        val isBookmarked = dsl.fetchCount(
            dsl.selectFrom(BOOKMARK).where(BOOKMARK.ARTWORK_ID.eq(artworkId))
        ) > 0

        val model = RenderedArtwork(
            id = artwork.get(ARTWORK.ID),
            title = artwork.get(ARTWORK.TITLE),
            caption = artwork.get(ARTWORK.CAPTION) ?: "No caption available.",
            pageCount = pageCount,
            pageUrls = pageUrls,
            uploadedAt = uploadedFmt,

            bookmarked = isBookmarked,
            bookmarks = artwork.get(ARTWORK.TOTAL_BOOKMARKS),
            views = artwork.get(ARTWORK.TOTAL_VIEWS),

            authorId = artwork.get(PIXIV_USER.ID),
            authorName = artwork.get(PIXIV_USER.DISPLAY_NAME),

            tags = tagObjects
        )

        val context = mutableMapOf<String, Any>(
            "artwork" to model
        )

        return if (pageCount > 1) {
            Templates.render("artwork_view/multiple.html", context)
        } else {
            Templates.render("artwork_view/single.html", context)
        }
    }
}
