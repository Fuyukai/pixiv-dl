package tf.sailor.pixivdl.web.views

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import okhttp3.internal.checkOffsetAndCount
import org.jooq.DSLContext
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.papi.BookmarkType
import tf.sailor.pixivdl.web.SortMode
import javax.annotation.security.RunAs
import javax.inject.Inject

@Controller("/views/bookmarks")
class BookmarkGridController {
    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var minio: MinioHandler

    /**
     * Root controller.
     */
    @Get("/")
    fun root() {
        TODO()
    }

    /**
     * Renders a bookmark grid.
     */
    fun renderBookmarkGrid(
        whoFor: Int, offset: Int, sortMode: SortMode, category: BookmarkType
    ): String {
        val offset = if (offset < 0) 0 else offset
        val grid = Grid("bookmark")

        val join = ARTWORK
            .join(PIXIV_USER).on(ARTWORK.AUTHOR_ID.eq(PIXIV_USER.ID))
            .join(BOOKMARK).on(ARTWORK.ID.eq(BOOKMARK.ARTWORK_ID))

        var condition = BOOKMARK.IS_PRIVATE.eq(category == BookmarkType.PRIVATE)
        if (whoFor > -1) {
            condition = condition.and(BOOKMARK.BOOKMARKER_ID.eq(whoFor))
        }

        val sort = sortMode.toSortMode(ARTWORK.ID)
        val query = dsl.selectFrom(join)
                .where(condition)

        val total = dsl.fetchCount(query)
        val results = query.orderBy(sort).limit(25).offset(offset).fetch()

        // build cards
        val cards = results.toGridCards(minio)

        val ctx = mutableMapOf<String, Any>(
            "bookmark_category" to category.name.toLowerCase().capitalize()
        )
        return grid.render(ctx, offset, total, sortMode, *cards.toTypedArray())
    }

    /**
     * Views public bookmarks.
     */
    @Get("/public")
    @Produces("text/html")
    fun viewPublicBookmarks(
        @QueryValue("who_for", defaultValue = "-1") whoFor: Int,
        @QueryValue("offset", defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        return renderBookmarkGrid(whoFor, offset, sortMode, BookmarkType.PUBLIC)
    }

    /**
     * Views private bookmarks.
     */
    @Get("/private")
    @Produces("text/html")
    fun viewPrivateBookmarks(
        @QueryValue("who_for", defaultValue = "-1") whoFor: Int,
        @QueryValue("offset", defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        return renderBookmarkGrid(whoFor, offset, sortMode, BookmarkType.PRIVATE)
    }
}
