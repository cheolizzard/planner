-- ============================================
-- 학과 연동 플래너 데이터베이스 스키마
-- ============================================

-- 1. 데이터베이스 생성 (이미 있으면 건너뜀)
CREATE DATABASE IF NOT EXISTS scalable_todo_db;

-- 2. 해당 데이터베이스 사용 선언
USE scalable_todo_db;

-- 3. 반 정보 테이블 (가장 먼저 생성 - student가 참조함)
CREATE TABLE IF NOT EXISTS class_info (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(20) NOT NULL
);

-- 반 정보 기초 데이터 삽입
INSERT INTO class_info (class_name) VALUES ('A반'), ('B반'), ('C반');

-- 4. 학생 테이블
CREATE TABLE IF NOT EXISTS student (
    student_id VARCHAR(20) PRIMARY KEY,
    class_id INT,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(20) NOT NULL,
    department VARCHAR(30) DEFAULT '컴퓨터 정보학과',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES class_info(class_id)
);

-- 5. 교수 테이블
CREATE TABLE IF NOT EXISTS professor (
    professor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    office_location VARCHAR(50)
);

-- 6. 과목 테이블
CREATE TABLE IF NOT EXISTS subject (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(50) NOT NULL,
    credits INT DEFAULT 3
);

-- 7. 강의(개설 강좌) 테이블
CREATE TABLE IF NOT EXISTS course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT NOT NULL,
    professor_id INT, 
    classroom VARCHAR(30),
    semester VARCHAR(20) DEFAULT '2025-1',
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id),
    FOREIGN KEY (professor_id) REFERENCES professor(professor_id)
);

-- 8. 강의 시간 테이블 (요일/교시)
CREATE TABLE IF NOT EXISTS lecture_time (
    time_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    day_of_week VARCHAR(3) NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

-- 9. 수강신청(Enrollment) 테이블
CREATE TABLE IF NOT EXISTS enrollment (
    enroll_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

-- 10. 할 일(To-Do) 테이블
CREATE TABLE IF NOT EXISTS todo_list (
    todo_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    enroll_id INT,  -- NULL이면 '개인/기타' 카테고리, 값이 있으면 '과목' 카테고리
    custom_category VARCHAR(50), -- 사용자가 직접 입력한 카테고리 이름 (enroll_id가 없을 때 사용)
    title VARCHAR(100) NOT NULL,
    content TEXT,
    start_datetime VARCHAR(20),
    end_datetime VARCHAR(20),
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (enroll_id) REFERENCES enrollment(enroll_id) ON DELETE SET NULL
);

