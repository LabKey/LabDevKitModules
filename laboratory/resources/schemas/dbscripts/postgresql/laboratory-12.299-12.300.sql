/*
 * Copyright (c) 2014-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
-- @SkipOnEmptySchemasBegin
DELETE FROM laboratory.species WHERE common_name = 'Rabbit';
INSERT INTO laboratory.species (common_name, scientific_name) VALUES ('Rabbit', 'Lepus curpaeums');
-- @SkipOnEmptySchemasEnd
