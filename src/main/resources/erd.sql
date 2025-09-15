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
                                   "id" BIGSERIAL PRIMARY KEY,          -- PK (자동 증가)
                                   "email" VARCHAR(255) NOT NULL,       -- 인증 대상 이메일
                                   "code" VARCHAR(20) NOT NULL,         -- 인증 코드
                                   "expires_at" TIMESTAMP NOT NULL,     -- 만료 시각
                                   "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   "used" BOOLEAN NOT NULL DEFAULT FALSE
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
                         "start_date" TIMESTAMP NOT NULL,
                         "end_date" TIMESTAMP NOT NULL,
                         "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
                           "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "address" (
                           "address_id" SERIAL PRIMARY KEY,
                           "name" VARCHAR(100) NOT NULL, -- 배송지명 (예: 우리집, 회사)
                           "recipient" VARCHAR(50) NOT NULL,    -- 수령인
                           "contact_number" VARCHAR(20) NOT NULL, -- 연락처
                           "base_address" VARCHAR(255) NOT NULL,     -- 주소
                           "detail_address" VARCHAR(255),       -- 상세주소 (필수 아닐 수 있음)
                           "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성일시
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "cart" (
                        "cart_id" SERIAL PRIMARY KEY,
                        "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "product" (
                           "product_id" SERIAL PRIMARY KEY,
                           "product_url" VARCHAR(1000),
                           "name" VARCHAR(50) NOT NULL,
                           "price" INTEGER NOT NULL,
                           "quantity" INTEGER NOT NULL,
                           "status"  VARCHAR(20) NOT NULL CHECK ("type" IN ('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK')),
                           "min_quantity" INTEGER NULL,
                           "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           "category_id" INTEGER REFERENCES "category" ("category_id") ON DELETE SET NULL,
                           "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "coupon" (
                          "coupon_id" SERIAL PRIMARY KEY,
                          "coupon_name" VARCHAR(255) NOT NULL,
                          "type" VARCHAR(20) NOT NULL CHECK ("type" IN ('PERCENT', 'FIXED')),
                          "event_id" INTEGER NOT NULL REFERENCES "event" ("event_id"),
                          "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "user_coupon" (
                               "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id"),
                               "coupon_id" INTEGER NOT NULL REFERENCES "coupon" ("coupon_id"),
                               "is_used" BOOLEAN NOT NULL,
                               "used_at" TIMESTAMP,
                               PRIMARY KEY ("user_id", "coupon_id")
);

CREATE UNIQUE INDEX uniq_user_coupon ON user_coupon(user_id, coupon_id);

CREATE TABLE "settlement" (
                              "settlement_id" SERIAL PRIMARY KEY,
                              "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id"),
                              "period_start" DATE NOT NULL,          -- 정산 시작일
                              "period_end" DATE NOT NULL,            -- 정산 종료일
                              "total_amount" INTEGER NOT NULL,       -- 해당 기간 합계 금액
                              "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'PAID')),
                              "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "order" (
                          "order_id" SERIAL PRIMARY KEY,
                          "amount" INTEGER NOT NULL,
                          "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('READY', 'IN_PROGRESS', 'WAITING_FOR_DEPOSIT', 'DONE', 'CANCELED', 'PARTIAL_CANCELED', 'ABORTED', 'EXPIRED')),
                          "paid_at" TIMESTAMP,
                          "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id"),
                          "address_id" INTEGER NOT NULL REFERENCES "address" ("address_id"),
                          "settlement_id" INTEGER NULL REFERENCES "settlement" ("settlement_id")
);

CREATE TABLE "partner_category" (
                                    "partner_category_id" SERIAL PRIMARY KEY,
                                    "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id"),
                                    "category_id" INTEGER NOT NULL REFERENCES "category" ("category_id")
);

CREATE TABLE "cart_product" (
                                "cart_product_id" SERIAL PRIMARY KEY,
                                "quantity" SMALLINT NOT NULL,
                                "cart_id" INTEGER NOT NULL REFERENCES "cart" ("cart_id"),
                                "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id"),
);

CREATE TABLE "image_link" (
                              "image_link_id" SERIAL PRIMARY KEY,
                              "link" VARCHAR(1000) NOT NULL,
                              "order" INTEGER NOT NULL,
                              "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "review" (
                          "review_id" SERIAL PRIMARY KEY,
                          "detail" TEXT NOT NULL,
                          "rating" SMALLINT NOT NULL CHECK ("rating" BETWEEN 0 AND 5),
                          "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id"),
                          "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "event_application" (
                                     "event_application_id" SERIAL PRIMARY KEY,
                                     "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'APPROVED')),
                                     "application_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     "event_id" INTEGER NOT NULL REFERENCES "event" ("event_id"),
                                     "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id")
);

CREATE TABLE "keyword" (
                           "keyword_id" SERIAL PRIMARY KEY,
                           "keyword" VARCHAR(200) NOT NULL,
                           "normalized" VARCHAR(200) NOT NULL,
                           "count" INTEGER NOT NULL,
                           "last_searched_at" TIMESTAMP NOT NULL,
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "ai_report" (
                             "type" VARCHAR(50) PRIMARY KEY,  -- 유형별로 한 줄만
                             "title" TEXT NOT NULL,
                             "summary" TEXT,
                             "content_format" VARCHAR(20) NOT NULL CHECK (content_format IN ('json','markdown','html')),
                             "content" TEXT,
                             "content_json" JSONB,
                             "updated_at" TIMESTAMP DEFAULT NOW()
);

CREATE TABLE "product_application" (
                                       "product_application_id" SERIAL PRIMARY KEY,
                                       "name" VARCHAR(200) NOT NULL,
                                       "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'APPROVED', 'DENIED')),
                                       "quantity" INTEGER NOT NULL,
                                       "price" INTEGER NOT NULL,
                                       "type" VARCHAR(20) NOT NULL CHECK ("type" IN ('CREATE', 'UPDATE', 'REMOVE')),
                                       "application_date" TIMESTAMP NOT NULL,
                                       "min_quantity" INTEGER NOT NULL DEFAULT 0,
                                       "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id"),
                                       "category_id" INTEGER NOT NULL REFERENCES "category" ("category_id"),
                                       "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "coupon_product" (
                                  "coupon_product_id" SERIAL PRIMARY KEY,
                                  "coupon_id" INTEGER NOT NULL REFERENCES "coupon" ("coupon_id"),
                                  "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
);

CREATE TABLE "order_product" (
                                 "order_product_id" SERIAL PRIMARY KEY,
                                 "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id"),
                                 "order_id" INTEGER NOT NULL REFERENCES "orders" ("order_id"),
                                 "coupon_id" INTEGER REFERENCES "coupon"("coupon_id"),
                                 "quantity" INTEGER NOT NULL,
                                 "product_amount" INTEGER NOT NULL,
                                 "discount_amount" INTEGER NOT NULL,
                                 "paid_amount" INTEGER NOT NULL,
                                 "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

CREATE TABLE "admin_permission" (
                                    "admin_id" INTEGER NOT NULL REFERENCES "admin" ("admin_id"),
                                    "permission_id" INTEGER NOT NULL REFERENCES "permission" ("permission_id"),
                                    PRIMARY KEY ("admin_id", "permission_id")
);

CREATE TABLE "purchase_order" (
                                    "purchase_order_id" SERIAL PRIMARY KEY,
                                    "quantity" INTEGER NOT NULL,
                                    "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'COMPLETE', 'CANCELED')),
                                    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    "partner_id" INTEGER NOT NULL REFERENCES "partner" ("partner_id"),
                                    "product_id" INTEGER NOT NULL REFERENCES "product" ("product_id")
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
CREATE INDEX idx_partner_category_category_id2 ON partner_category (category_id);

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

-- ==== Product 검색 최적화 ====

-- 최신 상품 조회
CREATE INDEX IF NOT EXISTS idx_product_created_at
    ON product (created_at DESC);

-- 카테고리별 최신 상품 조회
CREATE INDEX IF NOT EXISTS idx_product_category_created_at
    ON product (category_id, created_at DESC);

-- 파트너별 최신 상품 조회
CREATE INDEX IF NOT EXISTS idx_product_partner_created_at
    ON product (partner_id, created_at DESC);

-- 가격 필터링
CREATE INDEX IF NOT EXISTS idx_product_price
    ON product (price);

-- 상품명 LIKE 검색 최적화 (pg_trgm 확장 필요)
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_product_name_trgm
    ON product USING gin (name gin_trgm_ops);

-- ==== Event 검색 최적화 ====

CREATE INDEX idx_event_title_desc_trgm ON event USING gin ((title || ' ' || description) gin_trgm_ops);

CREATE INDEX idx_event_start_end ON event (start_date, end_date);

CREATE INDEX idx_event_created_at_desc ON event (created_at DESC);

-- ==== Order 검색 최적화 ====

-- 상태별 주문 조회
CREATE INDEX idx_order_status ON "order"(status);

-- 사용자별 최신 주문 조회
CREATE INDEX idx_order_user_created_at ON "order"(user_id, created_at DESC);

-- 결제 완료 시간 기준 조회
CREATE INDEX idx_order_paid_at ON "order"(paid_at);

-- ==== OrderProduct 검색 최적화 ====

CREATE INDEX idx_order_product_product_id ON order_product(product_id);

CREATE INDEX idx_order_product_order_id ON order_product(order_id);

-- ==== Review 검색 최적화 ====

CREATE INDEX idx_review_product_id ON review(product_id);

CREATE INDEX idx_review_user_id ON review(user_id);

CREATE INDEX idx_review_product_created_at ON review(product_id, created_at DESC);

-- ==== PartnerSettlement 검색 최적화 ====

CREATE INDEX idx_settlement_partner_period ON settlement(partner_id, period_start, period_end);

-- ==== keyword 검색 최적화 ====

CREATE INDEX idx_keyword_user_id ON keyword(user_id);

CREATE INDEX idx_keyword_count ON keyword(count DESC);