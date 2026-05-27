/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
--text datatype not sortable
--this is required since we cannot change datatype without dropping the constraint
declare @Command nvarchar(max) = '';
select @Command = @Command + 'ALTER TABLE laboratory.samples DROP CONSTRAINT ' + d.name
from sys.default_constraints d
where d.object_id = (SELECT constid FROM sysconstraints WHERE id=OBJECT_ID('laboratory.samples')
    AND COL_NAME(id,colid)='comment'
    AND OBJECTPROPERTY(constid, 'IsDefaultCnst')=1
    );

execute (@Command);


ALTER TABLE laboratory.samples ALTER COLUMN comment varchar(MAX);