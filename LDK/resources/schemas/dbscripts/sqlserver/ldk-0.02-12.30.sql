/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE ldk.notificationrecipients (
  rowid INT IDENTITY(1,1) NOT NULL,
  notificationtype varchar(200),
  recipient integer,

  container entityid NOT NULL,
  createdby userid NOT NULL,
  created datetime,
  modifiedby userid NOT NULL,
  modified datetime,

  CONSTRAINT pk_notificationrecipients PRIMARY KEY (rowid)
);

--note: field lengths altered
CREATE TABLE ldk.notificationtypes (
  notificationtype varchar(200) NOT NULL,
  description text,
  CONSTRAINT pk_notificationtypes PRIMARY KEY (notificationtype)
);

