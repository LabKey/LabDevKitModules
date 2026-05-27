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
package org.labkey.laboratory.query;

import org.labkey.api.data.AbstractTableInfo;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.DisplayColumn;
import org.labkey.api.data.DisplayColumnFactory;
import org.labkey.api.data.JdbcType;
import org.labkey.api.data.MutableColumnInfo;
import org.labkey.api.data.SQLFragment;
import org.labkey.api.data.TableCustomizer;
import org.labkey.api.data.TableInfo;
import org.labkey.api.ldk.LDKService;
import org.labkey.api.query.DetailsURL;
import org.labkey.api.query.ExprColumn;
import org.labkey.api.query.FieldKey;

import java.util.ArrayList;
import java.util.List;

public class AssayRunTemplatesCustomizer implements TableCustomizer
{
    public AssayRunTemplatesCustomizer()
    {

    }

    @Override
    public void customize(TableInfo ti)
    {
        //apply defaults
        TableCustomizer tc = LDKService.get().getDefaultTableCustomizer();
        tc.customize(ti);

        if (ti instanceof AbstractTableInfo ati)
        {
            ati.setInsertURL(AbstractTableInfo.LINK_DISABLER);
            ati.setImportURL(AbstractTableInfo.LINK_DISABLER);
            ati.setUpdateURL(AbstractTableInfo.LINK_DISABLER);
            ati.setUpdateURL(DetailsURL.fromString("/laboratory/prepareExptRun.view?assayId=${assayId}&templateId=${rowid}"));
            ati.setDetailsURL(null);

            List<ColumnInfo> newCols = new ArrayList<>();

            ExprColumn completeCol = new ExprColumn(ti, "enter_results", new SQLFragment("'Enter Results'"), JdbcType.VARCHAR, ti.getColumn("assayId"), ti.getColumn("runid"));
            completeCol.setName("enter_results");
            completeCol.setLabel("Enter Results");
            completeCol.setUserEditable(false);
            completeCol.setShownInInsertView(false);
            completeCol.setShownInUpdateView(false);
            completeCol.setDisplayColumnFactory(new DisplayColumnFactory()
            {
                @Override
                public DisplayColumn createRenderer(ColumnInfo colInfo)
                {
                    return new EnterResultsDisplayColumn(colInfo);
                }
            });

            newCols.add(completeCol);
            newCols.addAll(ati.getColumns());

            //reset default visible columns on table
            List<FieldKey> defaultColumns = new ArrayList<>();
            defaultColumns.add(completeCol.getFieldKey());
            defaultColumns.addAll(ati.getDefaultVisibleColumns());
            ati.setDefaultVisibleColumns(defaultColumns);

            for (ColumnInfo ci : ati.getColumns())
            {
                ati.removeColumn(ci);
            }

            for (ColumnInfo ci : newCols)
            {
                ati.addColumn( (MutableColumnInfo) ci);
            }
        }

    }
}
