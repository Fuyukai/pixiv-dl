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

@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package tf.sailor.pixivdl.scraper.jobs

import java.util.*
import java.util.concurrent.Executors
import org.apache.logging.log4j.LogManager
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.jooq.impl.DSL
import tf.sailor.pixivdl.MinioHandler
import tf.sailor.pixivdl.db.Tables.*
import tf.sailor.pixivdl.db.tables.records.ArtworkRecord
import tf.sailor.pixivdl.db.tables.records.PixivUserRecord
import tf.sailor.pixivdl.db.tables.records.TagRecord
import tf.sailor.pixivdl.papi.PixivApi
import tf.sailor.pixivdl.papi.structures.ApiIllustration
import tf.sailor.pixivdl.papi.structures.ApiTag
import tf.sailor.pixivdl.papi.structures.ApiUser
import tf.sailor.pixivdl.scraper.DLArtworkImage
import tf.sailor.pixivdl.scraper.DownloadableImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a single job. This should be inherited by specific jobs.
 */
abstract class Job(
    val api: PixivApi,
    private val _globalDsl: DSLContext,
    val minio: MinioHandler
) : Thread() {
    enum class State {
        NOT_RUNNING,
        DL_METADATA,
        DL_IMAGES,
        FINISHED,
        ERRORED
    }

    /** The state for this job. */
    var state: State =
        State.NOT_RUNNING

    companion object {
        /**
         * The executor used to download images.
         *
         * This must EXCLUSIVELY be used to download
         */
        protected val EXECUTOR = Executors.newFixedThreadPool(4)

        // this is truly one of the most cursed generic type signatures ive ever seen
        /**
        * Upserts a record into the DB.
        */
        fun <T : UpdatableRecord<T>> DSLContext.upsert(record: UpdatableRecord<T>): Int {
            // https://stackoverflow.com/a/39666237
            return insertInto(record.table)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()
        }
    }

    /** The shared DSL context. Ran within a transaction. */
    protected lateinit var sharedDSL: DSLContext

    /** The callback for when this job is done. */
    lateinit var doneCallback: (Job) -> Unit

    /** The ID for this job. */
    val uuid = UUID.randomUUID()

    /** The logger for this job. */
    protected val LOGGER = LogManager.getLogger()

    /** Progress counter info. */
    var maxDownloads: Int = 0
        protected set
    val currentDownloads = AtomicInteger(0)

    val progress: Double get() = (currentDownloads.toDouble() / maxDownloads.toDouble()) * 100

    /**
     * Stores a user in the database.
     */
    fun ApiUser.store(): PixivUserRecord {
        val record = sharedDSL.newRecord(PIXIV_USER).also {
            it.id = id
            it.username = username
            it.displayName = name
        }

        sharedDSL.upsert(record)
        return record
    }

    /**
     * Stores a tag in the database.
     */
    fun ApiTag.store(): TagRecord {
        // if a tag already exists, its updated with a new translation if needed
        // otherwise a new one is created fresh and stored

        val existingTag =
            sharedDSL
                .selectFrom(TAG)
                .where(TAG.ORIGINAL_NAME.eq(name))
                .fetch()

        return if (existingTag.isEmpty()) {
            sharedDSL.newRecord(TAG).also {
                it.originalName = name
                it.translatedName = name
                it.store()
            }
        } else {
            val first = existingTag.first()
            if (translatedName != null && translatedName != first.translatedName) {
                first.translatedName = translatedName
                first.store()
            }

            first
        }
    }

    /**
     * Stores a single artwork in the database.
     *
     * This will also update authors and tags.
     */
    fun ApiIllustration.store(): ArtworkRecord {
        val authorRecord = author.store()

        val record = sharedDSL.newRecord(ARTWORK).also {
            it.id = this.id
            it.authorId = authorRecord.id

            it.title = title
            it.caption = caption
            it.uploadedAt = this.creationDate.toOffsetDateTime()

            it.totalBookmarks = bookmarks
            it.totalViews = views
            it.pageCount = pageCount

            it.r18 = r18
            it.r18g = r18g
            it.lewdLevel = lewdLevel._value
        }
        // updates fields if the record already exists
        sharedDSL.upsert(record)

        // store all tags and create association entry
        for (tag in tags) {
            val tagRecord = tag.store()

            val assocRecord = sharedDSL.newRecord(ARTWORK_TAG).also {
                it.artworkId = this.id
                it.tagId = tagRecord.id
            }
            sharedDSL.upsert(assocRecord)
        }

        return record
    }

    /**
     * Gets the collection of pages to download.
     */
    abstract fun getPages(): Collection<DownloadableImage>

    /** The title for this job. */
    abstract val title: String

    /**
     * Performs an actual page download.
     */
    fun doPageDownload(page: DownloadableImage) {
        if (page is DLArtworkImage) {
            if (minio.pageDownloaded(page.artworkId, page.page)) {
                LOGGER.info("Skipping download of $page as it is already downloaded")
            } else {
                LOGGER.info("Downloading artwork ${page.artworkId} page #${page.page + 1}")
                api.downloadCdn(page.url) {
                    LOGGER.debug("Uploading artwork to Minio...")
                    minio.uploadArtwork(page.artworkId, page.page, it)
                }
            }
            val current = currentDownloads.addAndGet(1)
            LOGGER.info("Success! ${maxDownloads - current} to go.")
        }
    }

    fun doRun() {
        // 1) Download the metadata
        state = State.DL_METADATA
        LOGGER.info("Starting job: `$title`")
        LOGGER.info("Downloading metadata...")
        val pages = try {
            _globalDsl.transactionResult { it ->
                sharedDSL = DSL.using(it)
                getPages()
            }
        } catch (e: Exception) {
            LOGGER.error("Job encountered error during metadata download!", e)
            state = State.ERRORED
            return
        }

        // 2) Download all of the pages
        maxDownloads = pages.size
        currentDownloads.set(0)

        LOGGER.info("Downloading $maxDownloads images.")
        state = State.DL_IMAGES
        val futures = pages.map { EXECUTOR.submit { doPageDownload(it) } }
        LOGGER.info("Waiting for all futures to complete...")

        for (fut in futures) {
            try {
                fut.get()
            } catch (e: ExecutionException) {
                LOGGER.warn("Future $fut threw an error!", e)
            }
        }

        LOGGER.info("Job completed.")

        state = State.FINISHED
    }

    override fun run() {
        try {
            doRun()
        } finally {
            doneCallback.invoke(this)
        }
    }
}
