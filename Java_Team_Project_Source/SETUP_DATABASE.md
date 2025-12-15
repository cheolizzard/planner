# 데이터베이스 설치 가이드 (팀원용)

## 📋 필수 사항
- MySQL 5.7 이상 또는 MariaDB 10.3 이상
- MySQL Workbench 또는 MySQL Command Line Client

## 🚀 설치 순서

### 1단계: 데이터베이스 스키마 생성
먼저 테이블 구조를 생성합니다.

**MySQL Workbench 사용:**
1. MySQL Workbench 실행
2. `database_schema.sql` 파일 열기
3. 전체 선택 (Ctrl+A) 후 실행 (Ctrl+Shift+Enter)

**MySQL Command Line 사용:**
```bash
mysql -u root -p < database_schema.sql
```

### 2단계: Status 컬럼 추가 (마이그레이션)
할일 상태 기능을 위한 컬럼을 추가합니다.

**MySQL Workbench 사용:**
1. `database_migration_add_status.sql` 파일 열기
2. 전체 선택 후 실행

**MySQL Command Line 사용:**
```bash
mysql -u root -p scalable_todo_db < database_migration_add_status.sql
```

### 3단계: 시간표 데이터 삽입
최신 시간표 데이터를 삽입합니다.

**MySQL Workbench 사용:**
1. `update_timetable_data.sql` 파일 열기
2. 전체 선택 후 실행

**MySQL Command Line 사용:**
```bash
mysql -u root -p scalable_todo_db < update_timetable_data.sql
```

## ✅ 설치 확인

다음 쿼리로 설치가 제대로 되었는지 확인하세요:

```sql
USE scalable_todo_db;

-- 테이블 목록 확인
SHOW TABLES;

-- 학생 정보 확인
SELECT * FROM student;

-- 과목 정보 확인
SELECT s.subject_name, p.name as professor_name, c.classroom
FROM course c
JOIN subject s ON c.subject_id = s.subject_id
LEFT JOIN professor p ON c.professor_id = p.professor_id;

-- 강의 시간 확인
SELECT s.subject_name, lt.day_of_week, lt.start_time, lt.end_time
FROM lecture_time lt
JOIN course c ON lt.course_id = c.course_id
JOIN subject s ON c.subject_id = s.subject_id
ORDER BY lt.day_of_week, lt.start_time;
```

## 📝 기본 테스트 계정

설치 후 다음 계정으로 로그인할 수 있습니다:

- **학번**: `202444085`
- **비밀번호**: `password123`
- **이름**: 김철중

## ⚠️ 주의사항

1. **기존 데이터가 있는 경우**: 
   - `update_timetable_data.sql`은 기존 데이터를 삭제하고 새로 삽입합니다.
   - 기존 데이터를 보존하려면 백업 후 실행하세요.

2. **에러 발생 시**:
   - 외래키 제약 조건 오류가 발생하면, 파일 실행 순서를 확인하세요.
   - 데이터베이스가 이미 존재하는 경우, `DROP DATABASE scalable_todo_db;` 후 다시 생성하세요.

3. **재설치가 필요한 경우**:
   ```sql
   DROP DATABASE IF EXISTS scalable_todo_db;
   ```
   위 명령어로 데이터베이스를 삭제한 후 1단계부터 다시 실행하세요.

## 🔧 데이터베이스 연결 설정

프로그램 실행 전에 `src/DB/dbconfig.properties` 파일을 확인하세요:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/scalable_todo_db?characterEncoding=UTF-8&serverTimezone=UTC
db.user=root
db.password=your_password
```

`your_password`를 본인의 MySQL 비밀번호로 변경하세요.

