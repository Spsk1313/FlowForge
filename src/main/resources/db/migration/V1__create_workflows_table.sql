CREATE TABLE workflows (
   id BIGSERIAL,
   name VARCHAR(100) NOT NULL,
   description VARCHAR(500),
   status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT pk_workflows PRIMARY KEY (id),

   CONSTRAINT ck_workflows_name_not_blank
       CHECK (btrim(name) <> ''),

   CONSTRAINT ck_workflows_status
       CHECK (status IN ('DRAFT', 'ACTIVE'))
);