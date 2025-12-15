@echo off
chcp 65001 >nul
echo ========================================
echo 데이터베이스 설치 스크립트
echo ========================================
echo.

set /p DB_USER="MySQL 사용자명 (기본: root): "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASSWORD="MySQL 비밀번호: "

echo.
echo [1/3] 데이터베이스 스키마 생성 중...
mysql -u %DB_USER% -p%DB_PASSWORD% < database_schema.sql
if %errorlevel% neq 0 (
    echo 오류: 스키마 생성 실패
    pause
    exit /b 1
)
echo ✓ 스키마 생성 완료

echo.
echo [2/3] Status 컬럼 추가 중...
mysql -u %DB_USER% -p%DB_PASSWORD% scalable_todo_db < database_migration_add_status.sql
if %errorlevel% neq 0 (
    echo 경고: Status 컬럼 추가 실패 (이미 추가되었을 수 있음)
)
echo ✓ Status 컬럼 추가 완료

echo.
echo [3/3] 시간표 데이터 삽입 중...
mysql -u %DB_USER% -p%DB_PASSWORD% scalable_todo_db < update_timetable_data.sql
if %errorlevel% neq 0 (
    echo 오류: 데이터 삽입 실패
    pause
    exit /b 1
)
echo ✓ 데이터 삽입 완료

echo.
echo ========================================
echo 데이터베이스 설치 완료!
echo ========================================
echo.
echo 테스트 계정:
echo   학번: 202444085
echo   비밀번호: password123
echo.
pause

