CREATE TABLE "TechBloggingTerm" (       
    "id" BIGSERIAL PRIMARY KEY,
    "year" INTEGER NOT NULL,
    "firstHalf" BOOLEAN NOT NULL,
    "isDeleted" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "TechBloggingRound" (
    "id" BIGSERIAL PRIMARY KEY,
    "startDate" DATE NOT NULL,
    "endDate" DATE NOT NULL,
    "sequence" INTEGER NOT NULL,
    "isDeleted" BOOLEAN NOT NULL DEFAULT FALSE,
    "termId" BIGINT NOT NULL,
    "createdAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("termId") REFERENCES "TechBloggingTerm" ("id")
);

CREATE TABLE "TechBloggingTermParticipant" (
    "id" BIGSERIAL PRIMARY KEY,
    "termId" BIGINT NOT NULL,
    "userId" BIGINT NOT NULL,
    "isDeleted" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("termId") REFERENCES "TechBloggingTerm" ("id"),
    FOREIGN KEY ("userId") REFERENCES "User" ("id"),
    UNIQUE ("termId", "userId")
);

CREATE TABLE "TechBloggingAttendance" (
    "id" BIGSERIAL PRIMARY KEY,
    "blogId" BIGINT NOT NULL,
    "userId" BIGINT NOT NULL,
    "techBloggingRoundId" BIGINT NOT NULL,
    "isDeleted" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("blogId") REFERENCES "Blog" ("id"),
    FOREIGN KEY ("userId") REFERENCES "User" ("id"),
    FOREIGN KEY ("techBloggingRoundId") REFERENCES "TechBloggingRound" ("id")
);