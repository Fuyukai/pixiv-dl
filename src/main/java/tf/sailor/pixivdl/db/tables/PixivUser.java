/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
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

import tf.sailor.pixivdl.db.Keys;
import tf.sailor.pixivdl.db.Public;
import tf.sailor.pixivdl.db.tables.records.PixivUserRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PixivUser extends TableImpl<PixivUserRecord> {

    private static final long serialVersionUID = -1163739395;

    /**
     * The reference instance of <code>public.pixiv_user</code>
     */
    public static final PixivUser PIXIV_USER = new PixivUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PixivUserRecord> getRecordType() {
        return PixivUserRecord.class;
    }

    /**
     * The column <code>public.pixiv_user.id</code>.
     */
    public final TableField<PixivUserRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.pixiv_user.username</code>.
     */
    public final TableField<PixivUserRecord, String> USERNAME = createField(DSL.name("username"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.pixiv_user.display_name</code>.
     */
    public final TableField<PixivUserRecord, String> DISPLAY_NAME = createField(DSL.name("display_name"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>public.pixiv_user</code> table reference
     */
    public PixivUser() {
        this(DSL.name("pixiv_user"), null);
    }

    /**
     * Create an aliased <code>public.pixiv_user</code> table reference
     */
    public PixivUser(String alias) {
        this(DSL.name(alias), PIXIV_USER);
    }

    /**
     * Create an aliased <code>public.pixiv_user</code> table reference
     */
    public PixivUser(Name alias) {
        this(alias, PIXIV_USER);
    }

    private PixivUser(Name alias, Table<PixivUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private PixivUser(Name alias, Table<PixivUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> PixivUser(Table<O> child, ForeignKey<O, PixivUserRecord> key) {
        super(child, key, PIXIV_USER);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<PixivUserRecord> getPrimaryKey() {
        return Keys.PIXIV_USER_PKEY;
    }

    @Override
    public List<UniqueKey<PixivUserRecord>> getKeys() {
        return Arrays.<UniqueKey<PixivUserRecord>>asList(Keys.PIXIV_USER_PKEY, Keys.PIXIV_USER_USERNAME_KEY);
    }

    @Override
    public PixivUser as(String alias) {
        return new PixivUser(DSL.name(alias), this);
    }

    @Override
    public PixivUser as(Name alias) {
        return new PixivUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PixivUser rename(String name) {
        return new PixivUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PixivUser rename(Name name) {
        return new PixivUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
