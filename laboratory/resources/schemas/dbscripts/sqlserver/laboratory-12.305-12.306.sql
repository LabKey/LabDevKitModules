ALTER TABLE laboratory.species ADD rowid INT identity(1,1);
ALTER TABLE laboratory.species ADD container entityid;
ALTER TABLE laboratory.species ADD created datetime;
ALTER TABLE laboratory.species ADD createdby int;
ALTER TABLE laboratory.species ADD modified datetime;
ALTER TABLE laboratory.species ADD modifiedby int;
GO
UPDATE laboratory.species SET container = (SELECT entityid FROM core.containers WHERE name = 'Shared');

ALTER TABLE laboratory.species DROP CONSTRAINT PK_species;
ALTER TABLE laboratory.species ADD CONSTRAINT PK_species PRIMARY KEY (rowid);