-- V4: 전문가 관련 테이블 생성

-- 전문가 신청 테이블
CREATE TABLE IF NOT EXISTS expert_application (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    expertise_field VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    credentials TEXT,
    portfolio_url VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    rejection_reason TEXT,
    reviewed_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES member(id) ON DELETE SET NULL
);

-- 전문가 프로필 테이블
CREATE TABLE IF NOT EXISTS expert_profile (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL UNIQUE,
    application_id BIGINT NOT NULL,
    expertise_field VARCHAR(200) NOT NULL,
    bio TEXT,
    portfolio_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    articles_count INTEGER NOT NULL DEFAULT 0,
    followers_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES expert_application(id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_expert_app_member_id ON expert_application(member_id);
CREATE INDEX IF NOT EXISTS idx_expert_app_status ON expert_application(status);
CREATE INDEX IF NOT EXISTS idx_expert_app_member_status ON expert_application(member_id, status);

CREATE INDEX IF NOT EXISTS idx_expert_profile_member_id ON expert_profile(member_id);
CREATE INDEX IF NOT EXISTS idx_expert_profile_is_active ON expert_profile(is_active);

-- 테이블 및 컬럼 설명
COMMENT ON TABLE expert_application IS '전문가 신청 테이블';
COMMENT ON COLUMN expert_application.expertise_field IS '전문 분야';
COMMENT ON COLUMN expert_application.description IS '전문가 설명 및 자기소개';
COMMENT ON COLUMN expert_application.credentials IS '자격증 및 경력사항';
COMMENT ON COLUMN expert_application.portfolio_url IS '포트폴리오 URL';
COMMENT ON COLUMN expert_application.status IS '신청 상태 (PENDING, APPROVED, REJECTED, CANCELLED)';
COMMENT ON COLUMN expert_application.rejection_reason IS '거부 사유';
COMMENT ON COLUMN expert_application.reviewed_by IS '검토한 관리자 ID';

COMMENT ON TABLE expert_profile IS '전문가 프로필 테이블';
COMMENT ON COLUMN expert_profile.member_id IS '전문가 회원 ID';
COMMENT ON COLUMN expert_profile.application_id IS '승인된 신청서 ID';
COMMENT ON COLUMN expert_profile.expertise_field IS '전문 분야';
COMMENT ON COLUMN expert_profile.bio IS '전문가 소개';
COMMENT ON COLUMN expert_profile.portfolio_url IS '포트폴리오 URL';
COMMENT ON COLUMN expert_profile.is_active IS '활성 상태';
COMMENT ON COLUMN expert_profile.articles_count IS '작성한 전문가 게시글 수';
COMMENT ON COLUMN expert_profile.followers_count IS '팔로워 수';
