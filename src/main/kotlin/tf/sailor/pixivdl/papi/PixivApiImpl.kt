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

@file:Suppress("MemberVisibilityCanBePrivate")

package tf.sailor.pixivdl.papi

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.treeToValue
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.LogManager
import tf.sailor.pixivdl.papi.results.Pageable
import tf.sailor.pixivdl.papi.results.PagedIllustrations
import tf.sailor.pixivdl.papi.structures.ApiExtendedUser
import tf.sailor.pixivdl.papi.structures.ApiIllustration
import java.io.InputStream

/**
 * Represents a single pixiv scraper.
 *
 * @param refreshToken: The refresh token used for this scraper.
 * @param accessToken: The access token used, if any. A value of `null` will cause a fresh one to
 *      be looked up.
 *
 * It is recommended to use a factory method instead.
 */
class PixivApiImpl(var refreshToken: String? = null, var accessToken: String? = null) : PixivApi {
    companion object {
        const val CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
        const val CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
        const val HASH_SECRET = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"

        val pixivHost = "app-api.pixiv.net"

        val LOGGER = LogManager.getLogger()

        /** Shared object mapper. */
        val MAPPER = ObjectMapper(JsonFactory()).apply {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            findAndRegisterModules()
        }

        /**
         * Gets a built pixiv URL using the specified block for building.
         */
        fun pixivUrl(block: HttpUrl.Builder.() -> Unit): HttpUrl {
            val builder = HttpUrl.Builder().scheme("https").host(pixivHost)
            builder.block()
            return builder.build()
        }
    }

    /** The user ID this client is connected to. */
    override var loginId: Int by Delegates.notNull()

    override fun toString(): String {
        return "<PixivApi for $loginId>"
    }

    /** The shared HTTP client for all requests. */
    val client = OkHttpClient()

    /**
     * Logs in using a refresh token.
     */
    fun login() {
        val token = refreshToken
        check(token != null) { "No refresh token provided!" }
        val body = mutableMapOf<String, Any>(
            "grant_type" to "refresh_token",
            "refresh_token" to token
        )
        return auth(body)
    }

    /**
     * Logs in using a username and a password.
     */
    fun login(username: String, password: String) {
        val body: MutableMap<String, Any> = mutableMapOf(
            "grant_type" to "password",
            "username" to username,
            "password" to password
        )
        return auth(body)
    }

    /**
     * Sends an authentication request to Pixiv.
     */
    fun auth(body: MutableMap<String, Any>) {
        require("grant_type" in body) { "Missing 'grant_type' in auth body!" }

        // weird time shenanigans...
        // %Y-%m-%dT%H:%M:%S+00:00
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+00:00")
        val localTime = LocalDateTime.now(ZoneOffset.UTC)
        val localTimeStr = localTime.format(formatter)
        val hexDigest = DigestUtils.md5Hex(localTimeStr + HASH_SECRET)

        val headers = Headers.headersOf(
            "User-Agent", "PixivAndroidApp/5.0.64 (Android 6.0)",
            "X-Client-Time", localTimeStr,
            "X-Client-Hash", hexDigest
        )

        // make sure body has ""our"" oauth2 info
        body["get_secure_url"] = 1
        body["client_id"] = CLIENT_ID
        body["client_secret"] = CLIENT_SECRET

        val endpoint = "https://oauth.secure.pixiv.net/auth/token"
        val requestBody = FormBody.Builder().apply {
            body.forEach { add(it.key, it.value.toString()) }
        }.build()

        val request = Request.Builder().apply {
            url(endpoint)
            headers(headers)
            post(requestBody)
        }.build()

        LOGGER.debug("Attempting to log into pixiv...")
        client.newCall(request).execute().use {
            val body = it.body?.string() ?: ""
            if (!it.isSuccessful) {
                LOGGER.fatal("Failed to log into Pixiv!")
                throw AuthFailedException(body)
            }

            val mapped = MAPPER.readValue(body, JsonNode::class.java)
            val realResponse = mapped.get("response")

            val user = realResponse["user"]
            assert(user != null) { "Response missing user key!" }

            val username = user["account"]
            assert(username != null) { "Response missing 'account' key!" }

            LOGGER.debug("Authorised scraper for $username")

            val id = user["id"]
            assert(id != null) { "Response missing id key!" }
            // for whatever reason, the encoded user ID is a string, rather than an int
            assert(id.isTextual) { "ID is not textual!" }
            loginId = id.asText().toInt()

            val at = realResponse["access_token"]
            assert(at != null) { "Response missing access token!" }
            accessToken = realResponse.get("access_token").asText()

            val rt = realResponse["refresh_token"]
            assert(rt != null) { "Response missing refresh token!" }
            refreshToken = realResponse.get("refresh_token").asText()
        }
    }

    override var acceptLanguage: String = "en-us"

    /**
     * Implements a retryable pixiv call.
     *
     * This only retries in the event of a network error. If a different error is hit, null is
     * returned.
     */
    fun retryablePixivCall(method: String, url: HttpUrl, body: RequestBody? = null): String? {
        val headers = Headers.headersOf(
            "App-OS", "ios",
            "App-OS-Version", "13.5",
            "App-Version", "7.8.23",
            "User-Agent", "PixivIOSApp/7.8.23 (iOS 13.5; iPhone11,2)",
            "Authorization", "Bearer ${accessToken!!}",
            "Accept-Language", acceptLanguage
        )

        val request = Request.Builder().apply {
            url(url)
            headers(headers)
            if (body != null) {
                method(method, body)
            }
        }.build()

        for (try_ in 0 until 5) {
            try {
                val logUrl = request.url.encodedPath + "?" + request.url.encodedQuery
                LOGGER.debug("${request.method} $logUrl - try ${try_ + 1} / 5")

                client.newCall(request).execute().use {
                    LOGGER.debug("${request.method} $logUrl - ${it.code}")
                    val body = it.body?.string() ?: ""
                    if (it.code in 200 until 300) {
                        LOGGER.debug("Sleeping for 2 seconds to avoid rate limits")
                        Thread.sleep(2000L)
                        return body
                    }

                    if (it.code == 403 && "rate limit" in body.toLowerCase()) {
                        LOGGER.warn("Hit rate limit, waiting 30 seconds")
                        Thread.sleep(30 * 1000L)
                    } else {
                        throw PixivApiException(if (body.isEmpty()) "Unknown error" else body)
                    }
                }
            } catch (e: IOException) {
                LOGGER.warn("Caught IOException $e")
            }
        }

        error("Request failed after 5 retries")
    }

    /**
     * Performs a pixiv API call. This handles authentication automatically.
     *
     * @param url: The [HttpUrl] to call.
     * @param body: The [RequestBody] to pass along, if any.
     * @param mapTo: The class to map the response to.
     */
    fun pixivCall(
        method: String,
        url: HttpUrl,
        body: RequestBody? = null
    ): JsonNode {
        if (accessToken == null) {
            refreshToken ?: error("This scraper has not been logged in!")
            login()
        }

        // first try is if auth is wrong, second try is with re-authed
        for (x in 0..1) {
            val result = retryablePixivCall(method, url, body)
            if (result == null) {
                LOGGER.debug("Auth token might be invalid, retrying...")
                login()
                continue
            }
            return MAPPER.readValue(result, JsonNode::class.java)
        }

        error("Failed to download...")
    }

    /**
     * Gets the user details for the specified id.
     */
    override fun userDetails(id: Int): ApiExtendedUser {
        val url = pixivUrl {
            encodedPath("/v1/user/detail")
            addQueryParameter("user_id", id.toString())
            addQueryParameter("filter", "for_ios")
        }

        return MAPPER.treeToValue(pixivCall("GET", url))!!
    }

    /**
     * Gets illustration details for the specified illustration.
     */
    override fun illustDetails(id: Int): ApiIllustration {
        val url = pixivUrl {
            encodedPath("/v1/illust/detail")
            addQueryParameter("illust_id", id.toString())
        }
        val node = pixivCall("GET", url)
        return MAPPER.treeToValue<ApiIllustration>(node["illust"])!!
    }

    /**
     * Gets a users illustrations.
     *
     * @param id: The ID of the user.
     * @param type: The type of illustrations to get.
     * @param offset: The offset to get after.
     */
    override fun userIllustrations(
        id: Int,
        type: IllustrationType?,
        offset: Int
    ): PagedIllustrations {
        val url = pixivUrl {
            encodedPath("/v1/user/illusts")

            addQueryParameter("user_id", id.toString())
            addQueryParameter("filter", "for_ios")

            if (type != null) {
                addQueryParameter("type", type.apiName)
            }

            addQueryParameter("offset", offset.toString())
        }
        val result = pixivCall("GET", url)
        return MAPPER.treeToValue(result)!!
    }

    /**
     * Gets a users bookmarks.
     */
    override fun userBookmarks(
        id: Int,
        type: BookmarkType,
        maxId: Int,
        tag: String?
    ): PagedIllustrations {
        val url = pixivUrl {
            encodedPath("/v1/user/bookmarks/illust")
            addQueryParameter("user_id", id.toString())
            addQueryParameter("restrict", type.apiName)
            addQueryParameter("filter", "for_ios")

            if (maxId >= 1) {
                addQueryParameter("max_bookmark_id", maxId.toString())
            }

            if (tag != null) {
                addQueryParameter("tag", tag)
            }
        }
        val result = pixivCall("GET", url)
        return MAPPER.treeToValue(result)!!
    }

    /**
     * Gets illustrations from your following feed.
     */
    override fun illustsFollowing(maxId: Int): PagedIllustrations {
        val url = pixivUrl {
            encodedPath("/v2/illust/follow")
            addQueryParameter("restrict", "public")

            if (maxId >= 0) {
                addQueryParameter("offset", maxId.toString())
            }
        }

        return MAPPER.treeToValue(pixivCall("GET", url))!!
    }

    override fun downloadCdn(url: String, block: (InputStream) -> Unit) {
        val url = url.toHttpUrl()
        val request = Request.Builder().apply {
            url(url)
            addHeader("Referer", "https://app-api.pixiv.net/")
            method("GET", null)
            addHeader(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64; rv:76.0) Gecko/20100101 Firefox/76.0"
            )
        }.build()

        client.newCall(request).execute().use {
            if (!it.isSuccessful) {
                throw PixivApiException("CDN download failed")
            }

            val body = it.body!!.byteStream()
            body.use(block)
        }
    }

    /**
     * Gets the next page of a pageable, or null if there is no further page.
     */
    override fun <T : Pageable> nextPage(pageable: T?): T? {
        val nextUrl = pageable?.nextUrl ?: return null
        val url = nextUrl.toHttpUrl()
        val response = pixivCall("GET", url)
        return MAPPER.treeToValue(response, pageable.javaClass)
    }
}
