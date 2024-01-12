DROP TABLE IF EXISTS news_tag;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS tag;

CREATE TABLE IF NOT EXISTS author
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP           NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS news
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    author_id  BIGINT       REFERENCES author (id) ON DELETE SET NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS news_tag
(
    news_id BIGINT NOT NULL REFERENCES news (id) ON DELETE CASCADE,
    tag_id  BIGINT NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (news_id, tag_id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id         BIGSERIAL PRIMARY KEY,
    content    VARCHAR(255) NOT NULL,
    news_id    BIGINT REFERENCES news (id) ON DELETE CASCADE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);