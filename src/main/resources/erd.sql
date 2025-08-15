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
                        "created_at" TIMESTAMP NOT NULL
);

CREATE TABLE "category" (
                            "category_id" SERIAL PRIMARY KEY,
                            "name" VARCHAR(100) NOT NULL UNIQUE,
                            "depth" SMALLINT,
                            "parent_id" INTEGER
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
                         "end_date" TIMESTAMP NOT NULL
);

-- 2. 종속 테이블
CREATE TABLE "admin" (
                         "admin_id" SERIAL PRIMARY KEY,
                         "is_super_admin" BOOLEAN NOT NULL,
                         "role" VARCHAR(100) NOT NULL,
                         "created_at" TIMESTAMP NOT NULL,
                         "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "partner" (
                           "partner_id" SERIAL PRIMARY KEY,
                           "partner_name" VARCHAR(50) NOT NULL,
                           "active" BOOLEAN NOT NULL,
                           "overview" TEXT,
                           "partner_email" VARCHAR(30) NOT NULL,
                           "user_id" INTEGER NOT NULL REFERENCES "user" ("user_id")
);

CREATE TABLE "address" (
                           "address_id" SERIAL PRIMARY KEY,
                           "address" VARCHAR(200) NOT NULL,
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
                           "category_id" INTEGER NOT NULL REFERENCES "category" ("category_id"),
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
                          "created_at" TIMESTAMP NOT NULL,
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
