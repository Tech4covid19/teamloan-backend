CREATE TABLE "prospect" (
  "id" SERIAL PRIMARY KEY,
  "email" varchar,
  "created_at" timestamp
);

CREATE TABLE "business_entity" (
  "id" SERIAL PRIMARY KEY,
  "id_business_type" int,
  "name" varchar,
  "email" varchar,
  "vat" varchar,
  "phone" varchar,
  "district" varchar,
  "city" varchar,
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "business_type" (
  "id" SERIAL PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "posting" (
  "id" SERIAL PRIMARY KEY,
  "id_business_entity" int,
  "title" varchar,
  "phone" varchar,
  "email" varchar,
  "district" varchar,
  "city" varchar,
  "fl_is_need" boolean,
  "created_at" timestamp,
  "updated_at" timestamp,
  "fl_cancelled" boolean,
  "canceled_at" timestamp,
  "fl_matched" boolean,
  "matched_at" timestamp
);

CREATE TABLE "posting_people" (
  "id" SERIAL PRIMARY KEY,
  "id_posting" int,
  "job" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "fl_cancelled" boolean,
  "canceled_at" timestamp,
  "fl_matched" boolean,
  "matched_at" timestamp,
  "id_match" int
);

ALTER TABLE "business_entity" ADD FOREIGN KEY ("id_business_type") REFERENCES "business_type" ("id");

ALTER TABLE "posting" ADD FOREIGN KEY ("id_business_entity") REFERENCES "business_entity" ("id");

ALTER TABLE "posting_people" ADD FOREIGN KEY ("id_posting") REFERENCES "posting" ("id");

ALTER TABLE "posting_people" ADD FOREIGN KEY ("id_match") REFERENCES "posting_people" ("id");
