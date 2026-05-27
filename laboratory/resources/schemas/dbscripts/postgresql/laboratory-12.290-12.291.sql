/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.sample_type DROP CONSTRAINT PK_sample_type;

ALTER TABLE laboratory.sample_type ADD rowid serial;

ALTER TABLE laboratory.sample_type ADD CONSTRAINT PK_sample_type PRIMARY KEY (rowid);