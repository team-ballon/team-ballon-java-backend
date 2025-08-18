-- 최상위 카테고리 (depth = 0)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('스킨케어', 0, NULL),         -- 1
                                                  ('마스크팩', 0, NULL),         -- 2
                                                  ('클렌징', 0, NULL),           -- 3
                                                  ('선케어', 0, NULL),           -- 4
                                                  ('메이크업', 0, NULL),         -- 5
                                                  ('뷰티소품', 0, NULL),         -- 6
                                                  ('더모 코스메틱', 0, NULL),    -- 7
                                                  ('맨즈케어', 0, NULL),         -- 8
                                                  ('헤어케어', 0, NULL),         -- 9
                                                  ('바디케어', 0, NULL),         -- 10
                                                  ('향수/디퓨저', 0, NULL),      -- 11
                                                  ('네일', 0, NULL);             -- 12


-- 스킨케어 (id = 1)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('스킨/토너', 1, 1),
                                                  ('에센스/세럼/앰플', 1, 1),
                                                  ('크림', 1, 1),
                                                  ('로션', 1, 1),
                                                  ('미스트/오일', 1, 1),
                                                  ('스킨케어세트', 1, 1),
                                                  ('스킨케어 디바이스', 1, 1);

-- 마스크팩 (id = 2)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('시트팩', 1, 2),
                                                  ('패드', 1, 2),
                                                  ('페이셜팩', 1, 2),
                                                  ('코팩', 1, 2),
                                                  ('패치', 1, 2);

-- 클렌징 (id = 3)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('클렌징폼/젤', 1, 3),
                                                  ('오일/밤', 1, 3),
                                                  ('워터/밀크', 1, 3),
                                                  ('필링&스크럽', 1, 3),
                                                  ('티슈/패드', 1, 3),
                                                  ('립&아이리무버', 1, 3),
                                                  ('클렌징 디바이스', 1, 3);

-- 선케어 (id = 4)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('선크림', 1, 4),
                                                  ('선스틱', 1, 4),
                                                  ('선쿠션', 1, 4),
                                                  ('선스프레이/선패치', 1, 4),
                                                  ('태닝/애프터선', 1, 4);

-- 메이크업 (id = 5)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('립메이크업', 1, 5),
                                                  ('베이스메이크업', 1, 5),
                                                  ('아이메이크업', 1, 5);

-- 뷰티소품 (id = 6)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('메이크업소품', 1, 6),
                                                  ('아이소품', 1, 6),
                                                  ('스킨케어소품', 1, 6),
                                                  ('헤어소품', 1, 6),
                                                  ('네일/바디소품', 1, 6),
                                                  ('뷰티잡화', 1, 6);

-- 더모 코스메틱 (id = 7)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('스킨케어', 1, 7),
                                                  ('바디케어', 1, 7),
                                                  ('클렌징', 1, 7),
                                                  ('선케어', 1, 7),
                                                  ('마스크팩', 1, 7);

-- 맨즈케어 (id = 8)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('스킨케어', 1, 8),
                                                  ('메이크업', 1, 8),
                                                  ('쉐이빙/왁싱', 1, 8),
                                                  ('바디케어', 1, 8),
                                                  ('헤어케어', 1, 8),
                                                  ('프래그런스', 1, 8),
                                                  ('패션/취미', 1, 8),
                                                  ('헬스용품/식품', 1, 8);

-- 헤어케어 (id = 9)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('샴푸/린스', 1, 9),
                                                  ('트리트먼트/팩', 1, 9),
                                                  ('두피앰플/토닉', 1, 9),
                                                  ('헤어에센스', 1, 9),
                                                  ('염색약/펌', 1, 9),
                                                  ('헤어기기/브러시', 1, 9),
                                                  ('스타일링', 1, 9);

-- 바디케어 (id = 10)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('샤워/입욕', 1, 10),
                                                  ('로션/오일/미스트', 1, 10),
                                                  ('핸드케어', 1, 10),
                                                  ('풋케어', 1, 10),
                                                  ('제모/왁싱', 1, 10),
                                                  ('데오드란트', 1, 10),
                                                  ('선물세트', 1, 10),
                                                  ('베이비', 1, 10);

-- 향수/디퓨저 (id = 11)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('향수', 1, 11),
                                                  ('미니/고체향수', 1, 11),
                                                  ('홈프래그런스', 1, 11);

-- 네일 (id = 12)
INSERT INTO category (name, depth, parent_id) VALUES
                                                  ('일반네일', 1, 12),
                                                  ('젤네일', 1, 12),
                                                  ('네일팁/스티커', 1, 12),
                                                  ('네일케어/리무버', 1, 12);
