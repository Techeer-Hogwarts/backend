-- 확장 설치 (최초 1회)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ProjectTeam에 UUID 컬럼 추가
ALTER TABLE "ProjectTeam"
ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();

-- StudyTeam에도 동일 적용
ALTER TABLE "StudyTeam"
ADD COLUMN uuid UUID DEFAULT uuid_generate_v4();

ALTER TABLE "ProjectTeam"
  ALTER COLUMN uuid DROP DEFAULT,
  ALTER COLUMN uuid TYPE uuid USING uuid::uuid,
  ALTER COLUMN uuid SET DEFAULT uuid_generate_v4();


-- PostgreSQL의 enum → varchar 변환
ALTER TABLE "StudyMember"
  ALTER COLUMN status TYPE varchar USING status::text;
ALTER TABLE "ProjectMember"
  ALTER COLUMN status TYPE varchar USING status::text;



UPDATE "ProjectMember"SET "teamRole" = 'FRONTEND'WHERE "teamRole" = 'Frontend';
UPDATE "ProjectMember" SET "teamRole" = 'BACKEND' WHERE "teamRole" = 'Backend';
UPDATE "ProjectMember" SET "teamRole" = 'FULLSTACK' WHERE "teamRole" = 'FullStack';
UPDATE "ProjectMember" SET "teamRole" = 'DEVOPS' WHERE "teamRole" = 'DevOps';
UPDATE "ProjectMember" SET "teamRole" = 'DATA_ENGINEER' WHERE "teamRole" = 'DataEngineer';

DROP VIEW "TeamUnionView";

CREATE VIEW "TeamUnionView" AS
SELECT
    pt.uuid AS "globalId",
    pt.id AS id,
    pt."viewCount",
    pt."likeCount",
    pt."isDeleted",
    pt."isRecruited",
    pt."isFinished",
    pt."createdAt",
    pt."updatedAt",
    'PROJECT' AS "teamType"
FROM "ProjectTeam" pt

UNION ALL

SELECT
    st.uuid::uuid AS "globalId",
    st.id AS id,
    st."viewCount",
    st."likeCount",
    st."isDeleted",
    st."isRecruited",
    st."isFinished",
    st."createdAt",
    st."updatedAt",
    'STUDY' AS "teamType"
FROM "StudyTeam" st;

CREATE VIEW "TeamUnionView" AS
SELECT
    pt.uuid AS "globalId",
    pt.id AS id,
    pt."viewCount",
    pt."likeCount",
    pt."isDeleted",
    pt."isRecruited",
    pt."isFinished",
    pt."createdAt",
    pt."updatedAt",
    'PROJECT' AS "teamType"
FROM "ProjectTeam" pt

UNION ALL

SELECT
    st.uuid::uuid AS "globalId",
    st.id AS id,
    st."viewCount",
    st."likeCount",
    st."isDeleted",
    st."isRecruited",
    st."isFinished",
    st."createdAt",
    st."updatedAt",
    'STUDY' AS "teamType"
FROM "StudyTeam" st;
