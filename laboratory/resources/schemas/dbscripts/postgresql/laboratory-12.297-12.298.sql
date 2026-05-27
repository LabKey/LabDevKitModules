/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
-- @SkipOnEmptySchemasBegin
DELETE FROM laboratory.result_status WHERE status = 'No Data';
INSERT INTO laboratory.result_status (status) VALUES ('No Data');
-- @SkipOnEmptySchemasEnd
