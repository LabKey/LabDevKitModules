/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.samples ADD passage_number integer;

ALTER TABLE laboratory.sample_type ADD container entityid;
UPDATE laboratory.sample_type
SET container = (SELECT entityid FROM core.containers c WHERE c.name = 'Shared' and Parent = (select EntityId from core.Containers c2 WHERE c2.Parent is null));
