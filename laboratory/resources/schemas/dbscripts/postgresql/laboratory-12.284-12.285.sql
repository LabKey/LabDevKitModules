/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
DROP TABLE laboratory.workbook_group_members;

CREATE TABLE laboratory.workbook_tags (
  rowid serial,
  tag varchar(200),
  container entityid,
  created int,
  createdby timestamp,

  CONSTRAINT pk_workbook_tags PRIMARY KEY (rowid)
);
