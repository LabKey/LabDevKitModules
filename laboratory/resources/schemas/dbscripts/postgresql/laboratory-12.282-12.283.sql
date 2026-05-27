/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
ALTER table laboratory.dna_oligos add cognate_primer_name varchar(100);
ALTER table laboratory.dna_oligos add oligo_id integer;
ALTER table laboratory.dna_oligos drop column cognate_primer;