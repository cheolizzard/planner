-- ============================================
-- 시간표 데이터 업데이트 스크립트
-- 기존 데이터를 삭제하고 새로운 시간표 데이터로 교체
-- ============================================

USE scalable_todo_db;

-- 기존 강의 시간 데이터 삭제 (외래키 제약 때문에 역순으로 삭제)
DELETE FROM lecture_time;
DELETE FROM enrollment;
DELETE FROM course;
DELETE FROM subject;
DELETE FROM professor WHERE professor_id <= 8;

-- AUTO_INCREMENT 리셋 (재실행 시 ID가 1부터 시작하도록)
ALTER TABLE professor AUTO_INCREMENT = 1;
ALTER TABLE subject AUTO_INCREMENT = 1;
ALTER TABLE course AUTO_INCREMENT = 1;
ALTER TABLE lecture_time AUTO_INCREMENT = 1;
ALTER TABLE enrollment AUTO_INCREMENT = 1;

-- ============================================
-- 1. 교수 정보 삽입 (이미지 시간표 기준)
-- ============================================
INSERT INTO professor (professor_id, name, email, office_location) VALUES
(1, '이원주', 'lee.wonju@university.ac.kr', '7호관 301호'),
(2, '조영석', 'jo.youngseok@university.ac.kr', '5호관 117호'),
(3, '허태성', 'heo.taeseong@university.ac.kr', '7호관 315호'),
(4, '조규철', 'jo.gyucheol@university.ac.kr', '7호관 315호'),
(5, '최성수', 'choi.seongsu@university.ac.kr', '5호관 310호'),
(6, '이선애', 'lee.seonae@university.ac.kr', '4호관 403호'),
(7, '윤교수', 'yoon.prof@university.ac.kr', '공학관 307호'),
(8, '김상일', 'kim.sangil@university.ac.kr', '4호관 403호')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    email = VALUES(email),
    office_location = VALUES(office_location);

-- AUTO_INCREMENT를 다음 ID로 설정
ALTER TABLE professor AUTO_INCREMENT = 9;

-- ============================================
-- 2. 과목 정보 삽입 (이미지 시간표 기준)
-- ============================================
INSERT INTO subject (subject_id, subject_name, credits) VALUES
(1, 'JAVA프로그래밍', 3),
(2, 'Oracle SQL&PL/SQL', 3),
(3, 'JSP', 3),
(4, '운영체제', 3),
(5, '정보보안', 3),
(6, 'S/W프로젝트', 3),
(7, '알고리즘', 3),
(8, '시스템분석설계', 3)
ON DUPLICATE KEY UPDATE 
    subject_name = VALUES(subject_name),
    credits = VALUES(credits);

-- AUTO_INCREMENT를 다음 ID로 설정
ALTER TABLE subject AUTO_INCREMENT = 9;

-- ============================================
-- 3. 강의(개설 강좌) 정보 삽입 (이미지 시간표 기준)
-- ============================================
INSERT INTO course (course_id, subject_id, professor_id, classroom, semester) VALUES
(1, 1, 1, '7호관-301', '2025-1'),  -- JAVA프로그래밍, 이원주
(2, 2, 3, '7호관-315', '2025-1'),  -- Oracle SQL&PL/SQL, 허태성
(3, 3, 6, '4호관-403', '2025-1'),  -- JSP, 이선애
(4, 4, 2, '5호관-117', '2025-1'),  -- 운영체제, 조영석
(5, 5, 5, '5호관-310', '2025-1'),  -- 정보보안, 최성수
(6, 6, 4, '7호관-315', '2025-1'),  -- S/W프로젝트, 조규철
(7, 7, 7, '공학관 107호', '2025-1'),  -- 알고리즘 (미사용)
(8, 8, 8, '4호관-403', '2025-1')  -- 시스템분석설계, 김상일
ON DUPLICATE KEY UPDATE 
    subject_id = VALUES(subject_id),
    professor_id = VALUES(professor_id),
    classroom = VALUES(classroom),
    semester = VALUES(semester);

-- AUTO_INCREMENT를 다음 ID로 설정
ALTER TABLE course AUTO_INCREMENT = 9;

-- ============================================
-- 4. 강의 시간 정보 삽입 (이미지 시간표 기준 - 교시 정확히 매칭)
-- 교시: 1(09:00~09:50), 2(09:55~10:45), 3(10:50~11:40), 4(11:45~12:35), 
--       5(12:40~13:30), 6(13:35~14:25), 7(14:30~15:20), 8(15:25~16:15),
--       9(16:20~17:10), 10(17:15~18:05), 11(18:10~19:00), 12(19:05~19:55),
--       13(20:00~20:45), 14(20:50~21:35), 15(21:40~22:25), 16(22:30~23:15)
-- ============================================
INSERT INTO lecture_time (course_id, day_of_week, start_time, end_time) VALUES
-- JAVA프로그래밍: 월요일 6-8교시 (13:35~16:15) - 7호관-301, 이원주
(1, '월', '13:35', '16:15'),
-- 운영체제: 화요일 2-4교시 (09:55~12:35) - 5호관-117, 조영석
(4, '화', '09:55', '12:35'),
-- Oracle SQL&PL/SQL: 화요일 6-8교시 (13:35~16:15) - 7호관-315, 허태성
(2, '화', '13:35', '16:15'),
-- S/W프로젝트: 수요일 6-8교시 (13:35~16:15) - 7호관-315, 조규철
(6, '수', '13:35', '16:15'),
-- 정보보안: 목요일 2-4교시 (09:55~12:35) - 5호관-310, 최성수
(5, '목', '09:55', '12:35'),
-- JSP: 목요일 6-8교시 (13:35~16:15) - 4호관-403, 이선애
(3, '목', '13:35', '16:15'),
-- 시스템분석설계: 수요일 10-12교시 (17:15~19:55) - 4호관-403, 김상일
(8, '수', '17:15', '19:55');

-- ============================================
-- 5. 수강신청 정보 삽입 (202444085 학생이 이미지 시간표 기준 과목 수강)
-- ============================================
INSERT INTO enrollment (student_id, course_id) VALUES
('202444085', 1),  -- JAVA프로그래밍
('202444085', 2),  -- Oracle SQL&PL/SQL
('202444085', 3),  -- JSP
('202444085', 4),  -- 운영체제
('202444085', 5),  -- 정보보안
('202444085', 6),  -- S/W프로젝트
('202444085', 8);  -- 시스템분석설계

-- 다른 학생들도 일부 과목 수강
INSERT INTO enrollment (student_id, course_id) VALUES
('202444086', 1),  -- JAVA프로그래밍
('202444086', 2),  -- Oracle SQL&PL/SQL
('202444086', 3),  -- JSP
('202444087', 1),  -- JAVA프로그래밍
('202444087', 4),  -- 운영체제
('202444088', 2),  -- Oracle SQL&PL/SQL
('202444088', 3);  -- JSP

