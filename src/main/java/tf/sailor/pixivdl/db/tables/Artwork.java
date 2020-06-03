/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db.tables;


import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
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
import tf.sailor.pixivdl.db.tables.records.ArtworkRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Artwork extends TableImpl<ArtworkRecord> {

    private static final long serialVersionUID = -2111746690;

    /**
     * The reference instance of <code>public.artwork</code>
     */
    public static final Artwork ARTWORK = new Artwork();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ArtworkRecord> getRecordType() {
        return ArtworkRecord.class;
    }

    /**
     * The column <code>public.artwork.id</code>.
     */
    public final TableField<ArtworkRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.artwork.author_id</code>.
     */
    public final TableField<ArtworkRecord, Integer> AUTHOR_ID = createField(DSL.name("author_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.artwork.title</code>.
     */
    public final TableField<ArtworkRecord, String> TITLE = createField(DSL.name("title"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.artwork.caption</code>.
     */
    public final TableField<ArtworkRecord, String> CAPTION = createField(DSL.name("caption"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.artwork.uploaded_at</code>.
     */
    public final TableField<ArtworkRecord, OffsetDateTime> UPLOADED_AT = createField(DSL.name("uploaded_at"), org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE.nullable(false), this, "");

    /**
     * The column <code>public.artwork.total_bookmarks</code>.
     */
    public final TableField<ArtworkRecord, Integer> TOTAL_BOOKMARKS = createField(DSL.name("total_bookmarks"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.artwork.total_views</code>.
     */
    public final TableField<ArtworkRecord, Integer> TOTAL_VIEWS = createField(DSL.name("total_views"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.artwork.page_count</code>.
     */
    public final TableField<ArtworkRecord, Integer> PAGE_COUNT = createField(DSL.name("page_count"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("1", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.artwork.r18</code>.
     */
    public final TableField<ArtworkRecord, Boolean> R18 = createField(DSL.name("r18"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.artwork.r18g</code>.
     */
    public final TableField<ArtworkRecord, Boolean> R18G = createField(DSL.name("r18g"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>public.artwork.lewd_level</code>.
     */
    public final TableField<ArtworkRecord, Integer> LEWD_LEVEL = createField(DSL.name("lewd_level"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("2", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * Create a <code>public.artwork</code> table reference
     */
    public Artwork() {
        this(DSL.name("artwork"), null);
    }

    /**
     * Create an aliased <code>public.artwork</code> table reference
     */
    public Artwork(String alias) {
        this(DSL.name(alias), ARTWORK);
    }

    /**
     * Create an aliased <code>public.artwork</code> table reference
     */
    public Artwork(Name alias) {
        this(alias, ARTWORK);
    }

    private Artwork(Name alias, Table<ArtworkRecord> aliased) {
        this(alias, aliased, null);
    }

    private Artwork(Name alias, Table<ArtworkRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Artwork(Table<O> child, ForeignKey<O, ArtworkRecord> key) {
        super(child, key, ARTWORK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IDX_ARTWORK_BOOKMARKS, Indexes.IDX_ARTWORK_CAPTION, Indexes.IDX_ARTWORK_LEWD_LEVEL, Indexes.IDX_ARTWORK_R18, Indexes.IDX_ARTWORK_TITLE, Indexes.IDX_ARTWORK_UPLOADED_AT, Indexes.IDX_ARTWORK_VIEWS);
    }

    @Override
    public UniqueKey<ArtworkRecord> getPrimaryKey() {
        return Keys.ARTWORK_PKEY;
    }

    @Override
    public List<UniqueKey<ArtworkRecord>> getKeys() {
        return Arrays.<UniqueKey<ArtworkRecord>>asList(Keys.ARTWORK_PKEY);
    }

    @Override
    public List<ForeignKey<ArtworkRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ArtworkRecord, ?>>asList(Keys.ARTWORK__ARTWORK_AUTHOR_ID_FKEY);
    }

    public PixivUser pixivUser() {
        return new PixivUser(this, Keys.ARTWORK__ARTWORK_AUTHOR_ID_FKEY);
    }

    @Override
    public Artwork as(String alias) {
        return new Artwork(DSL.name(alias), this);
    }

    @Override
    public Artwork as(Name alias) {
        return new Artwork(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Artwork rename(String name) {
        return new Artwork(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Artwork rename(Name name) {
        return new Artwork(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, Integer, String, String, OffsetDateTime, Integer, Integer, Integer, Boolean, Boolean, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
