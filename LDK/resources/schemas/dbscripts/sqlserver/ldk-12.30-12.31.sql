/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE ldk.ldapSyncMap (
  rowid int identity(1,1),
  provider varchar(1000),
  sourceId varchar(1000),
  labkeyId int,
  type char(1),

  created datetime
);