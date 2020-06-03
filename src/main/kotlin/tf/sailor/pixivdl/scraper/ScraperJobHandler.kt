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

package tf.sailor.pixivdl.scraper

import io.micronaut.caffeine.cache.Caffeine
import org.jooq.DSLContext
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.PIXIV_LOGIN
import tf.sailor.pixivdl.db.tables.records.PixivLoginRecord
import tf.sailor.pixivdl.papi.BookmarkType
import tf.sailor.pixivdl.papi.PixivApi
import tf.sailor.pixivdl.scraper.jobs.BookmarkDownloadJob
import tf.sailor.pixivdl.scraper.jobs.Job
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents the scraper job handler.
 *
 * This is responsible for spawning scraper jobs in threads and scraping them.
 */
@Singleton
class ScraperJobHandler {
    /** The SQL interface. */
    @Inject
    lateinit var context: DSLContext

    /** The minio client used for storing images. */
    @Inject
    lateinit var minioHandler: MinioHandler

    /** The deque of jobs. */
    val runningJobs = ArrayBlockingQueue<Job>(4)

    /** The mapping of finished jobs. */
    val finishedJobs = Caffeine.newBuilder()
        .maximumSize(10L)
        .build<UUID, Job>()

    /**
     * Called when a job is done.
     */
    fun doneCallback(job: Job) {
        runningJobs.remove(job)
        finishedJobs.put(job.uuid, job)
    }

    /**
     * Starts a new bookmark download job.
     */
    fun startBookmarkDownload(auth: PixivLoginRecord, for_: Int, type: BookmarkType): Job {
        // save some round-trips
        if (runningJobs.remainingCapacity() <= 0) {
            throw ScrapeError("Maximum number of concurrent jobs reached!")
        }

        val api = PixivApi.fromRefreshToken(auth.refreshToken)
        val job = BookmarkDownloadJob(type, for_, api, context, minioHandler)

        try {
            runningJobs.add(job)
            job.doneCallback = this::doneCallback
        } catch (e: IllegalStateException) {
            throw ScrapeError("Maximum number of concurrent jobs reached!")
        }

        job.start()
        return job
    }
}
