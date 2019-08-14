-- ACTIVITY

CREATE TABLE activity (
	id BIGINT AUTO_INCREMENT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE TABLE activity_property (
	property_id BIGINT NOT NULL,
	activity_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, activity_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(activity_id) REFERENCES activity(id)
);

ALTER TABLE session ADD
	activity_id BIGINT NULL;

ALTER TABLE session ADD FOREIGN KEY (piece_id)
	REFERENCES piece(id);

ALTER TABLE session ADD FOREIGN KEY (activity_id)
	REFERENCES activity(id);

ALTER TABLE property_descriptor
	DROP CONSTRAINT property_descriptor_entity_ck;

ALTER TABLE property_descriptor
	ADD CONSTRAINT property_descriptor_entity_ck
	CHECK (entity in ('meta', 'pricing', 'activity', 'author', 'work', 'piece', 'session'));

-- Update version

UPDATE property
	SET value = '1.0.1'
	WHERE property_descriptor_id IN (
		SELECT id FROM property_descriptor pd
			WHERE pd.entity = 'meta'
			AND pd.name = 'VERSION'
	);
