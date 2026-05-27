/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE laboratory.project_usage (
  rowid serial,
  subjectId varchar(100),
  project varchar(100),
  groupname varchar(200),
  startdate timestamp,
  enddate timestamp,
  comment varchar(4000),

  container entityid NOT NULL,
  createdBy int,
  created timestamp,
  modifiedBy int,
  modified timestamp,

  CONSTRAINT PK_project_usage PRIMARY KEY (rowid)
);

