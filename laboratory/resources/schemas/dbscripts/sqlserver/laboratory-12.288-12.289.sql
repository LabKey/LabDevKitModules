/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.workbook_tags DROP COLUMN created;
ALTER TABLE laboratory.workbook_tags DROP COLUMN createdby;

GO

ALTER TABLE laboratory.workbook_tags ADD created datetime;
ALTER TABLE laboratory.workbook_tags ADD createdby int;