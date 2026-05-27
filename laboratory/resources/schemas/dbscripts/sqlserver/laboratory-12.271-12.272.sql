/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.assay_run_templates ADD runid INTEGER;
ALTER TABLE laboratory.assay_run_templates ADD importMethod varchar(200);