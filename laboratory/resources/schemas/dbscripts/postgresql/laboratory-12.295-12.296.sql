/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE laboratory.antibodies (
  rowid serial NOT NULL,
  antibodyId int,
  name varchar(200),
  shortname varchar(200),
  antigen varchar(200),
  concentration float,

  clonality varchar(100),
  species varchar(200),
  vendor varchar(200),
  comment varchar(4000),

  container entityid NOT NULL,
  createdBy integer,
  created timestamp,
  modifiedBy integer,
  modified timestamp,

  CONSTRAINT PK_antibodies PRIMARY KEY (rowid)
);