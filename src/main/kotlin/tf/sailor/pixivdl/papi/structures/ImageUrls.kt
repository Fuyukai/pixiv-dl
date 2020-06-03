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

/** Used for image URLs. */
data class ImageUrls(
    val large: String?,
    val medium: String?,
    val original: String?,
    val squareMedium: String?
) {
    /**
     * Gets the largest image URL for this image.
     */
    val largestUrl: String
        @JsonIgnore get() {
            return original ?: large ?: medium ?: squareMedium
            ?: error("This image has no URLS!")
        }
}
