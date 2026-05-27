/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
SELECT
well_96 as well,
addressbyrow_96 as addressByRow,
addressbycolumn_96 as addressByColumn

FROM laboratory.well_layout p
WHERE p.plate = 1