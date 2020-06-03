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

package tf.sailor.pixivdl.web

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import org.jooq.DSLContext
import org.jooq.Select
import org.jooq.SelectOptionStep
import tf.sailor.pixivdl.JsonError
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.papi.BookmarkType
import tf.sailor.pixivdl.scraper.ScraperJobHandler
import javax.inject.Inject

/**
 * The controller for scraping jobs.
 */
@Controller("/api/scraping")
@Produces("application/json")
class ScraperJobController {
    @Inject
    lateinit var scraper: ScraperJobHandler

    @Inject
    lateinit var dsl: DSLContext

    /**
     * Gets job data.
     */
    @Produces("application/json")
    @Get("/jobs")
    fun getJobs(): JobList {
        return JobList(
            scraper.runningJobs.map { it.toInfo() },
            scraper.finishedJobs.asMap().values.map { it.toInfo() }
        )
    }

    /**
     * Starts a public bookmark download job.
     */
    @Produces("application/json")
    @Post("/jobs/bookmarks/public")
    fun startPublicBookmarks(
        @QueryValue("who_for") whoFor: Int,
        @QueryValue("who_with", defaultValue = "0") whoWith: Int
    ): HttpResponse<*> {
        val result = if (whoWith <= 0) {
            dsl.selectFrom(PIXIV_LOGIN).limit(1).fetch()
        } else {
            dsl.selectFrom(PIXIV_LOGIN).where(PIXIV_LOGIN.PIXIV_ID.eq(whoWith)).fetch()
        }

        if (result.isEmpty()) {
            return HttpResponse.badRequest(JsonError("No valid user to query with"))
        }

        val auth = result.first()
        val job = scraper.startBookmarkDownload(auth, whoFor, BookmarkType.PUBLIC)
        return HttpResponse.created(job.toInfo())
    }

    /**
     * Starts a new private bookmark scraping job.
     */
    @Produces("application/json")
    @Post("/jobs/bookmarks/private")
    fun startPrivateJob(@QueryValue("who_for") whoFor: Int): HttpResponse<*> {
        val rset = dsl
            .selectFrom(PIXIV_LOGIN)
            .where(PIXIV_LOGIN.PIXIV_ID.eq(whoFor))
            .fetch()

        if (rset.isEmpty()) {
            return HttpResponse.badRequest(
                JsonError("Cannot fetch private bookmarks for other users")
            )
        }

        val auth = rset.first()
        val job = scraper.startBookmarkDownload(auth, whoFor, BookmarkType.PRIVATE)
        return HttpResponse.created(job.toInfo())
    }
}
