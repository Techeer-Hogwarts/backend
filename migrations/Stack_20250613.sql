ALTER TABLE "Stack"
ALTER COLUMN category TYPE varchar USING category::text;