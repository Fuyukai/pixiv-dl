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

package tf.sailor.pixivdl.papi.structures

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Represents a single image page.
 *
 * @param index: The index of this page.
 * @param urls: The URL of this page.
 */
data class ImagePage(val index: Int, val urls: ImageUrls) {
    /** The largest image URL for this page. */
    val largestUrl: String
        @JsonIgnore get() = urls.largestUrl
}
