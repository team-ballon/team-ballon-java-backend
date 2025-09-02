-- 1. 기초 테이블 (참조되는 테이블 먼저)
CREATE TABLE "user" (
                        "user_id" SERIAL PRIMARY KEY,
                        "email" VARCHAR(100) NOT NULL UNIQUE,
                        "password" VARCHAR(80) NOT NULL,
                        "name" VARCHAR(50),
                        "sex" VARCHAR(10) NOT NULL CHECK ("sex" IN ('MALE', 'FEMALE')),
                        "role" VARCHAR(20) NOT NULL CHECK ("role" IN ('USER', 'PARTNER', 'ADMIN')),
                        "age" SMALLINT,
                        "refresh_token" VARCHAR(50),
                        "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE verification_code (
                                   id BIGSERIAL PRIMARY KEY,          -- PK (자동 증가)
                                   email VARCHAR(255) NOT NULL,       -- 인증 대상 이메일
                                   code VARCHAR(20) NOT NULL,         -- 인증 코드
                                   expires_at TIMESTAMP NOT NULL,     -- 만료 시각
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   used BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_verification_email_code
    ON verification_code (email, code);

CREATE INDEX idx_verification_expires_at
    ON verification_code (expires_at);

CREATE TABLE "category" (
                            "category_id" SERIAL PRIMARY KEY,
                            "name" VARCHAR(100) NOT NULL,
                            "depth" SMALLINT,
                            "parent_id" INTEGER REFERENCES "category" ("category_id") ON DELETE CASCADE
);

CREATE TABLE "permission" (
                              "permission_id" SERIAL PRIMARY KEY,
                              "name" VARCHAR(200) NOT NULL,
                              "description" VARCHAR(400) NOT NULL
);

CREATE TABLE "event" (
                         "event_id" SERIAL PRIMARY KEY,
                         "title" VARCHAR(100) NOT NULL,
                         "description" TEXT NOT NULL,
                         "start_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         "end_date" TIMESTAMP NOT NULL
);

-- 2. 종속 테이블
CREATE TABLE "admin" (
                         "admin_id" SERIAL PRIMARY KEY,
                         "is_super_admin" BOOLEAN NOT NULL,
                         "role" VARCHAR(100) NOT NULL,
                         "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "partner" (
                           "partner_id" SERIAL PRIMARY KEY,
                           "partner_name" VARCHAR(50) NOT NULL,
                           "active" BOOLEAN NOT NULL,
                           "overview" TEXT,
                           "partner_email" VARCHAR(30) NOT NULL,
                           "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "address" (
                           "address_id" SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL, -- 배송지명 (예: 우리집, 회사)
                           recipient VARCHAR(50) NOT NULL,    -- 수령인
                           contact_number VARCHAR(20) NOT NULL, -- 연락처
                           base_address VARCHAR(255) NOT NULL,     -- 주소
                           detail_address VARCHAR(255),       -- 상세주소 (필수 아닐 수 있음)
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일시
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "cart" (
                        "cart_id" SERIAL PRIMARY KEY,
                        "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "product" (
                           "product_id" SERIAL PRIMARY KEY,
                           "name" VARCHAR(50) NOT NULL,
                           "price" INTEGER NOT NULL,
                           "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK')),
                           "quantity" INTEGER NOT NULL,
                           "category_id" INTEGER REFERENCES "category" ("category_id") ON DELETE SET NULL,
                           "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "coupon" (
                          "coupon_id" SERIAL PRIMARY KEY,
                          "field" VARCHAR(255),
                          "event_id" INTEGER NOT NULL REFERENCES "event" ("event_id"),
                          "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "orders" (
                          "order_id" SERIAL PRIMARY KEY,
                          "toss_order_id" VARCHAR(100) NOT NULL,
                          "amount" INTEGER NOT NULL,
                          "status" VARCHAR(20) NOT NULL,
                          "payment_key" VARCHAR(200),
                          "paid_at" TIMESTAMP,
                          "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id"),
                          "address_id" INTEGER NOT NULL REFERENCES "address" ("address_id")
);

CREATE TABLE "partner_category" (
                                    "partner_category_id" SERIAL PRIMARY KEY,
                                    "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id"),
                                    "category_id2" INTEGER NOT NULL REFERENCES "category" ("category_id")
);

CREATE TABLE "cart_product" (
                                "cart_product_id" SERIAL PRIMARY KEY,
                                "quantity" SMALLINT NOT NULL,
                                "cart_id" INTEGER NOT NULL REFERENCES "cart" ("cart_id"),
                                "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "image_link" (
                              "image_link_id" SERIAL PRIMARY KEY,
                              "link" VARCHAR(300) NOT NULL,
                              "order" INTEGER NOT NULL,
                              "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "wishlist" (
                            "wishlist_id" SERIAL PRIMARY KEY,
                            "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id"),
                            "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "review" (
                          "review_id" SERIAL PRIMARY KEY,
                          "detail" TEXT NOT NULL,
                          "rating" SMALLINT NOT NULL CHECK ("rating" BETWEEN 0 AND 5),
                          "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id"),
                          "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "event_application" (
                                     "event_application_id" SERIAL PRIMARY KEY,
                                     "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'APPROVED')),
                                     "application_date" TIMESTAMP NOT NULL,
                                     "event_id" INTEGER NOT NULL REFERENCES "event" ("event_id"),
                                     "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "keyword" (
                           "keyword_id" SERIAL PRIMARY KEY,
                           "keyword" VARCHAR(100) NOT NULL,
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "ai_report" (
                             "ai_report_id" SERIAL PRIMARY KEY,
                             "title" TEXT NOT NULL,
                             "summary" TEXT,
                             "content_format" TEXT NOT NULL,
                             "content" TEXT,
                             "content_json" JSONB,
                             "type" VARCHAR(50) NOT NULL
);

CREATE TABLE "product_application" (
                                       "product_application_id" SERIAL PRIMARY KEY,
                                       "name" VARCHAR(200) NOT NULL,
                                       "status" VARCHAR(20) NOT NULL,
                                       "quantity" INTEGER NOT NULL,
                                       "price" INTEGER NOT NULL,
                                       "type" VARCHAR(20) NOT NULL,
                                       "application_date" TIMESTAMP NOT NULL,
                                       "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "coupon_product" (
                                  "coupon_product_id" SERIAL PRIMARY KEY,
                                  "coupon_id" INTEGER NOT NULL REFERENCES "coupon" ("coupon_id"),
                                  "product_id2" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "order_product" (
                                 "order_product_id" SERIAL PRIMARY KEY,
                                 "product_id2" INTEGER NOT NULL REFERENCES "product" ("product_id"),
                                 "order_id" INTEGER NOT NULL REFERENCES "orders" ("order_id")
);

CREATE TABLE "admin_permission" (
                                    "admin_id" INTEGER NOT NULL REFERENCES "admin" ("admin_id"),
                                    "permission_id" INTEGER NOT NULL REFERENCES "permission" ("permission_id"),
                                    PRIMARY KEY ("admin_id", "permission_id")
);

-- ==== Admin 검색 최적화 ====

-- 이유: user 테이블과 JOIN 시 사용
CREATE INDEX idx_admin_user_id ON "admin" (user_id);

-- 이유: role 컬럼 필터링 및 정렬용
CREATE INDEX idx_admin_role ON "admin" (role);

-- 이유: 최신순/오래된순 정렬용
CREATE INDEX idx_admin_created_at ON "admin" (created_at DESC);

-- 이유: permission_id로 관리자를 찾기 위함
CREATE INDEX idx_admin_permission_permission_id ON admin_permission (permission_id);


-- ==== Partner 검색 최적화 ====

-- Trigram 확장을 먼저 활성화합니다. (DB 당 최초 1회)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 이유: category 테이블과 JOIN 시 사용 (partner_id 기준)
CREATE INDEX idx_partner_category_partner_id ON partner_category (partner_id);

-- 이유: category 테이블과 JOIN 시 사용 (category_id 기준)
CREATE INDEX idx_partner_category_category_id2 ON partner_category (category_id2);

-- 이유: active 여부 필터링용
CREATE INDEX idx_partner_active ON partner (active);

-- 이유: 이름 기준 정렬(ORDER BY)용
CREATE INDEX idx_partner_partner_name ON partner (partner_name);

-- 이유: 이메일 기준 정렬(ORDER BY)용
CREATE INDEX idx_partner_partner_email ON partner (partner_email);

-- 이유: 이름 포함 검색(LIKE '%%') 성능 개선 (강력 추천)
CREATE INDEX idx_partner_partner_name_trgm ON partner USING GIN (partner_name gin_trgm_ops);

-- 이유: 이메일 포함 검색(LIKE '%%') 성능 개선 (강력 추천)
CREATE INDEX idx_partner_partner_email_trgm ON partner USING GIN (partner_email gin_trgm_ops);

-- ==== User 검색 최적화 ====

-- role 검색 (예: 관리자 목록, 파트너 목록 조회)
CREATE INDEX idx_user_role ON "user"(role);

-- created_at 정렬/범위 조회 (최신 가입자, 오래된 사용자 등)
CREATE INDEX idx_user_created_at ON "user"(created_at DESC);

-- 복합 인덱스 (조건에 따라)
-- (role, created_at): "관리자 중 최근 가입자" 같은 쿼리에 유용
CREATE INDEX idx_user_role_created_at ON "user"(role, created_at DESC);
