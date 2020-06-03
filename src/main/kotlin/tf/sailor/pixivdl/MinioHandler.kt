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

package tf.sailor.pixivdl

import io.micronaut.context.annotation.Value
import io.minio.ErrorCode
import io.minio.MinioClient
import io.minio.PutObjectOptions
import io.minio.errors.ErrorResponseException
import org.apache.logging.log4j.LogManager
import java.io.*
import javax.imageio.ImageIO
import javax.inject.Singleton

/**
 * Minio upload helper.
 */
@Singleton
class MinioHandler {
    val LOGGER = LogManager.getLogger()

    // micronaut config annoyingness
    @Value("\${minio.address}")
    lateinit var minioAddress: String
    @Value("\${minio.access_key}")
    lateinit var minioAccessKey: String
    @Value("\${minio.secret_key}")
    lateinit var minioSecretKey: String

    /** The underlying minio client. */
    val client: MinioClient by lazy {
        MinioClient(minioAddress, minioAccessKey, minioSecretKey)
    }

    /**
     * Tests the Minio connection.
     */
    fun testConnection() {
        // deliberate no-op because this will then throw an error
        client.listBuckets()
    }

    /**
     * Checks if a specific page has been downloaded.
     */
    fun pageDownloaded(artwork: Int, page: Int): Boolean {
        val key = "page/$artwork/$page.png"
        try {
            client.statObject("artwork-page", key)
        } catch (e: ErrorResponseException) {
            if (e.errorResponse().errorCode() == ErrorCode.NO_SUCH_KEY) {
                return false
            }
            throw e
        }

        return true
    }

    /**
     * Uploads an artwork to object storage.
     */
    fun uploadArtwork(artwork: Int, page: Int, data: InputStream) {
        if (!client.bucketExists("artwork-page")) {
            client.makeBucket("artwork-page")
            client.setBucketPolicy("artwork-page", "public")
        }

        val key = "page/$artwork/$page.png"

        // read in and save as png
        // this will balloon memory usage for big things... but whatever.
        LOGGER.debug("Reading in raw image...")
        val image = ImageIO.read(data)

        // avoid copying the inner bytearray, so there will only be one around.
        val os = object : ByteArrayOutputStream() {
            override fun toByteArray(): ByteArray = buf
        }

        LOGGER.debug("Converting image to PNG...")
        ImageIO.write(image, "png", os)
        val ips = ByteArrayInputStream(os.toByteArray())
        val humanSize = os.size() / 1024
        val options = PutObjectOptions(os.size().toLong(), -1)

        client.putObject("artwork-page", key, ips, options)
        LOGGER.debug("Success! Uploaded `$key` ($humanSize KiB)")
    }

    /**
     * Gets an artwork page URL.
     *
     * @param artwork: The ID of the artwork.
     * @param page: The page of the artwork.
     */
    fun getArtworkPage(artwork: Int, page: Int): String {
        return client.getObjectUrl("artwork-page", "page/$artwork/$page.png")
    }
}
