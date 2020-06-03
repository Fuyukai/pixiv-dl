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

package tf.sailor.pixivdl.papi

import org.apache.logging.log4j.LogManager
import tf.sailor.pixivdl.papi.results.Pageable
import tf.sailor.pixivdl.papi.results.PagedIllustrations
import tf.sailor.pixivdl.papi.structures.ApiExtendedUser
import tf.sailor.pixivdl.papi.structures.ApiIllustration
import java.io.InputStream

/**
 * Represents the pixiv API interface.
 *
 * This should be created as-needed, rather than sharing a singleton instance.
 */
interface PixivApi {
    companion object {
        /**
         * Creates a new scraper implementation from a username and password.
         */
        fun fromUsernamePassword(username: String, password: String): PixivApi {
            val impl = PixivApiImpl(null, null)
            impl.login(username, password)
            return impl
        }

        /**
         * Creates a new scraper implementation from a refresh token.
         */
        fun fromRefreshToken(token: String): PixivApi {
            val impl = PixivApiImpl(token, null)
            impl.login()
            return impl
        }
    }

    /**
     * Defines the Accept-Language header. Used for translated tags.
     */
    var acceptLanguage: String

    var loginId: Int

    /**
     * Gets the user details for the specified userid.
     */
    fun userDetails(id: Int): ApiExtendedUser

    /**
     * Gets the illustration details for the specified ID.
     */
    fun illustDetails(id: Int): ApiIllustration

    /**
     * Downloads a user's illustrations.
     *
     * @param id: The ID of the user.
     * @param type: The type of the illustration.
     * @param offset: The offset after which to download.
     */
    fun userIllustrations(
        id: Int,
        type: IllustrationType? = null,
        offset: Int = 0
    ): PagedIllustrations

    /**
     * Downloads a user's bookmarks.
     *
     * @param id: The ID of the user.
     * @param type: The type of the bookmarks.
     * @param maxId: The maximum bookmark ID.
     */
    fun userBookmarks(
        id: Int,
        type: BookmarkType = BookmarkType.PUBLIC,
        maxId: Int = 0,
        tag: String? = null
    ): PagedIllustrations

    /**
     * Gets illustrations from your following feed.
     */
    fun illustsFollowing(maxId: Int = 0): PagedIllustrations

    /**
     * Performs a CDN download.
     */
    fun downloadCdn(url: String, block: (InputStream) -> Unit)

    /**
     * Gets the next page of the specified [Pageable].
     */
    fun <T : Pageable> nextPage(pageable: T?): T?
}

/**
 * Depaginates a response. This will automatically fetch each subsequent page from the initial
 * response.
 */
fun <T : Pageable> PixivApi.depaginate(block: PixivApi.() -> T): List<T> {
    val logger = LogManager.getLogger(PixivApi::class.java)

    val initial = block()
    val results = mutableListOf(initial)

    var currentPage = initial
    while (true) {
        logger.debug("Fetching next page ${currentPage.nextUrl}")
        currentPage = nextPage(currentPage) ?: break
        results.add(currentPage)
    }

    return results
}
