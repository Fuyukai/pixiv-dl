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

package tf.sailor.pixivdl.papi.results

import com.fasterxml.jackson.annotation.JsonProperty
import tf.sailor.pixivdl.papi.structures.ApiIllustration

/**
 * Represents a paged list of illustrations.
 */
data class PagedIllustrations(
    /** The list of illustrations. */
    @param:JsonProperty("illusts") val illustrations: List<ApiIllustration>,
    /** The next URL to download. */
    override val nextUrl: String?
) : Pageable
