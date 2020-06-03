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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents an extended user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiExtendedUser(
    /** The core user details for this user. */
    @param:JsonProperty("user") val userDetails: ApiUser,
    @param:JsonProperty("profile") val profile: ApiUserProfile
) {
    /**
     * Represents a user's profile.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ApiUserProfile(
        /** The user's twitter account. */
        val twitterAccount: String?,
        /** The user's twitter URL. */
        val twiterUrl: String?
    )
}
