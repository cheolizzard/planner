# 데이터베이스 설정 가이드

## 📌 빠른 시작 (팀원용)

**Windows 사용자:**
```bash
install_database.bat
```

**Mac/Linux 사용자:**
```bash
chmod +x install_database.sh
./install_database.sh
```

또는 **SETUP_DATABASE.md** 파일을 참고하세요.

## 파일 설명
- `database_schema.sql`: 데이터베이스 스키마 생성 (테이블 생성)
- `database_migration_add_status.sql`: 할일 상태(status) 컬럼 추가
- `update_timetable_data.sql`: 최신 시간표 데이터 삽입 (권장)
- `database_dummy_data.sql`: 구버전 더미 데이터 (사용 안 함)

## 실행 방법

### 방법 1: MySQL Command Line 사용
```bash
# MySQL에 접속
mysql -u root -p

# 스키마 실행
source database_schema.sql;

# 더미 데이터 삽입
source database_dummy_data.sql;
```

### 방법 2: MySQL Workbench 사용
1. MySQL Workbench 실행
2. `database_schema.sql` 파일 열기
3. 전체 선택 후 실행 (Ctrl+Shift+Enter)
4. `database_dummy_data.sql` 파일 열기
5. 전체 선택 후 실행

### 방법 3: 명령어로 직접 실행
```bash
mysql -u root -p < database_schema.sql
mysql -u root -p < database_dummy_data.sql
```

## 데이터베이스 정보
- 데이터베이스명: `scalable_todo_db`
- 사용자: `root` (dbconfig.properties에 설정된 사용자)
- 포트: `3306` (기본값)

## 더미 데이터 내용

### 학생 계정
- 학번: `202444085`, 비밀번호: `password123`, 이름: `김철중` (C반)
- 학번: `202444086`, 비밀번호: `password123`, 이름: `이영희` (C반)
- 학번: `202444087`, 비밀번호: `password123`, 이름: `박민수` (C반)
- 학번: `202444088`, 비밀번호: `password123`, 이름: `최지영` (C반)

### 기본 과목 (7개)
1. 자바 프로그래밍 (월 09:00-12:00)
2. 데이터베이스 (화 09:00-12:00)
3. 웹 프로그래밍 (수 09:00-12:00)
4. 운영체제 (목 09:00-12:00)
5. 컴퓨터 네트워크 (금 09:00-12:00)
6. 소프트웨어 공학 (월 13:00-16:00)
7. 알고리즘 (화 13:00-16:00)

### 할일 데이터
- 202444085 학생에게 과목 관련 할일 5개
- 사용자 정의 카테고리 할일 5개 (기타, 개인)
- 다른 학생들의 할일 2개

## 데이터 확인 쿼리
```sql
USE scalable_todo_db;

-- 모든 테이블 확인
SHOW TABLES;

-- 학생 정보 확인
SELECT * FROM student;

-- 수강신청 정보 확인
SELECT s.student_id, s.name, sub.subject_name, c.classroom
FROM enrollment e
JOIN student s ON e.student_id = s.student_id
JOIN course c ON e.course_id = c.course_id
JOIN subject sub ON c.subject_id = sub.subject_id;

-- 할일 확인
SELECT t.todo_id, t.title, t.is_completed, 
       CASE 
           WHEN t.enroll_id IS NOT NULL THEN sub.subject_name
           ELSE t.custom_category
       END AS category
FROM todo_list t
LEFT JOIN enrollment e ON t.enroll_id = e.enroll_id
LEFT JOIN course c ON e.course_id = c.course_id
LEFT JOIN subject sub ON c.subject_id = sub.subject_id
WHERE t.student_id = '202444085';
```

