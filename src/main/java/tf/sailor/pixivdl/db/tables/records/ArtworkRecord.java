/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db.tables.records;


import java.time.OffsetDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;

import tf.sailor.pixivdl.db.tables.Artwork;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ArtworkRecord extends UpdatableRecordImpl<ArtworkRecord> implements Record11<Integer, Integer, String, String, OffsetDateTime, Integer, Integer, Integer, Boolean, Boolean, Integer> {

    private static final long serialVersionUID = 988472831;

    /**
     * Setter for <code>public.artwork.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.artwork.id</code>.
     */
    @NotNull
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.artwork.author_id</code>.
     */
    public void setAuthorId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.artwork.author_id</code>.
     */
    @NotNull
    public Integer getAuthorId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.artwork.title</code>.
     */
    public void setTitle(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.artwork.title</code>.
     */
    @NotNull
    public String getTitle() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.artwork.caption</code>.
     */
    public void setCaption(@Nullable String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.artwork.caption</code>.
     */
    @Nullable
    public String getCaption() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.artwork.uploaded_at</code>.
     */
    public void setUploadedAt(OffsetDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.artwork.uploaded_at</code>.
     */
    @NotNull
    public OffsetDateTime getUploadedAt() {
        return (OffsetDateTime) get(4);
    }

    /**
     * Setter for <code>public.artwork.total_bookmarks</code>.
     */
    public void setTotalBookmarks(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.artwork.total_bookmarks</code>.
     */
    public Integer getTotalBookmarks() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>public.artwork.total_views</code>.
     */
    public void setTotalViews(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.artwork.total_views</code>.
     */
    public Integer getTotalViews() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>public.artwork.page_count</code>.
     */
    public void setPageCount(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.artwork.page_count</code>.
     */
    public Integer getPageCount() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>public.artwork.r18</code>.
     */
    public void setR18(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.artwork.r18</code>.
     */
    public Boolean getR18() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>public.artwork.r18g</code>.
     */
    public void setR18g(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.artwork.r18g</code>.
     */
    public Boolean getR18g() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>public.artwork.lewd_level</code>.
     */
    public void setLewdLevel(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.artwork.lewd_level</code>.
     */
    public Integer getLewdLevel() {
        return (Integer) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, Integer, String, String, OffsetDateTime, Integer, Integer, Integer, Boolean, Boolean, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Integer, Integer, String, String, OffsetDateTime, Integer, Integer, Integer, Boolean, Boolean, Integer> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Artwork.ARTWORK.ID;
    }

    @Override
    public Field<Integer> field2() {
        return Artwork.ARTWORK.AUTHOR_ID;
    }

    @Override
    public Field<String> field3() {
        return Artwork.ARTWORK.TITLE;
    }

    @Override
    public Field<String> field4() {
        return Artwork.ARTWORK.CAPTION;
    }

    @Override
    public Field<OffsetDateTime> field5() {
        return Artwork.ARTWORK.UPLOADED_AT;
    }

    @Override
    public Field<Integer> field6() {
        return Artwork.ARTWORK.TOTAL_BOOKMARKS;
    }

    @Override
    public Field<Integer> field7() {
        return Artwork.ARTWORK.TOTAL_VIEWS;
    }

    @Override
    public Field<Integer> field8() {
        return Artwork.ARTWORK.PAGE_COUNT;
    }

    @Override
    public Field<Boolean> field9() {
        return Artwork.ARTWORK.R18;
    }

    @Override
    public Field<Boolean> field10() {
        return Artwork.ARTWORK.R18G;
    }

    @Override
    public Field<Integer> field11() {
        return Artwork.ARTWORK.LEWD_LEVEL;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getAuthorId();
    }

    @Override
    public String component3() {
        return getTitle();
    }

    @Override
    @Nullable
    public String component4() {
        return getCaption();
    }

    @Override
    public OffsetDateTime component5() {
        return getUploadedAt();
    }

    @Override
    public Integer component6() {
        return getTotalBookmarks();
    }

    @Override
    public Integer component7() {
        return getTotalViews();
    }

    @Override
    public Integer component8() {
        return getPageCount();
    }

    @Override
    public Boolean component9() {
        return getR18();
    }

    @Override
    public Boolean component10() {
        return getR18g();
    }

    @Override
    public Integer component11() {
        return getLewdLevel();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getAuthorId();
    }

    @Override
    public String value3() {
        return getTitle();
    }

    @Override
    @Nullable
    public String value4() {
        return getCaption();
    }

    @Override
    public OffsetDateTime value5() {
        return getUploadedAt();
    }

    @Override
    public Integer value6() {
        return getTotalBookmarks();
    }

    @Override
    public Integer value7() {
        return getTotalViews();
    }

    @Override
    public Integer value8() {
        return getPageCount();
    }

    @Override
    public Boolean value9() {
        return getR18();
    }

    @Override
    public Boolean value10() {
        return getR18g();
    }

    @Override
    public Integer value11() {
        return getLewdLevel();
    }

    @Override
    public ArtworkRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public ArtworkRecord value2(Integer value) {
        setAuthorId(value);
        return this;
    }

    @Override
    public ArtworkRecord value3(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public ArtworkRecord value4(@Nullable String value) {
        setCaption(value);
        return this;
    }

    @Override
    public ArtworkRecord value5(OffsetDateTime value) {
        setUploadedAt(value);
        return this;
    }

    @Override
    public ArtworkRecord value6(Integer value) {
        setTotalBookmarks(value);
        return this;
    }

    @Override
    public ArtworkRecord value7(Integer value) {
        setTotalViews(value);
        return this;
    }

    @Override
    public ArtworkRecord value8(Integer value) {
        setPageCount(value);
        return this;
    }

    @Override
    public ArtworkRecord value9(Boolean value) {
        setR18(value);
        return this;
    }

    @Override
    public ArtworkRecord value10(Boolean value) {
        setR18g(value);
        return this;
    }

    @Override
    public ArtworkRecord value11(Integer value) {
        setLewdLevel(value);
        return this;
    }

    @Override
    public ArtworkRecord values(Integer value1, Integer value2, String value3, @Nullable String value4, OffsetDateTime value5, Integer value6, Integer value7, Integer value8, Boolean value9, Boolean value10, Integer value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ArtworkRecord
     */
    public ArtworkRecord() {
        super(Artwork.ARTWORK);
    }

    /**
     * Create a detached, initialised ArtworkRecord
     */
    public ArtworkRecord(Integer id, Integer authorId, String title, @Nullable String caption, OffsetDateTime uploadedAt, Integer totalBookmarks, Integer totalViews, Integer pageCount, Boolean r18, Boolean r18g, Integer lewdLevel) {
        super(Artwork.ARTWORK);

        set(0, id);
        set(1, authorId);
        set(2, title);
        set(3, caption);
        set(4, uploadedAt);
        set(5, totalBookmarks);
        set(6, totalViews);
        set(7, pageCount);
        set(8, r18);
        set(9, r18g);
        set(10, lewdLevel);
    }
}
