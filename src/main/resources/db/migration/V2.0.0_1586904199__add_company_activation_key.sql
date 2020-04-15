ALTER TABLE "company" ADD COLUMN "auth_subject_uuid" varchar;
ALTER TABLE "company" ADD COLUMN "activation_key" varchar;
ALTER TABLE "company" ADD COLUMN "dt_activation_key_expires_at" timestamp;
ALTER TABLE "company" ADD COLUMN "fl_email_verified" boolean default false;
