-- 초기 스키마 생성 (Flyway V1 Migration)

-- 회원 역할 테이블
CREATE TABLE IF NOT EXISTS member_role (
    id BIGSERIAL PRIMARY KEY,
    role_key VARCHAR(255) NOT NULL UNIQUE,
    role_name VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 시드머니 범위 테이블
CREATE TABLE IF NOT EXISTS seed_money_range (
    id BIGSERIAL PRIMARY KEY,
    seed_range_key VARCHAR(255) NOT NULL UNIQUE,
    range VARCHAR(255) NOT NULL,
    rank VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 활동 등급 테이블
CREATE TABLE IF NOT EXISTS activity_rank (
    id BIGSERIAL PRIMARY KEY,
    rank_key VARCHAR(255) NOT NULL UNIQUE,
    grade VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 사용자 관련 테이블

-- 회원 기본 정보
CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    nickname VARCHAR(255) NOT NULL UNIQUE,
    role_id BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_role FOREIGN KEY (role_id) REFERENCES member_role(id)
);

-- OAuth 정보
CREATE TABLE IF NOT EXISTS oauth (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT,
    provider VARCHAR(255),
    provider_id VARCHAR(255),
    access_token VARCHAR(1000),
    refresh_token VARCHAR(1000),
    token_type VARCHAR(255),
    expires_in BIGINT,
    scope VARCHAR(500),
    expires_at TIMESTAMP,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oauth_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 유저 프로필
CREATE TABLE IF NOT EXISTS profile (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT,
    image_url VARCHAR(255),
    seed_range_key VARCHAR(255),
    activity_rank_key VARCHAR(255),
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_profile_seed_range FOREIGN KEY (seed_range_key) REFERENCES seed_money_range(seed_range_key),
    CONSTRAINT fk_profile_activity_rank FOREIGN KEY (activity_rank_key) REFERENCES activity_rank(rank_key)
);

-- 팔로우 관계
CREATE TABLE IF NOT EXISTS follow (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES member(id),
    CONSTRAINT fk_follow_following FOREIGN KEY (following_id) REFERENCES member(id),
    CONSTRAINT uk_follow_relation UNIQUE (follower_id, following_id)
);

-- 유저 에이전트 정보
CREATE TABLE IF NOT EXISTS member_agent (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT,
    ip VARCHAR(255),
    is_banned BOOLEAN DEFAULT false,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_agent FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 밴 카운트 정보
CREATE TABLE IF NOT EXISTS banned_count (
    id BIGSERIAL PRIMARY KEY,
    member_agent_id BIGINT,
    count INTEGER DEFAULT 0,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_banned_count_member_agent FOREIGN KEY (member_agent_id) REFERENCES member_agent(id)
);

-- 신고 테이블
CREATE TABLE IF NOT EXISTS declaration (
    id BIGSERIAL PRIMARY KEY,
    from_member VARCHAR(255) NOT NULL,
    from_member_ip VARCHAR(255),
    to_member VARCHAR(255) NOT NULL,
    reason TEXT,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 회원 탈퇴 정보
CREATE TABLE IF NOT EXISTS member_withdraw (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT,
    is_withdraw BOOLEAN DEFAULT false,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_withdraw FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 익명 유저 관리
CREATE TABLE IF NOT EXISTS annonymous_member (
    id BIGSERIAL PRIMARY KEY,
    member_uuid VARCHAR(255),
    member_ip VARCHAR(255),
    is_banned BOOLEAN DEFAULT false,
    banned_start_time BIGINT,
    application_banned_count INTEGER DEFAULT 0,
    cdn_banned_count INTEGER DEFAULT 0,
    banned_expiry_time BIGINT,
    ban_type VARCHAR(255),
    cf_rule_id VARCHAR(255),
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_annonymous_member_uuid_ip UNIQUE (member_uuid, member_ip)
);

-- 3. 커뮤니티 테이블

-- 카테고리
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE,
    member_id BIGINT,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 게시글
CREATE TABLE IF NOT EXISTS board (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT,
    title VARCHAR(255) NOT NULL,
    member_id BIGINT,
    content TEXT NOT NULL,
    is_pin BOOLEAN DEFAULT false,
    is_deleted BOOLEAN DEFAULT false,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_board_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 댓글
CREATE TABLE IF NOT EXISTS board_comment (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    parent_comment_id BIGINT NOT NULL DEFAULT 0,
    depth INTEGER NOT NULL DEFAULT 0,
    member_id BIGINT,
    board_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_comment_board FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 게시글 좋아요
CREATE TABLE IF NOT EXISTS board_like (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_like_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_board_like_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT uk_board_like UNIQUE (board_id, member_id)
);


-- 게시글 조회수
CREATE TABLE IF NOT EXISTS board_views (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT,
    views INTEGER DEFAULT 0,
    CONSTRAINT fk_board_views FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 통계 테이블들
CREATE TABLE IF NOT EXISTS board_counts (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT,
    counts INTEGER DEFAULT 0,
    CONSTRAINT fk_board_counts_category FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 댓글 수
CREATE TABLE IF NOT EXISTS comments_count (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT,
    counts INTEGER DEFAULT 0,
    CONSTRAINT fk_comments_count_board FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 게시글 좋아요 수
CREATE TABLE IF NOT EXISTS board_like_cnt (
    id BIGSERIAL PRIMARY KEY,
    board_id BIGINT,
    likes INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_board_like_cnt_board FOREIGN KEY (board_id) REFERENCES board(id)
);

-- 댓글 좋아요 매핑
CREATE TABLE IF NOT EXISTS comment_likes (
    id BIGSERIAL PRIMARY KEY,
    comment_id BIGINT,
    likes INTEGER DEFAULT 0,
    CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES board_comment(id)
);

-- 4. 마켓 데이터 테이블

-- 코인 정보
CREATE TABLE IF NOT EXISTS coin (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    en_name VARCHAR(255),
    content TEXT,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 거래소 정보
CREATE TABLE IF NOT EXISTS exchange (
    id BIGSERIAL PRIMARY KEY,
    market VARCHAR(255) NOT NULL,
    link VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 코인-거래소 관계
CREATE TABLE IF NOT EXISTS coin_exchange (
    id BIGSERIAL PRIMARY KEY,
    coin_id BIGINT,
    exchange_id BIGINT,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coin_exchange_coin FOREIGN KEY (coin_id) REFERENCES coin(id),
    CONSTRAINT fk_coin_exchange_exchange FOREIGN KEY (exchange_id) REFERENCES exchange(id)
);

-- 5. 공지사항 테이블
CREATE TABLE IF NOT EXISTS notice (
    id BIGSERIAL PRIMARY KEY,
    exchange_id BIGINT,
    title VARCHAR(500) NOT NULL,
    link VARCHAR(500) NOT NULL,
    date TIMESTAMP NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notice_exchange FOREIGN KEY (exchange_id) REFERENCES exchange(id)
);

-- 6. CMC (CoinMarketCap) 관련 테이블

-- CMC 코인 정보
CREATE TABLE IF NOT EXISTS cmc_coin (
    id BIGSERIAL PRIMARY KEY,
    logo VARCHAR(255),
    cmc_coin_id BIGINT NOT NULL UNIQUE,
    coin_id BIGINT,
    name VARCHAR(255) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    status BOOLEAN NOT NULL,
    is_mainnet BOOLEAN NOT NULL,
    first_historical_data TIMESTAMP NOT NULL,
    last_historical_data TIMESTAMP NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_coin_coin FOREIGN KEY (coin_id) REFERENCES coin(id)
);

-- CMC 코인 상세 정보
CREATE TABLE IF NOT EXISTS cmc_coin_info (
    id BIGSERIAL PRIMARY KEY,
    cmc_coin_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    infinite_supply BOOLEAN NOT NULL,
    is_fiat INTEGER NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_coin_info FOREIGN KEY (cmc_coin_id) REFERENCES cmc_coin(cmc_coin_id)
);

-- CMC 코인 메타데이터
CREATE TABLE IF NOT EXISTS cmc_coin_meta (
    id BIGSERIAL PRIMARY KEY,
    cmc_coin_id BIGINT NOT NULL,
    market_cap VARCHAR(255) NOT NULL,
    market_cap_dominance DOUBLE PRECISION NOT NULL,
    fully_diluted_market_cap VARCHAR(255) NOT NULL,
    circulating_supply VARCHAR(255) NOT NULL,
    total_supply VARCHAR(255) NOT NULL,
    max_supply VARCHAR(255) NOT NULL,
    self_reported_circulating_supply VARCHAR(255) NOT NULL,
    self_reported_market_cap VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_coin_meta FOREIGN KEY (cmc_coin_id) REFERENCES cmc_coin(cmc_coin_id)
);

-- CMC 플랫폼 정보
CREATE TABLE IF NOT EXISTS cmc_platform (
    id BIGSERIAL PRIMARY KEY,
    cmc_coin_id BIGINT NOT NULL,
    name VARCHAR(255),
    symbol VARCHAR(255),
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_platform FOREIGN KEY (cmc_coin_id) REFERENCES cmc_coin(cmc_coin_id)
);

-- CMC 메인넷 정보
CREATE TABLE IF NOT EXISTS cmc_mainnet (
    id BIGSERIAL PRIMARY KEY,
    cmc_coin_id BIGINT NOT NULL,
    explorer_url VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_mainnet FOREIGN KEY (cmc_coin_id) REFERENCES cmc_coin(cmc_coin_id)
);

-- CMC 순위 정보
CREATE TABLE IF NOT EXISTS cmc_rank (
    id BIGSERIAL PRIMARY KEY,
    cmc_coin_id BIGINT NOT NULL UNIQUE,
    rank BIGINT NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_rank FOREIGN KEY (cmc_coin_id) REFERENCES cmc_coin(cmc_coin_id)
);

-- CMC 거래소 정보
CREATE TABLE IF NOT EXISTS cmc_exchange (
    id BIGSERIAL PRIMARY KEY,
    cmc_exchange_id BIGINT NOT NULL UNIQUE,
    exchange_id BIGINT,
    cmc_exchange_info_id BIGINT,
    cmc_exchange_meta_id BIGINT,
    cmc_exchange_url_id BIGINT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_listed BOOLEAN NOT NULL,
    description TEXT NOT NULL,
    logo VARCHAR(255) NOT NULL,
    date_launched TIMESTAMP NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CMC 거래소 상세 정보
CREATE TABLE IF NOT EXISTS cmc_exchange_info (
    id BIGSERIAL PRIMARY KEY,
    cmc_exchange_id BIGINT NOT NULL,
    fiats TEXT,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_exchange_info FOREIGN KEY (cmc_exchange_id) REFERENCES cmc_exchange(cmc_exchange_id)
);

-- CMC 거래소 메타데이터
CREATE TABLE IF NOT EXISTS cmc_exchange_meta (
    id BIGSERIAL PRIMARY KEY,
    cmc_exchange_id BIGINT NOT NULL,
    market_fee DECIMAL(10,8) NOT NULL,
    taker_fee DECIMAL(10,8) NOT NULL,
    spot_volume_usd DECIMAL(20,2) NOT NULL,
    spot_volume_last_updated TIMESTAMP NOT NULL,
    weekly_visits BIGINT NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_exchange_meta FOREIGN KEY (cmc_exchange_id) REFERENCES cmc_exchange(cmc_exchange_id)
);

-- CMC 거래소 URL 정보
CREATE TABLE IF NOT EXISTS cmc_exchange_url (
    id BIGSERIAL PRIMARY KEY,
    cmc_exchange_id BIGINT NOT NULL,
    website VARCHAR(255) NOT NULL,
    twitter VARCHAR(255) NOT NULL,
    register VARCHAR(255) NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmc_exchange_url FOREIGN KEY (cmc_exchange_id) REFERENCES cmc_exchange(cmc_exchange_id)
);

-- 7. 채팅 추적 테이블
CREATE TABLE IF NOT EXISTS chat_tracking (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255),
    nickname VARCHAR(255) NOT NULL UNIQUE,
    member_id BIGINT,
    is_authenticated BOOLEAN NOT NULL,
    registed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Spring Batch 메타 테이블 (Spring Boot 3.x 호환)
CREATE TABLE IF NOT EXISTS batch_job_instance (
    job_instance_id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    job_name VARCHAR(100) NOT NULL,
    job_key VARCHAR(32) NOT NULL,
    CONSTRAINT job_inst_un UNIQUE (job_name, job_key)
);

CREATE TABLE IF NOT EXISTS batch_job_execution (
    job_execution_id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    job_instance_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    start_time TIMESTAMP DEFAULT NULL,
    end_time TIMESTAMP DEFAULT NULL,
    status VARCHAR(10),
    exit_code VARCHAR(2500),
    exit_message VARCHAR(2500),
    last_updated TIMESTAMP,
    job_configuration_location VARCHAR(2500) NULL,
    CONSTRAINT job_exec_job_inst_fk FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance(job_instance_id)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_params (
    job_execution_id BIGINT NOT NULL,
    parameter_name VARCHAR(100) NOT NULL,
    parameter_type VARCHAR(100) NOT NULL,
    parameter_value VARCHAR(2500) NOT NULL,
    identifying CHAR(1) NOT NULL,
    CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_step_execution (
    step_execution_id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    job_execution_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    start_time TIMESTAMP DEFAULT NULL,
    end_time TIMESTAMP DEFAULT NULL,
    status VARCHAR(10),
    commit_count BIGINT,
    read_count BIGINT,
    filter_count BIGINT,
    write_count BIGINT,
    read_skip_count BIGINT,
    write_skip_count BIGINT,
    process_skip_count BIGINT,
    rollback_count BIGINT,
    exit_code VARCHAR(2500),
    exit_message VARCHAR(2500),
    last_updated TIMESTAMP,
    CONSTRAINT job_exec_step_fk FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_step_execution_context (
    step_execution_id BIGINT NOT NULL,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    CONSTRAINT step_exec_ctx_fk FOREIGN KEY (step_execution_id) REFERENCES batch_step_execution(step_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_context (
    job_execution_id BIGINT NOT NULL,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    CONSTRAINT job_exec_ctx_fk FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

-- 9. 추가 제약조건 (필요시)
-- OneToOne relationships with cascade are managed by JPA, not explicit FK columns

-- 10. 성능을 위한 주요 인덱스들
CREATE INDEX IF NOT EXISTS idx_cmc_coin_id ON cmc_coin(cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_cmc_exchange_id ON cmc_exchange(cmc_exchange_id);
CREATE INDEX IF NOT EXISTS idx_cmc_rank_coin_id ON cmc_rank(cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_board_category_id ON board(category_id);
CREATE INDEX IF NOT EXISTS idx_board_member_id ON board(member_id);
CREATE INDEX IF NOT EXISTS idx_comment_board_id ON board_comment(board_id);
CREATE INDEX IF NOT EXISTS idx_member_email ON member(email);
CREATE INDEX IF NOT EXISTS idx_member_nickname ON member(nickname);

-- 11. 초기 기본 데이터 삽입

-- 회원 역할 기본 데이터
INSERT INTO member_role (role_key, role_name, registed_at, updated_at) VALUES 
('222e3ac0-bf1b-4071-a429-a24f63b4d11e', 'USER', NOW(), NOW()),
('fdc6a178-112d-42f9-b476-e02ee0c8281b', 'INFLUENCER', NOW(), NOW()),
('46e55574-f17e-4761-a203-f7b39c2d726d', 'MANAGER', NOW(), NOW()),
('39698ddd-fc9d-4dd9-b35e-187625f87120', 'OPERATOR', NOW(), NOW())
ON CONFLICT (role_key) DO NOTHING;

-- 시드머니 범위 기본 데이터
INSERT INTO seed_money_range (seed_range_key, range, rank, registed_at, updated_at) VALUES 
('71a7ec2d-53c6-4d2f-b917-8bc014879a12', '0 ~ 1000만원', 'Bronze', NOW(), NOW()),
('1294a31c-08ec-4d2a-a7e3-b56cb8f940ba', '1000만원 ~ 5000만원', 'Silver', NOW(), NOW()),
('144d8bcc-e2ad-497e-9814-28893a635daf', '5000만원 ~ 1억원', 'Gold', NOW(), NOW()),
('3dcc41b8-aae4-4d75-96c5-fb972a898419', '1억원 ~ 5억원', 'Platinum', NOW(), NOW()),
('994908cb-5931-4e89-9398-fa184c308984', '5억원 ~ 10억원', 'Diamond', NOW(), NOW()),
('4225d6c8-85e2-4966-aadc-fcdd19867b3f', '10억원 ~ 100억원', 'Master', NOW(), NOW()),
('16daf42e-b428-471e-85c0-bc3af8a12cbb', '100억원 이상', 'King', NOW(), NOW())
ON CONFLICT (seed_range_key) DO NOTHING;

-- 활동 등급 기본 데이터
INSERT INTO activity_rank (rank_key, grade, registed_at, updated_at) VALUES 
('6c69afd3-f4b7-41a6-b619-944f86442e1b', '새싹', NOW(), NOW()),
('44d1019b-6261-4bd4-95a9-ead1e87225d5', '일반회원', NOW(), NOW()),
('d1a6c20d-8462-4ba0-aea4-055ce64a5117', '우수회원', NOW(), NOW()),
('5e0eba1c-eeff-47f9-9890-872b727e3286', '마스터', NOW(), NOW()),
('a94806da-d8db-4bda-9230-7cf12cc42df3', '운영자', NOW(), NOW())
ON CONFLICT (rank_key) DO NOTHING;

-- 기본 카테고리 데이터
INSERT INTO category (category_name, registed_at, updated_at) 
SELECT category_name, NOW(), NOW()
FROM (VALUES 
    ('전체'), 
    ('코인'), 
    ('주식'), 
    ('뉴스'), 
    ('자유')
) AS new_categories(category_name)
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE category.category_name = new_categories.category_name
);

-- 카테고리에 대응하는 board_counts 데이터 생성
INSERT INTO board_counts (category_id, counts)
SELECT c.id, 0
FROM category c
WHERE NOT EXISTS (
    SELECT 1 FROM board_counts bc WHERE bc.category_id = c.id
);