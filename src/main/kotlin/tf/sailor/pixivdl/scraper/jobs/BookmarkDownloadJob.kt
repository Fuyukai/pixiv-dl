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

package tf.sailor.pixivdl.scraper.jobs

import org.jooq.DSLContext
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.BOOKMARK
import tf.sailor.pixivdl.papi.BookmarkType
import tf.sailor.pixivdl.papi.PixivApi
import tf.sailor.pixivdl.papi.depaginate
import tf.sailor.pixivdl.scraper.DLArtworkImage
import tf.sailor.pixivdl.scraper.DownloadableImage

/**
 * Represents a bookmark download job, downloading the bookmarks of a specified user.
 *
 * @param type: The type of bookmarks to download.
 * @param for_: The user ID to download bookmarks of.
 */
class BookmarkDownloadJob(
    val type: BookmarkType,
    val for_: Int,
    api: PixivApi,
    dsl: DSLContext,
    client: MinioHandler
) : Job(api, dsl, client) {
    override val title: String = "Bookmark download ${type.name} for $for_"

    override fun getPages(): Collection<DownloadableImage> {
        val bookmarks = api.depaginate {
            userBookmarks(for_, type = type)
        }.flatMap { it.illustrations }

        val pages = mutableListOf<DownloadableImage>()
        for (bm in bookmarks) {
            bm.store()

            // insert bookmark record, if appropriate
            if (for_ == api.loginId) {
                val bookmarkRecord = sharedDSL.newRecord(BOOKMARK).also {
                    it.artworkId = bm.id
                    it.bookmarkerId = api.loginId
                    it.isPrivate = type == BookmarkType.PRIVATE
                }
                sharedDSL.upsert(bookmarkRecord)
            }


            for (page in bm.pages) {
                val dlpg = DLArtworkImage(bm.id, page.index, page.largestUrl)
                pages.add(dlpg)
            }
        }
        return pages
    }
}
