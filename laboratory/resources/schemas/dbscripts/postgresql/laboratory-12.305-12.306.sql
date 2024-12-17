ALTER TABLE laboratory.species ADD rowid SERIAL;
ALTER TABLE laboratory.species ADD container entityid;
ALTER TABLE laboratory.species ADD created timestamp;
ALTER TABLE laboratory.species ADD createdby int;
ALTER TABLE laboratory.species ADD modified timestamp;
ALTER TABLE laboratory.species ADD modifiedby int;

UPDATE laboratory.species SET container = (SELECT entityid FROM core.containers WHERE name = 'Shared');

ALTER TABLE laboratory.species DROP CONSTRAINT PK_species;
ALTER TABLE laboratory.species ADD CONSTRAINT PK_species PRIMARY KEY (rowid);