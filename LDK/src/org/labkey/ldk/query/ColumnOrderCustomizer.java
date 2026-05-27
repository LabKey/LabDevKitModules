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
package org.labkey.ldk.query;

import org.labkey.api.data.AbstractTableInfo;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.MutableColumnInfo;
import org.labkey.api.data.TableCustomizer;
import org.labkey.api.data.TableInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColumnOrderCustomizer implements TableCustomizer
{
    public ColumnOrderCustomizer()
    {
    }

    @Override
    public void customize(TableInfo table)
    {
        if (table instanceof AbstractTableInfo)
        {
            sortColumns((AbstractTableInfo)table);

            setDefaultVisible((AbstractTableInfo)table);
        }
    }

    public void sortColumns(AbstractTableInfo table)
    {
        List<ColumnInfo> columns = new ArrayList<>();
        columns.addAll(table.getColumns());
        for (ColumnInfo c : columns)
            table.removeColumn(c);

        // sort the columns using the following rules:
        // put calculated columns at the end
        // respect the original sort order for non-calculated
        // alphabetize calculated columns
        columns.sort(new Comparator<ColumnInfo>()
        {
            @Override
            public int compare(ColumnInfo o1, ColumnInfo o2)
            {
                if (isReorderCandidate(o1) && !isReorderCandidate(o2))
                {
                    return 1;
                }
                else if (!isReorderCandidate(o1) && isReorderCandidate(o2))
                {
                    return -1;
                }
                else if (isReorderCandidate(o1))
                {
                    return o1.getLabel().compareTo(o2.getLabel());
                }

                return 0;
            }

            public boolean isReorderCandidate(ColumnInfo col)
            {
                return col.isCalculated() && col.isUnselectable() && col.getFk() != null;
            }
        });

        for (ColumnInfo c : columns)
            table.addColumn( (MutableColumnInfo) c );
    }

    public void setDefaultVisible(AbstractTableInfo table)
    {
        //this will reset default visible and force recalculation next time they are requested
        table.setDefaultVisibleColumns(null);
    }
}
