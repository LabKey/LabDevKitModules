/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER table laboratory.antibodies ADD source varchar(500);
ALTER table laboratory.antibodies ADD catalog varchar(500);
ALTER table laboratory.antibodies ADD location varchar(500);
ALTER table laboratory.antibodies ADD specificity varchar(500);
ALTER table laboratory.antibodies ADD mw double precision;
ALTER table laboratory.antibodies ADD applications varchar(1000);

UPDATE laboratory.antibodies SET source = vendor;
ALTER table laboratory.antibodies DROP COLUMN vendor;