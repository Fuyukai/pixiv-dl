package tf.sailor.pixivdl.web.views

import io.micronaut.http.annotation.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.rand
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.web.SortMode
import javax.inject.Inject

/**
 * Controller for the tag grids.
 */
@Controller("/views/tags")
class TagGridController {
    @Inject
    lateinit var dsl: DSLContext

    @Inject
    lateinit var minio: MinioHandler

    /**
     * Renders the grid for all tags.
     */
    @Get("/")
    @Produces("text/html")
    fun renderAllTagGrid(
        @QueryValue(defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        val offset = if (offset < 0) 0 else offset
        val grid = Grid("base")
        val cards = mutableListOf<Grid.GridCard>()

        val tagCount = dsl.fetchCount(TAG)

        val artworkJoin = ARTWORK_TAG
            .innerJoin(ARTWORK).on(ARTWORK_TAG.ARTWORK_ID.eq(ARTWORK.ID))

        // this is almost definitely going to shred performance.

        /*val tagResult = dsl.fetch("""
            select tag.id, tag.original_name, tag.translated_name, i.count
            from (
                select artwork_tag.tag_id, count(*) as count
                from artwork_tag
                group by artwork_tag.tag_id
            ) i right join tag on tag.id = i.tag_id order by i.count desc limit 25 offset ?;
        """.trimIndent(), offset)*/

        val tagResult = dsl.fetch("""
            select artwork.*, tag.id, tag.original_name, tag.translated_name, i.count
            from (
                select random(artwork_tag.artwork_id) as artwork_id, artwork_tag.tag_id, count(*) as count
                from artwork_tag
                group by artwork_tag.tag_id
            ) i 
            right join tag on tag.id = i.tag_id
            join artwork on artwork.id = i.artwork_id
            order by i.count 
            desc limit 25 offset ?;
        """.trimIndent(), offset)

        for ((idx, record) in tagResult.withIndex()) {
            val tagId = record.get(TAG.ID)

            // TODO: Investigate tablesample
            /*val artworkTagsResult1 = dsl.execute("""
                select artwork.*, artwork_tag.* from artwork_tag
                tablesample SYSTEM(0.1)
                join artwork on artwork_tag.artwork_id = artwork.id
                where artwork_tag.tag_id = ?
                limit 1;
            """.trimIndent(), tagId)*/

            val artworkId = record.get(ARTWORK.ID)
            val artworkLink = "/views/artwork/${artworkId}"
            val artworkUrl = minio.getArtworkPage(artworkId, 0)

            val r18 = record.get(ARTWORK.R18) || record.get(ARTWORK.R18G)
            val pageCount = record.get(ARTWORK.PAGE_COUNT)

            val tagName = record.get(TAG.ORIGINAL_NAME)
            val translatedName = record.get(TAG.TRANSLATED_NAME)

            val caption = if (translatedName != null) {
                "$translatedName (${tagName})"
            } else {
                tagName
            }
            val captionLink = "/views/tags/$tagName"
            val subtitle = "${record.get("count")} artworks"
            val footer = "Rank: ${offset + idx + 1}"

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

        return grid.render(mutableMapOf(), offset, tagCount, sortMode, *cards.toTypedArray())
    }

    /**
     * Shows an individual tag's view.
     */
    @Get("/{tagName}")
    @Produces("text/html")
    fun showIndividualTag(
        @PathVariable tagName: String,
        @QueryValue(defaultValue = "-1") offset: Int,
        sortMode: SortMode?
    ): String {
        val sortMode = sortMode ?: SortMode.DESCENDING
        val offset = if (offset < 0) 0 else offset

        // gotta do this for the tag names
        // if there's no artworks matching this tag (unlikely, but a possibility)
        val tagWhere = TAG.ORIGINAL_NAME.eq(tagName).or(TAG.TRANSLATED_NAME.eq(tagName))
        val tagResult = dsl.selectFrom(TAG).where(tagWhere).fetch()
        if (tagResult.isEmpty()) error("404")
        val tagRecord = tagResult.first()


        val join = ARTWORK_TAG
            .rightJoin(ARTWORK).on(ARTWORK_TAG.ARTWORK_ID.eq(ARTWORK.ID))
            .join(PIXIV_USER).on(ARTWORK.AUTHOR_ID.eq(PIXIV_USER.ID))

        val sort = sortMode.toSortMode(ARTWORK.ID)
        val query = dsl.selectFrom(join)
            .where(ARTWORK_TAG.TAG_ID.eq(tagRecord.get(TAG.ID)))

        val totalCount = dsl.fetchCount(query)
        val artworks = query.orderBy(sort).limit(25).offset(offset).fetch()
        val cards = artworks.toGridCards(minio)

        val ctx = mutableMapOf<String, Any>(
            "tag" to tagRecord.get(TAG.ORIGINAL_NAME),
            "translated_name" to tagRecord.get(TAG.TRANSLATED_NAME)
        )
        val grid = Grid("tags")
        return grid.render(ctx, offset, totalCount, sortMode, *cards.toTypedArray())
    }
}
