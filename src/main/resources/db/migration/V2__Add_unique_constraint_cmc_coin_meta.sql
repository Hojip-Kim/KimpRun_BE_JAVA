-- CMC 테이블들에 중복 레코드 방지를 위한 UNIQUE 제약 조건 추가
-- UPSERT 작업이 정상적으로 동작하기 위해 필요함

-- 1. cmc_coin_meta: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_coin_meta 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_coin_id) id
    FROM cmc_coin_meta
    ORDER BY cmc_coin_id, id DESC
);

ALTER TABLE cmc_coin_meta 
ADD CONSTRAINT uk_cmc_coin_meta_coin_id UNIQUE (cmc_coin_id);

-- 2. cmc_coin_info: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_coin_info 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_coin_id) id
    FROM cmc_coin_info
    ORDER BY cmc_coin_id, id DESC
);

ALTER TABLE cmc_coin_info 
ADD CONSTRAINT uk_cmc_coin_info_coin_id UNIQUE (cmc_coin_id);

-- 3. cmc_exchange_info: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_exchange_info 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_exchange_id) id
    FROM cmc_exchange_info
    ORDER BY cmc_exchange_id, id DESC
);

ALTER TABLE cmc_exchange_info 
ADD CONSTRAINT uk_cmc_exchange_info_exchange_id UNIQUE (cmc_exchange_id);

-- 4. cmc_exchange_meta: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_exchange_meta 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_exchange_id) id
    FROM cmc_exchange_meta
    ORDER BY cmc_exchange_id, id DESC
);

ALTER TABLE cmc_exchange_meta 
ADD CONSTRAINT uk_cmc_exchange_meta_exchange_id UNIQUE (cmc_exchange_id);

-- 5. cmc_exchange_url: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_exchange_url 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_exchange_id) id
    FROM cmc_exchange_url
    ORDER BY cmc_exchange_id, id DESC
);

ALTER TABLE cmc_exchange_url 
ADD CONSTRAINT uk_cmc_exchange_url_exchange_id UNIQUE (cmc_exchange_id);

-- 6. cmc_platform: 중복 데이터 제거 후 UNIQUE 제약 조건 추가
DELETE FROM cmc_platform 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_coin_id) id
    FROM cmc_platform
    ORDER BY cmc_coin_id, id DESC
);

ALTER TABLE cmc_platform 
ADD CONSTRAINT uk_cmc_platform_coin_id UNIQUE (cmc_coin_id);

-- 7. cmc_mainnet: 중복 데이터 제거 후 (cmc_coin_id, explorer_url) 복합 UNIQUE 제약 조건 추가
DELETE FROM cmc_mainnet 
WHERE id NOT IN (
    SELECT DISTINCT ON (cmc_coin_id, explorer_url) id
    FROM cmc_mainnet
    ORDER BY cmc_coin_id, explorer_url, id DESC
);

ALTER TABLE cmc_mainnet 
ADD CONSTRAINT uk_cmc_mainnet_coin_url UNIQUE (cmc_coin_id, explorer_url);

-- 자주 조회되는 컬럼들을 위한 성능 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_cmc_coin_meta_coin_id ON cmc_coin_meta (cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_cmc_coin_info_coin_id ON cmc_coin_info (cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_cmc_exchange_info_exchange_id ON cmc_exchange_info (cmc_exchange_id);
CREATE INDEX IF NOT EXISTS idx_cmc_exchange_meta_exchange_id ON cmc_exchange_meta (cmc_exchange_id);
CREATE INDEX IF NOT EXISTS idx_cmc_exchange_url_exchange_id ON cmc_exchange_url (cmc_exchange_id);
CREATE INDEX IF NOT EXISTS idx_cmc_platform_coin_id ON cmc_platform (cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_cmc_mainnet_coin_id ON cmc_mainnet (cmc_coin_id);
CREATE INDEX IF NOT EXISTS idx_cmc_mainnet_url ON cmc_mainnet (explorer_url);