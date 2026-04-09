CREATE TABLE devices (
                         id         UUID        NOT NULL DEFAULT gen_random_uuid(),
                         name       VARCHAR(255) NOT NULL,
                         brand      VARCHAR(255) NOT NULL,
                         state      VARCHAR(50)  NOT NULL,
                         created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

                         CONSTRAINT pk_devices PRIMARY KEY (id),
                         CONSTRAINT chk_devices_state CHECK (state IN ('AVAILABLE', 'IN_USE', 'INACTIVE'))
);
