-- Initial DB schema.

-- Login details for a pixiv account.
create table pixiv_login (
    id serial primary key,
    pixiv_id int unique not null,

    refresh_token text not null
);

-- A specifix pixiv user who has artwork.
create table pixiv_user (
    id int primary key,

    username text not null unique,
    display_name text null
);

-- A specific tag.
create table tag (
    id serial primary key,

    original_name text not null unique,
    translated_name text null
);

create index idx_tag_translated_name on tag (translated_name);

-- A singular artwork.
create table artwork (
    id int primary key,
    author_id int not null references pixiv_user (id) on delete cascade,

    title text not null,
    caption text null,
    uploaded_at timestamptz not null,

    total_bookmarks int not null default 0,
    total_views int not null default 0,

    page_count int not null default 1,

    r18 boolean not null default false,
    r18g boolean not null default false,
    lewd_level integer not null default 2
);

--- Artwork indexes
create index idx_artwork_title on artwork (title);
create index idx_artwork_caption on artwork (caption);
create index idx_artwork_uploaded_at on artwork (uploaded_at);
create index idx_artwork_bookmarks on artwork (total_bookmarks);
create index idx_artwork_views on artwork (total_views);
create index idx_artwork_r18 on artwork (r18);
create index idx_artwork_lewd_level on artwork (lewd_level);

-- Association table between artworks and tags.
create table artwork_tag (
    artwork_id int not null references artwork (id) on delete cascade,
    tag_id int not null references tag (id) on delete cascade,
    primary key (artwork_id, tag_id)
);

create index fkey_artwork_tag_artwork_idx on artwork_tag (artwork_id);
create index fkey_artwork_tag_tag_id on artwork_tag (tag_id);

-- Bookmark info.
create table bookmark (
    bookmarker_id int not null references pixiv_login (pixiv_id) on delete cascade,
    artwork_id int not null references artwork (id) on delete cascade,
    is_private boolean not null default false,

    primary key (bookmarker_id, artwork_id)
);

create index idx_bookmark_is_private on bookmark (artwork_id);
