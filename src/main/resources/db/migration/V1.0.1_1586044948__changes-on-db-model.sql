DROP TABLE business_entity;
DROP TABLE business_type;
DROP TABLE posting;
DROP TABLE posting_people;
DROP TABLE job;

CREATE TABLE "company" (
  "id" SERIAL PRIMARY KEY,
  "id_business_area" int,
  "name" varchar,
  "email" varchar,
  "vat" varchar,
  "phone" varchar,
  "district" varchar,
  "city" varchar,
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "business_area" (
  "id" SERIAL PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "posting" (
  "id" SERIAL PRIMARY KEY,
  "id_company" int,
  "intention" varchar,
  "title" varchar,
  "phone" varchar,
  "email" varchar,
  "district" varchar,
  "city" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "status" varchar,
  "updated_status_at" timestamp
);

CREATE TABLE "posting_job" (
  "id" SERIAL PRIMARY KEY,
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
  "name" varchar,
  "created_at" timestamp,
  "updated_at" timestamp
);

ALTER TABLE "company" ADD FOREIGN KEY ("id_business_area") REFERENCES "business_area" ("id");

ALTER TABLE "posting" ADD FOREIGN KEY ("id_company") REFERENCES "company" ("id");

ALTER TABLE "posting_job" ADD FOREIGN KEY ("id_posting") REFERENCES "posting" ("id");

ALTER TABLE "posting_job" ADD FOREIGN KEY ("id_job") REFERENCES "job" ("id");
