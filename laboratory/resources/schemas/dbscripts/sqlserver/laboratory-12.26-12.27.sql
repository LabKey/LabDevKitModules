/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
INSERT into laboratory.samplecategory (category) VALUES ('Empty Well');

CREATE TABLE laboratory.plate_templates (
    rowid int identity(1,1),
    name varchar(200),
    category varchar(100),
    json text,

    container entityid,
    createdby integer,
    created datetime,
    modifiedby integer,
    modified datetime,

    constraint PK_plate_templates PRIMARY KEY (rowid)
);