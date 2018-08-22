-- DICTIONARY

CREATE TABLE IF NOT EXISTS property_descriptor (
	id BIGINT AUTO_INCREMENT NOT NULL,
	entity VARCHAR(256) NOT NULL,
	name VARCHAR(4096),
	description CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS property_descriptor_idx
	ON property_descriptor (entity);

ALTER TABLE property_descriptor
	ADD CONSTRAINT IF NOT EXISTS property_descriptor_entity_ck
	CHECK (entity in ('meta', 'author', 'work', 'piece', 'session'));

CREATE UNIQUE INDEX IF NOT EXISTS property_descriptor_idx2
	ON property_descriptor (entity, name);

-- PROPERTY

CREATE TABLE IF NOT EXISTS property (
	id BIGINT AUTO_INCREMENT NOT NULL,
	property_descriptor_id BIGINT,
	value CLOB,
	PRIMARY KEY(id),
	FOREIGN KEY(property_descriptor_id) REFERENCES property_descriptor(id)
);

-- AUTHOR

CREATE TABLE IF NOT EXISTS author (
	id BIGINT AUTO_INCREMENT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS author_property (
	property_id BIGINT NOT NULL,
	author_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, author_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(author_id) REFERENCES author(id)
);

-- WORK

CREATE TABLE IF NOT EXISTS work (
	id BIGINT AUTO_INCREMENT NOT NULL,
	pricing_id BIGINT NOT NULL,
	author_id BIGINT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS work_idx
	ON work (name); 

CREATE TABLE IF NOT EXISTS work_property (
	property_id BIGINT NOT NULL,
	work_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, work_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(work_id) REFERENCES work(id)
);

-- PIECE

CREATE TABLE IF NOT EXISTS piece (
	id BIGINT AUTO_INCREMENT NOT NULL,
	work_id BIGINT NOT NULL,
	name VARCHAR(4096),
	previous_id BIGINT,
	duration BIGINT,
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS piece_idx
	ON piece (name); 

CREATE TABLE IF NOT EXISTS piece_property (
	property_id BIGINT NOT NULL,
	piece_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, piece_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(piece_id) REFERENCES piece(id)
);

-- SESSION

CREATE TABLE IF NOT EXISTS session (
	id BIGINT AUTO_INCREMENT NOT NULL,
	piece_id BIGINT NOT NULL,
	start TIMESTAMP NOT NULL,
	stop TIMESTAMP,
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS session_property (
	property_id BIGINT NOT NULL,
	session_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, session_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(session_id) REFERENCES session(id)
);

-- PRICING

CREATE TABLE IF NOT EXISTS pricing (
	id BIGINT AUTO_INCREMENT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS pricing_property (
	property_id BIGINT NOT NULL,
	pricing_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, pricing_id),
	FOREIGN KEY(property_id) REFERENCES property(id),
	FOREIGN KEY(pricing_id) REFERENCES pricing(id)
);

-- META

CREATE TABLE IF NOT EXISTS meta_property (
	property_id BIGINT NOT NULL,
	PRIMARY KEY(property_id),
	FOREIGN KEY(property_id) REFERENCES property(id)
);

-- Set initial version if not already set

INSERT INTO property_descriptor (entity, name, description)
	SELECT entity, name, description FROM (
		SELECT 'meta' AS entity, 'VERSION' AS name, 'Database schema version' AS description
	) sub
	WHERE NOT EXISTS (
		SELECT entity, name, description
		FROM property_descriptor p
		WHERE sub.entity = p.entity
		AND sub.name = p.name
	);

INSERT INTO property (property_descriptor_id, value)
	SELECT property_descriptor_id, value FROM (
		SELECT pd.id AS property_descriptor_id, '1.0.0' AS value
		FROM property_descriptor pd
		WHERE pd.entity = 'meta'
		AND pd.name = 'VERSION'
	) sub
	WHERE NOT EXISTS (
		SELECT property_descriptor_id, value
		FROM property p
		WHERE sub.property_descriptor_id = p.property_descriptor_id
	);

MERGE INTO meta_property (property_id)
	SELECT MIN(p.id)
	FROM property p, property_descriptor pd
	WHERE pd.name = 'VERSION'
	AND pd.entity = 'meta'
	AND p.property_descriptor_id = pd.id;
