package tf.sailor.pixivdl.web

import io.micronaut.core.annotation.Introspected
import org.jooq.SortField
import org.jooq.TableField
import tf.sailor.pixivdl.db.Tables.ARTWORK

/**
 * Represents a sorting mode for views.
 */
@Introspected
enum class SortMode {
    ASCENDING {
        override fun <T> toSortMode(field: TableField<*, T>): SortField<T> {
            return field.asc()
        }
    },
    DESCENDING {
        override fun <T> toSortMode(field: TableField<*, T>): SortField<T> {
            return field.desc()
        }
    };

    abstract fun <T> toSortMode(field: TableField<*, T>): SortField<T>
}
