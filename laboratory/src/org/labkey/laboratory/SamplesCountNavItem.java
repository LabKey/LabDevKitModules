/*
 * Copyright (c) 2013-2026 LabKey Corporation
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
package org.labkey.laboratory;

import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.laboratory.DataProvider;
import org.labkey.api.laboratory.LaboratoryService;
import org.labkey.api.laboratory.QueryCountNavItem;
import org.labkey.api.query.FieldKey;
import org.labkey.api.security.User;

public class SamplesCountNavItem extends QueryCountNavItem
{
    public SamplesCountNavItem(DataProvider provider, String schema, String query, LaboratoryService.NavItemCategory itemType, String category, String label)
    {
        super(provider, schema, query, itemType, category, label);
    }

    @Override
    protected SimpleFilter getFilter(Container c, User u, TableInfo ti)
    {
        SimpleFilter filter = super.getFilter(c, u, ti);
        filter.addCondition(FieldKey.fromString("dateremoved"), null, CompareType.ISBLANK);
        return filter;
    }
}
