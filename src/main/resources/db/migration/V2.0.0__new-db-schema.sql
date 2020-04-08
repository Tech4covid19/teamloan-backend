DROP TABLE posting_people;
DROP TABLE posting;
DROP TABLE business_entity;
DROP TABLE business_type;

CREATE TABLE "company" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid NOT NULL,
  "id_business_area" int,
  "name" varchar,
  "email" varchar,
  "vat" varchar,
  "phone" varchar,
  "zip_code" varchar,
  "responsible" varchar,
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "business_area" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid NOT NULL,
  "name" varchar
);

CREATE TABLE "posting" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid NOT NULL,
  "id_company" int,
  "intention" varchar,
  "title" varchar,
  "phone" varchar,
  "email" varchar,
  "district" varchar,
  "city" varchar,
  "zip_code" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "status" varchar,
  "updated_status_at" timestamp
);

CREATE TABLE "posting_job" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid NOT NULL,
  "id_posting" int,
  "id_job" int,
  "other_job" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "status" varchar,
  "updated_status_at" timestamp
);

CREATE TABLE "job" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid NOT NULL,
  "name" varchar,
  "created_at" timestamp,
  "updated_at" timestamp
);

ALTER TABLE "company" ADD FOREIGN KEY ("id_business_area") REFERENCES "business_area" ("id");
ALTER TABLE "posting" ADD FOREIGN KEY ("id_company") REFERENCES "company" ("id");
ALTER TABLE "posting_job" ADD FOREIGN KEY ("id_posting") REFERENCES "posting" ("id");
ALTER TABLE "posting_job" ADD FOREIGN KEY ("id_job") REFERENCES "job" ("id");

CREATE INDEX IF NOT EXISTS "company_business_area_idx" ON company("id_business_area");
CREATE INDEX IF NOT EXISTS "posting_id_company_idx" ON posting("id_company");
CREATE INDEX IF NOT EXISTS "posting_job_id_posting_idx" ON posting_job("id_posting");
CREATE INDEX IF NOT EXISTS "posting_job_id_job_idx" ON posting_job("id_job");

CREATE UNIQUE INDEX IF NOT EXISTS "company_uuid_idx" ON company("uuid");
CREATE UNIQUE INDEX IF NOT EXISTS "business_area_uuid_idx" ON business_area("uuid");
CREATE UNIQUE INDEX IF NOT EXISTS "posting_uuid_idx" ON posting("uuid");
CREATE UNIQUE INDEX IF NOT EXISTS "posting_job_uuid_idx" ON posting_job("uuid");
CREATE UNIQUE INDEX IF NOT EXISTS "job_uuid_idx" ON job("uuid");