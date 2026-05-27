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
import org.labkey.api.data.JdbcType;
import org.labkey.api.data.MutableColumnInfo;
import org.labkey.api.data.SQLFragment;
import org.labkey.api.data.TableCustomizer;
import org.labkey.api.data.TableInfo;
import org.labkey.api.ldk.LDKService;
import org.labkey.api.query.ExprColumn;

import java.util.ArrayList;
import java.util.List;

public class SamplesCustomizer implements TableCustomizer
{
    public SamplesCustomizer()
    {

    }

    @Override
    public void customize(TableInfo ti)
    {
        //apply defaults
        TableCustomizer tc = LDKService.get().getDefaultTableCustomizer();
        tc.customize(ti);

        if (ti instanceof AbstractTableInfo)
        {
            //appendAmountColumn((AbstractTableInfo)ti);
        }

        //this customizer also sorts columns, so we append amount first
        TableCustomizer tc2 = new LaboratoryTableCustomizer();
        tc2.customize(ti);
    }

    private void appendAmountColumn(AbstractTableInfo ti)
    {
        if (ti.getColumn("amount") == null)
        {
            ColumnInfo conc = ti.getColumn("concentration");
            ColumnInfo quantity = ti.getColumn("quantity");
            SQLFragment sql = new SQLFragment("(" + ExprColumn.STR_TABLE_ALIAS +".concentration * " + ExprColumn.STR_TABLE_ALIAS + ".quantity)");
            ExprColumn col = new ExprColumn(ti, "amount", sql, JdbcType.DOUBLE, conc, quantity);
            col.setLabel("Amount");
            col.setDescription("This field takes the concentration multiplied by the quantity fields.  It is automatically calculated and does not take units or other information into account.");

            //inject amount column after quantity.
            List<ColumnInfo> columns = new ArrayList<>();
            columns.addAll(ti.getColumns());
            for (ColumnInfo c : columns)
                ti.removeColumn(c);

            int idx = columns.indexOf(quantity);
            columns.add(idx + 1, col);
            for (ColumnInfo c : columns)
                ti.addColumn( (MutableColumnInfo)c );
        }
    }
}