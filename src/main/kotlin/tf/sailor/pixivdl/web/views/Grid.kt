package tf.sailor.pixivdl.web.views

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.SelectConditionStep
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.web.SortMode
import tf.sailor.pixivdl.web.Templates
import java.lang.Exception

/**
 * Class representing a simple grid of artworks.
 *
 * This will display the
 */
class Grid(val name: String) {
    /**
     * Represents a grid card.
     *
     * @param artworkId: The ID of the artwork for this card.
     * @param artworkLink: The link for the artwork image.
     * @param artworkUrl: The URL for the artwork image.
     * @param r18: If the artwork for this card is R-18.
     * @param pageCount: The page count for this card.
     * @param caption: The caption for the card.
     * @param captionLink: The link for the artwork and the caption.
     * @param subtitle: The subtitle for the card.
     * @param subtitleUrl: The url for the subtitle, if any.
     * @param footer: The footer for this card, if any.
     */
    data class GridCard(
        val artworkId: Int,
        val artworkLink: String,
        val artworkUrl: String,
        val r18: Boolean,
        val pageCount: Int,
        val caption: String,
        val captionLink: String,
        val subtitle: String,
        val subtitleUrl: String? = null,
        val footer: String? = null
    )

    /**
     * Renders the grid.
     *
     * @param ctx: The template context to render with.
     * @param offset: The offset to use for pagination buttons.
     * @param totalCount: The total count to use for pagination buttons.
     * @param sortMode: The sort mode to use for pagination buttons.
     * @param query: A block that performs a jOOQ query and returns a list of records.
     *
     * The query should join the artwork_author table onto it.
     */
    fun render(
        ctx: MutableMap<String, Any>,
        offset: Int, totalCount: Int, sortMode: SortMode,
        vararg cards: GridCard
    ): String {
        ctx["offset"] = offset
        ctx["total_count"] = totalCount
        ctx["sortmode"] = sortMode.name.toLowerCase()
        ctx["cards"] = cards

        return Templates.render("grids/${name}_grid.html", ctx)
    }
}

/**
 * Turns a list of records into a list of grid cards.
 *
 * The query must have the artwork and pixiv_user joined onto it.
 *
 * This is designed for use within filtered grids.
 */
fun Result<Record>.toGridCards(minioHandler: MinioHandler): List<Grid.GridCard> {
    val cards = mutableListOf<Grid.GridCard>()

    for (artwork in this) {
        val artworkId = artwork.get(ARTWORK.ID)
        val artworkLink = "/views/artwork/${artworkId}"
        val artworkUrl = minioHandler.getArtworkPage(artworkId, 0)

        val authorId = artwork.get(PIXIV_USER.ID)
        val authorName = artwork.get(PIXIV_USER.DISPLAY_NAME)
        val authorLink = "/views/author/${authorId}"

        val r18 = artwork.get(ARTWORK.R18) || artwork.get(ARTWORK.R18G)
        val pageCount = artwork.get(ARTWORK.PAGE_COUNT)

        val card = Grid.GridCard(
            artworkId = artworkId,
            artworkLink = artworkLink,
            artworkUrl = artworkUrl,
            r18 = r18,
            pageCount = pageCount,
            caption = artwork.get(ARTWORK.TITLE),
            captionLink = artworkLink,
            subtitle = "by $authorName",
            subtitleUrl = authorLink,
            footer = "ID: $artworkId"
        )
        cards.add(card)
    }

    return cards
}
