package tf.sailor.pixivdl.web

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.mitchellbosecke.pebble.template.PebbleTemplate
import java.io.StringWriter

/**
 * Singleton holding the template renderer.
 */
object Templates {
    /** The Pebble engine. */
    val ENGINE = PebbleEngine.Builder().apply {
        val loader = ClasspathLoader(Templates::class.java.classLoader)
        loader.prefix = "templates/"
        this.loader(loader)
        strictVariables(true)
    }.build()

    /**
     * Renders a template to a string.
     */
    fun PebbleTemplate.render(context: Map<String, Any>): String {
        val writer = StringWriter()
        this.evaluate(writer, context)
        return writer.toString()
    }

    /**
     * Renders a template using the engine.
     *
     * @param name: The path of the template, without the ``template`` prefix.
     * @param context: The context for the template.
     * @return A string holding the rendered template.
     */
    fun render(name: String, context: Map<String, Any>): String {
        val template = ENGINE.getTemplate(name)!!
        return template.render(context)
    }
}
