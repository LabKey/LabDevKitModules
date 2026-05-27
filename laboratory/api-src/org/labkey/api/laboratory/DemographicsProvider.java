/*
 * Copyright (c) 2024-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.api.laboratory;

import org.jetbrains.annotations.Nullable;
import org.labkey.api.data.Container;
import org.labkey.api.module.Module;
import org.labkey.api.security.User;


public class DemographicsProvider
{
    private final Module _owningModule;
    private final String _schemaName;
    private final String _queryName;
    private final String _subjectField;

    public DemographicsProvider(Module owningModule, String schemaName, String queryName, String subjectField)
    {
        _owningModule = owningModule;
        _schemaName = schemaName;
        _queryName = queryName;
        _subjectField = subjectField;
    }

    public String getSchema()
    {
        return _schemaName;
    }

    public String getQuery()
    {
        return _queryName;
    }

    public String getSubjectField()
    {
        return _subjectField;
    }

    public @Nullable String getMotherField()
    {
        return null;
    }

    public @Nullable String getFatherField()
    {
        return null;
    }

    public @Nullable String getSexField()
    {
        return null;
    }

    public boolean isAvailable(Container c, User u)
    {
        return c.getActiveModules().contains(_owningModule);
    }

    public  String getLabel()
    {
        return getSchema() + "." + getQuery();
    }

    public  boolean isValidForPedigree()
    {
        return getMotherField() != null && getFatherField() != null && getSexField() != null;
    }
}