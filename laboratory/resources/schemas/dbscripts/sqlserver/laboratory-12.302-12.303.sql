/*
 * Copyright (c) 2015-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER TABLE laboratory.samples ADD lsid LsidType;
ALTER TABLE laboratory.dna_oligos ADD lsid LsidType;
ALTER TABLE laboratory.subjects ADD lsid LsidType;
ALTER TABLE laboratory.antibodies ADD lsid LsidType;