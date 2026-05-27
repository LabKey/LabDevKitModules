/*
 * Copyright (c) 2018-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
IF object_id(N'ldk.Naturalize', N'FS') IS NOT NULL
DROP FUNCTION ldk.Naturalize
GO

IF  EXISTS (SELECT * FROM sys.assemblies asms WHERE asms.name = N'Naturalize' and is_user_defined = 1)
DROP ASSEMBLY [Naturalize]
GO