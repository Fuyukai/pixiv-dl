package tf.sailor.pixivdl.web

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

/**
 * Static file ccontroller.
 */
@Controller("/static")
class StaticController {

    /**
     *
     */
    @Produces("text/css")
    @Get("/custom.css")
    fun getCustomCss(): HttpResponse<*> {
        val resource = StaticController::class.java
            .getResourceAsStream("/static/custom.css")

        check(resource != null) { "Jar is somehow missing custom.css" }
        val body = resource.reader(Charsets.UTF_8).readText()
        return HttpResponse.ok(body)
    }
}
