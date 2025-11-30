-- ============================================
-- 할일 상태(status) 컬럼 추가 마이그레이션
-- ============================================
-- 실행 순서: 이 파일을 MySQL에서 실행하세요

USE scalable_todo_db;

-- 1. status 컬럼 추가
ALTER TABLE todo_list 
ADD COLUMN status VARCHAR(10) DEFAULT '미완료' 
COMMENT '할일 상태: 미완료, 진행중, 완료';

-- 2. 기존 데이터 업데이트
-- is_completed가 TRUE면 '완료'로 설정
UPDATE todo_list 
SET status = '완료' 
WHERE is_completed = TRUE;

-- is_completed가 FALSE이고 시간 정보가 있으면 상태 판단
-- 현재 시간 기준으로 진행중 판단
UPDATE todo_list 
SET status = CASE
    WHEN is_completed = FALSE AND start_datetime IS NOT NULL AND end_datetime IS NOT NULL THEN
        CASE
            WHEN NOW() < STR_TO_DATE(start_datetime, '%Y-%m-%d %H:%i:%s') THEN '미완료'
            WHEN NOW() BETWEEN STR_TO_DATE(start_datetime, '%Y-%m-%d %H:%i:%s') 
                          AND STR_TO_DATE(end_datetime, '%Y-%m-%d %H:%i:%s') THEN '진행중'
            WHEN NOW() > STR_TO_DATE(end_datetime, '%Y-%m-%d %H:%i:%s') THEN '미완료'
            ELSE '미완료'
        END
    WHEN is_completed = FALSE THEN '미완료'
    ELSE status
END
WHERE status = '미완료' OR status IS NULL;

-- 3. 확인 쿼리 (실행 후 상태 확인용)
-- SELECT todo_id, title, is_completed, status, start_datetime, end_datetime FROM todo_list;

