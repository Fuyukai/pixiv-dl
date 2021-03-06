/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db.tables.records;


import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

import tf.sailor.pixivdl.db.tables.Bookmark;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BookmarkRecord extends UpdatableRecordImpl<BookmarkRecord> implements Record3<Integer, Integer, Boolean> {

    private static final long serialVersionUID = -2110875098;

    /**
     * Setter for <code>public.bookmark.bookmarker_id</code>.
     */
    public void setBookmarkerId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.bookmark.bookmarker_id</code>.
     */
    @NotNull
    public Integer getBookmarkerId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.bookmark.artwork_id</code>.
     */
    public void setArtworkId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.bookmark.artwork_id</code>.
     */
    @NotNull
    public Integer getArtworkId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.bookmark.is_private</code>.
     */
    public void setIsPrivate(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.bookmark.is_private</code>.
     */
    public Boolean getIsPrivate() {
        return (Boolean) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Integer, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Boolean> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, Integer, Boolean> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Bookmark.BOOKMARK.BOOKMARKER_ID;
    }

    @Override
    public Field<Integer> field2() {
        return Bookmark.BOOKMARK.ARTWORK_ID;
    }

    @Override
    public Field<Boolean> field3() {
        return Bookmark.BOOKMARK.IS_PRIVATE;
    }

    @Override
    public Integer component1() {
        return getBookmarkerId();
    }

    @Override
    public Integer component2() {
        return getArtworkId();
    }

    @Override
    public Boolean component3() {
        return getIsPrivate();
    }

    @Override
    public Integer value1() {
        return getBookmarkerId();
    }

    @Override
    public Integer value2() {
        return getArtworkId();
    }

    @Override
    public Boolean value3() {
        return getIsPrivate();
    }

    @Override
    public BookmarkRecord value1(Integer value) {
        setBookmarkerId(value);
        return this;
    }

    @Override
    public BookmarkRecord value2(Integer value) {
        setArtworkId(value);
        return this;
    }

    @Override
    public BookmarkRecord value3(Boolean value) {
        setIsPrivate(value);
        return this;
    }

    @Override
    public BookmarkRecord values(Integer value1, Integer value2, Boolean value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BookmarkRecord
     */
    public BookmarkRecord() {
        super(Bookmark.BOOKMARK);
    }

    /**
     * Create a detached, initialised BookmarkRecord
     */
    public BookmarkRecord(Integer bookmarkerId, Integer artworkId, Boolean isPrivate) {
        super(Bookmark.BOOKMARK);

        set(0, bookmarkerId);
        set(1, artworkId);
        set(2, isPrivate);
    }
}
