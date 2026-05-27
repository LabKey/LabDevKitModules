/*
 * Copyright (c) 2024-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.species ADD rowid SERIAL;
ALTER TABLE laboratory.species ADD container entityid;
ALTER TABLE laboratory.species ADD created timestamp;
ALTER TABLE laboratory.species ADD createdby int;
ALTER TABLE laboratory.species ADD modified timestamp;
ALTER TABLE laboratory.species ADD modifiedby int;

UPDATE laboratory.species SET container = (SELECT entityid FROM core.containers c1 WHERE name = 'Shared' and (select parent from core.Containers c2 where c2.EntityId = c1.Parent) is null);

ALTER TABLE laboratory.species DROP CONSTRAINT PK_species;
ALTER TABLE laboratory.species ADD CONSTRAINT PK_species PRIMARY KEY (rowid);