-- V3: 뉴스 테이블 생성 (개선된 컬럼명 적용)

-- 뉴스 메인 테이블
CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    news_source VARCHAR(50) NOT NULL,
    source_sequence_id BIGINT NOT NULL,
    news_type VARCHAR(50),
    region VARCHAR(50),
    title VARCHAR(500) NOT NULL,
    plain_text_content TEXT,
    markdown_content TEXT,
    thumbnail VARCHAR(500),
    sentiment VARCHAR(50),
    source_url VARCHAR(1000) NOT NULL,
    create_epoch_millis BIGINT NOT NULL,
    update_epoch_millis BIGINT NOT NULL,
    change_value INTEGER,
    is_new BOOLEAN,
    is_headline BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_news_source_seq UNIQUE (news_source, source_sequence_id)
);

-- 뉴스 키워드 테이블
CREATE TABLE IF NOT EXISTS news_keyword (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

-- 뉴스 요약 테이블
CREATE TABLE IF NOT EXISTS news_summary (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    summary TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

-- 뉴스 인사이트 테이블
CREATE TABLE IF NOT EXISTS news_insight (
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    insight TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_news_source ON news(news_source);
CREATE INDEX IF NOT EXISTS idx_news_create_epoch ON news(create_epoch_millis DESC);
CREATE INDEX IF NOT EXISTS idx_news_type ON news(news_type);
CREATE INDEX IF NOT EXISTS idx_news_headline ON news(is_headline) WHERE is_headline = true;
CREATE INDEX IF NOT EXISTS idx_news_source_create_epoch ON news(news_source, create_epoch_millis DESC);

CREATE INDEX IF NOT EXISTS idx_keyword_news_id ON news_keyword(news_id);
CREATE INDEX IF NOT EXISTS idx_keyword_text ON news_keyword(keyword);

CREATE INDEX IF NOT EXISTS idx_summary_news_id ON news_summary(news_id);

CREATE INDEX IF NOT EXISTS idx_insight_news_id ON news_insight(news_id);

-- 테이블 및 컬럼 설명
COMMENT ON TABLE news IS '다중 소스 뉴스 통합 테이블 (블루밍비트, 코인니스)';
COMMENT ON COLUMN news.news_source IS '뉴스 소스 식별자 (예: BloomingBit, Coinness)';
COMMENT ON COLUMN news.source_sequence_id IS '원본 뉴스 소스에서 제공하는 시퀀스 ID';
COMMENT ON COLUMN news.create_epoch_millis IS '뉴스 생성 시각 (epoch 밀리초)';
COMMENT ON COLUMN news.update_epoch_millis IS '뉴스 수정 시각 (epoch 밀리초)';
COMMENT ON COLUMN news.is_headline IS '헤드라인 뉴스 여부';

COMMENT ON TABLE news_keyword IS '뉴스 키워드 테이블';
COMMENT ON TABLE news_summary IS '뉴스 요약 테이블';
COMMENT ON TABLE news_insight IS '뉴스 인사이트 테이블';
