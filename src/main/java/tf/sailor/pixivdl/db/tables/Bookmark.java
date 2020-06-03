/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import tf.sailor.pixivdl.db.Indexes;
import tf.sailor.pixivdl.db.Keys;
import tf.sailor.pixivdl.db.Public;
import tf.sailor.pixivdl.db.tables.records.BookmarkRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bookmark extends TableImpl<BookmarkRecord> {

    private static final long serialVersionUID = -90485380;

    /**
     * The reference instance of <code>public.bookmark</code>
     */
    public static final Bookmark BOOKMARK = new Bookmark();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BookmarkRecord> getRecordType() {
        return BookmarkRecord.class;
    }

    /**
     * The column <code>public.bookmark.bookmarker_id</code>.
     */
    public final TableField<BookmarkRecord, Integer> BOOKMARKER_ID = createField(DSL.name("bookmarker_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.bookmark.artwork_id</code>.
     */
    public final TableField<BookmarkRecord, Integer> ARTWORK_ID = createField(DSL.name("artwork_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.bookmark.is_private</code>.
     */
    public final TableField<BookmarkRecord, Boolean> IS_PRIVATE = createField(DSL.name("is_private"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>public.bookmark</code> table reference
     */
    public Bookmark() {
        this(DSL.name("bookmark"), null);
    }

    /**
     * Create an aliased <code>public.bookmark</code> table reference
     */
    public Bookmark(String alias) {
        this(DSL.name(alias), BOOKMARK);
    }

    /**
     * Create an aliased <code>public.bookmark</code> table reference
     */
    public Bookmark(Name alias) {
        this(alias, BOOKMARK);
    }

    private Bookmark(Name alias, Table<BookmarkRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bookmark(Name alias, Table<BookmarkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Bookmark(Table<O> child, ForeignKey<O, BookmarkRecord> key) {
        super(child, key, BOOKMARK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IDX_BOOKMARK_IS_PRIVATE);
    }

    @Override
    public UniqueKey<BookmarkRecord> getPrimaryKey() {
        return Keys.BOOKMARK_PKEY;
    }

    @Override
    public List<UniqueKey<BookmarkRecord>> getKeys() {
        return Arrays.<UniqueKey<BookmarkRecord>>asList(Keys.BOOKMARK_PKEY);
    }

    @Override
    public List<ForeignKey<BookmarkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BookmarkRecord, ?>>asList(Keys.BOOKMARK__BOOKMARK_BOOKMARKER_ID_FKEY, Keys.BOOKMARK__BOOKMARK_ARTWORK_ID_FKEY);
    }

    public PixivLogin pixivLogin() {
        return new PixivLogin(this, Keys.BOOKMARK__BOOKMARK_BOOKMARKER_ID_FKEY);
    }

    public Artwork artwork() {
        return new Artwork(this, Keys.BOOKMARK__BOOKMARK_ARTWORK_ID_FKEY);
    }

    @Override
    public Bookmark as(String alias) {
        return new Bookmark(DSL.name(alias), this);
    }

    @Override
    public Bookmark as(Name alias) {
        return new Bookmark(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Bookmark rename(String name) {
        return new Bookmark(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bookmark rename(Name name) {
        return new Bookmark(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Boolean> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
