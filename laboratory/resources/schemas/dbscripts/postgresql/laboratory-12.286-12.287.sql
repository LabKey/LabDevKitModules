/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
CREATE TABLE laboratory.result_status (
  status varchar(100),
  not_trusted boolean,

  CONSTRAINT PK_result_status PRIMARY KEY (status)
);

-- @SkipOnEmptySchemasBegin
INSERT INTO laboratory.result_status (status, not_trusted) VALUES ('Definitive', false);
INSERT INTO laboratory.result_status (status, not_trusted) VALUES ('Outlier', true);
INSERT INTO laboratory.result_status (status, not_trusted) VALUES ('Replaced', true);
-- @SkipOnEmptySchemasEnd
