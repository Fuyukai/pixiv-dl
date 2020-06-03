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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a user from the Pixiv API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiUser(
    /** The ID of this user. */
    val id: Int,
    /** The username of this user. */
    @JsonProperty("account") val username: String,
    /** The display name of this user. */
    val name: String,
    /** The comment of this user, if any. */
    val comment: String?,
    /** The profile image URLs for this user. */
    @JsonProperty("profile_image_urls") val profileUrls: ImageUrls?
) {
    /** The profile URL for this user. */
    val pixivUrl: String @JsonIgnore get() = "https://pixiv.net/users/$id"
}
