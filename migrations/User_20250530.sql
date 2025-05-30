-- 권한 저장 시, enum 말고 varchar로 저장
ALTER TABLE "PermissionRequest" ALTER COLUMN "status" TYPE VARCHAR;

-- 경력 설명란 필드 추가 / 기존 데이터들은 null 값
ALTER TABLE "UserExperience" ADD COLUMN "description" TEXT;