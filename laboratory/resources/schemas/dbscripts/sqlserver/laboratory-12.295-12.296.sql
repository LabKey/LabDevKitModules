/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE laboratory.antibodies (
  rowid int identity(1,1),
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
  created datetime,
  modifiedBy integer,
  modified datetime,

  CONSTRAINT PK_antibodies PRIMARY KEY (rowid)
);