/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
alter table laboratory.samples rename column quantity to quantity_string;
ALTER TABLE laboratory.samples ADD quantity double precision;
