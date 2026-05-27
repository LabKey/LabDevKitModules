/*
 * Copyright (c) 2022-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
-- Cleanup legacy orphan rows
delete from laboratory.workbooks WHERE (SELECT c.entityid from core.containers c where c.entityid = container) is null;
