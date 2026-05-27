/*
 * Copyright (c) 2025-2026 LabKey Corporation
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
package org.labkey.laboratory.query;

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.TableInfo;
import org.labkey.api.laboratory.TabbedReportItem;
import org.labkey.api.laboratory.query.TabbedReportFilterProvider;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.FieldKey;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.laboratory.LaboratoryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectGroupFilterProvider implements TabbedReportFilterProvider
{
    @Override
    public Module getOwningModule()
    {
        return ModuleLoader.getInstance().getModule(LaboratoryModule.class);
    }

    @Override
    public Collection<ClientDependency> getClientDependencies()
    {
        return List.of(ClientDependency.fromPath("laboratory/panel/ProjectFilterType.js"));
    }

    @Override
    public String getXType()
    {
        return "laboratory-projectfiltertype";
    }

    @Override
    public String getLabel()
    {
        return "Subject Groups";
    }

    @Override
    public String getInputValue()
    {
        return "projects";
    }

    @Override
    public @NotNull Map<String, FieldKey> getAdditionalFieldKeys(TableInfo ti, TabbedReportItem tri, Map<String, FieldKey> overrides)
    {
        Map<String, FieldKey> ret = new HashMap<>();
        FieldKey parent = tri.getSubjectIdFieldKey();
        if (parent != null)
        {
            parent = parent.getParent();
        }

        if (overrides.get("overlappingProjectsFieldKey") == null)
        {
            FieldKey overlapKey = FieldKey.fromString(parent, "overlappingProjectsPivot");

            Map<FieldKey, ColumnInfo> colMap = tri.getQueryCache().getColumns(ti, PageFlowUtil.set(overlapKey));
            if (colMap.containsKey(overlapKey))
                ret.put("overlappingProjectsFieldKey", colMap.get(overlapKey).getFieldKey());
        }

        if (overrides.get("allProjectsFieldKey") == null)
        {
            FieldKey allKey = FieldKey.fromString(parent, "allProjectsPivot");

            Map<FieldKey, ColumnInfo> colMap = tri.getQueryCache().getColumns(ti, PageFlowUtil.set(allKey));
            if (colMap.containsKey(allKey))
                ret.put("allProjectsFieldKey", colMap.get(allKey).getFieldKey());
        }

        return ret;
    }
}
