-- ============================================
-- 중복된 반 정보 정리
-- ============================================

USE scalable_todo_db;

-- 방법 1: 중복 제거 (class_name 기준으로 가장 작은 class_id만 유지)
DELETE t1 FROM class_info t1
INNER JOIN class_info t2 
WHERE t1.class_id > t2.class_id AND t1.class_name = t2.class_name;

-- 확인: 남은 반 정보 확인
SELECT * FROM class_info ORDER BY class_id;

