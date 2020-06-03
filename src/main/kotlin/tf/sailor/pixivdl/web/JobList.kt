package tf.sailor.pixivdl.web

import io.micronaut.core.annotation.Introspected
import tf.sailor.pixivdl.scraper.jobs.Job
import java.util.*

/**
 * Wrapper object for job into.
 */
@Introspected
data class JobList(val active: List<JobInfo>, val finished: List<JobInfo>) {
    /** Individual job info. */
    @Introspected
    data class JobInfo(
        val uuid: UUID, val title: String,
        val status: Job.State,
        val totalDownloads: Int, val currentDownloads: Int,
        val progress: Double
    )
}

/**
 * Turns a job into an info.
 */
fun Job.toInfo(): JobList.JobInfo = JobList.JobInfo(
    uuid, title, state,
    maxDownloads, currentDownloads.get(),
    progress
)
