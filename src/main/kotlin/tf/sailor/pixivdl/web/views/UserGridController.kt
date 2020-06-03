package tf.sailor.pixivdl.web.views

import io.micronaut.http.annotation.*
import org.jooq.DSLContext
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Keys
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.web.SortMode
import javax.inject.Inject

@Controller("/views/user")
class UserGridController {
    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var minio: MinioHandler

    /**
     * Renders the grid for all users.
     */
    @Get("/")
    @Produces("text/html")
    fun renderAllUserGrid(
        @QueryValue(defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        val offset = if (offset < 0) 0 else offset
        val grid = Grid("base")

        val authorCount = dsl.fetchCount(PIXIV_USER)
        // requires the random aggregate we defined in v2 migration
        val result = dsl.fetch("""
            select pixiv_user.*, artwork.*, i.count from (
                select random(artwork.id) as id, artwork.author_id, count(*) as count
                from artwork
                group by artwork.author_id
            ) i 
            right join pixiv_user on pixiv_user.id = i.author_id
            join artwork on artwork.id = i.id
            order by i.count desc limit 25;
        """.trimIndent())

        val cards = mutableListOf<Grid.GridCard>()
        for (record in result) {
            val artworkId = record.get(ARTWORK.ID)!!
            val artworkLink = "/views/artwork/${artworkId}"
            val artworkUrl = minio.getArtworkPage(artworkId, 0)

            val r18 = record.get(ARTWORK.R18)!! || record.get(ARTWORK.R18G)!!
            val pageCount = record.get(ARTWORK.PAGE_COUNT)!!

            val authorId = record.get(PIXIV_USER.ID)!!
            val authorDisplayName =
                record.get(PIXIV_USER.DISPLAY_NAME) ?: record.get(PIXIV_USER.USERNAME)!!
            val count = record.get("count")

            val caption = authorDisplayName
            val captionLink = "/views/user/${authorId}"
            val subtitle = "${count} artworks"
            val footer = "ID: $authorId"

            val card = Grid.GridCard(
                artworkId = artworkId,
                artworkUrl = artworkUrl,
                artworkLink = artworkLink,

                caption = caption,
                captionLink = captionLink,
                subtitle = subtitle,

                pageCount = pageCount,
                r18 = r18,
                footer = footer
            )
            cards.add(card)
        }

        val ctx = mutableMapOf<String, Any>()
        return grid.render(ctx, offset, authorCount, sortMode, *cards.toTypedArray())
    }

    /**
     * Shows a user profile grid.
     */
    @Get("/{userId}")
    @Produces("text/html")
    fun showIndividualUser(
        @PathVariable userId: Int,
        @QueryValue(defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        val offset = if (offset < 0) 0 else offset
        val sort = sortMode.toSortMode(ARTWORK.ID)

        // could do better with not being a join...
        // but oh well...
        val join =
            PIXIV_USER.join(ARTWORK).onKey(Keys.ARTWORK__ARTWORK_AUTHOR_ID_FKEY)

        val count =
            dsl.fetchCount(dsl.selectFrom(ARTWORK).where(ARTWORK.AUTHOR_ID.eq(userId)))

        if (count <= 0) {
            error("404")
        }

        val authorArtworks = dsl
            .selectFrom(join)
            .where(PIXIV_USER.ID.eq(userId))
            .orderBy(sort)
            .limit(25)
            .offset(offset)
            .fetch()

        val sampleRecord = authorArtworks.first()
        val authorId = sampleRecord.get(PIXIV_USER.ID)!!
        val authorDisplayName =
            sampleRecord.get(PIXIV_USER.DISPLAY_NAME) ?: sampleRecord.get(PIXIV_USER.USERNAME)!!

        val cards = authorArtworks.toGridCards(minio)
        val ctx = mutableMapOf<String, Any>(
            "author_id" to authorId,
            "author_name" to authorDisplayName,
            "author_account_name" to sampleRecord.get(PIXIV_USER.USERNAME)
        )

        val grid = Grid("oneuser")
        return grid.render(ctx, offset, count, sortMode, *cards.toTypedArray())
    }
}
