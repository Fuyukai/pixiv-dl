/*
 * This file is generated by jOOQ.
 */
package tf.sailor.pixivdl.db;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import tf.sailor.pixivdl.db.tables.Artwork;
import tf.sailor.pixivdl.db.tables.ArtworkTag;
import tf.sailor.pixivdl.db.tables.Bookmark;
import tf.sailor.pixivdl.db.tables.PixivLogin;
import tf.sailor.pixivdl.db.tables.PixivUser;
import tf.sailor.pixivdl.db.tables.Tag;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = -1070208917;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.artwork</code>.
     */
    public final Artwork ARTWORK = Artwork.ARTWORK;

    /**
     * The table <code>public.artwork_tag</code>.
     */
    public final ArtworkTag ARTWORK_TAG = ArtworkTag.ARTWORK_TAG;

    /**
     * The table <code>public.bookmark</code>.
     */
    public final Bookmark BOOKMARK = Bookmark.BOOKMARK;

    /**
     * The table <code>public.pixiv_login</code>.
     */
    public final PixivLogin PIXIV_LOGIN = PixivLogin.PIXIV_LOGIN;

    /**
     * The table <code>public.pixiv_user</code>.
     */
    public final PixivUser PIXIV_USER = PixivUser.PIXIV_USER;

    /**
     * The table <code>public.tag</code>.
     */
    public final Tag TAG = Tag.TAG;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.PIXIV_LOGIN_ID_SEQ,
            Sequences.TAG_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Artwork.ARTWORK,
            ArtworkTag.ARTWORK_TAG,
            Bookmark.BOOKMARK,
            PixivLogin.PIXIV_LOGIN,
            PixivUser.PIXIV_USER,
            Tag.TAG);
    }
}
