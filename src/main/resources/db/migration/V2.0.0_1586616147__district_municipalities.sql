CREATE TABLE "district" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid,
  "code" varchar,
  "name" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "fl_deleted" boolean default false
);

CREATE TABLE "municipality" (
  "id" SERIAL PRIMARY KEY,
  "uuid" uuid,
  "id_district" int,
  "code" varchar,
  "name" varchar,
  "created_at" timestamp,
  "updated_at" timestamp,
  "fl_deleted" boolean default false
);

ALTER TABLE "posting" DROP COLUMN "district";
ALTER TABLE "posting" DROP COLUMN "city";
ALTER TABLE "posting" ADD COLUMN "id_district" int;
ALTER TABLE "posting" ADD COLUMN "id_municipality" int;

ALTER TABLE "municipality" ADD FOREIGN KEY ("id_district") REFERENCES "district" ("id");
ALTER TABLE "posting" ADD FOREIGN KEY ("id_district") REFERENCES "district" ("id");
ALTER TABLE "posting" ADD FOREIGN KEY ("id_municipality") REFERENCES "municipality" ("id");
