ALTER TABLE "prospect" ADD COLUMN "fl_deleted" boolean default false;
ALTER TABLE "company" ADD COLUMN "fl_deleted" boolean default false;
ALTER TABLE "business_area" ADD COLUMN "fl_deleted" boolean default false;
ALTER TABLE "posting" ADD COLUMN "fl_deleted" boolean default false;
ALTER TABLE "posting_job" ADD COLUMN "fl_deleted" boolean default false;
ALTER TABLE "job" ADD COLUMN "fl_deleted" boolean default false;
