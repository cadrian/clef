CREATE TABLE IF NOT EXISTS property (
	id BIGINT AUTO_INCREMENT NOT NULL,
	name VARCHAR(4096),
	value CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS property_idx ON property (name); 

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
	PRIMARY KEY(property_id, session_id)
);

CREATE INDEX IF NOT EXISTS session_property_idx ON session_property (session_id); 

CREATE TABLE IF NOT EXISTS piece (
	id BIGINT AUTO_INCREMENT NOT NULL,
	work_id BIGINT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS piece_idx ON piece (name); 

CREATE TABLE IF NOT EXISTS piece_property (
	property_id BIGINT NOT NULL,
	piece_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, piece_id)
);

CREATE INDEX IF NOT EXISTS piece_property_idx ON piece_property (piece_id); 

CREATE TABLE IF NOT EXISTS work (
	id BIGINT AUTO_INCREMENT NOT NULL,
	pricing_id BIGINT NOT NULL,
	author_id BIGINT NOT NULL,
	name VARCHAR(4096),
	notes CLOB,
	PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS work_idx ON work (name); 

CREATE TABLE IF NOT EXISTS work_property (
	property_id BIGINT NOT NULL,
	work_id BIGINT NOT NULL,
	PRIMARY KEY(property_id, work_id)
);

CREATE INDEX IF NOT EXISTS work_property_idx ON work_property (work_id); 

CREATE TABLE IF NOT EXISTS author (
	id BIGINT AUTO_INCREMENT NOT NULL,
	name VARCHAR(4096),
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS pricing (
	id BIGINT AUTO_INCREMENT NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS meta_property (
	property_id BIGINT NOT NULL,
	PRIMARY KEY(property_id)
);

-- Set initial version if not already set

INSERT INTO property (name, value)
	SELECT name, value FROM (
		SELECT 'VERSION' AS name, '1.0.0' AS value
	) sub
	WHERE NOT EXISTS (
		SELECT name, value
		FROM property p
		WHERE sub.name = p.name
	);

MERGE INTO meta_property (property_id)
	SELECT MIN(id)
	FROM property
	WHERE name = 'VERSION';
