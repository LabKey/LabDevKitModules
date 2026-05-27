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

import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.DataColumn;
import org.labkey.api.data.RenderContext;
import org.labkey.api.writer.HtmlWriter;

public class WorkbookIdDisplayColumn extends DataColumn
{
    public WorkbookIdDisplayColumn(ColumnInfo col)
    {
        super(col);
    }

    @Override
    public void renderGridCellContents(RenderContext ctx, HtmlWriter out)
    {
        //if the lookup is broken, don't render a value
        Object dv = getDisplayValue(ctx);
        if (dv == null || "".equals(dv))
            return;

        super.renderGridCellContents(ctx, out);
    }

    @Override
    public Object getDisplayValue(RenderContext ctx)
    {
        Object ret = super.getDisplayValue(ctx);

        //if the lookup is broken, don't render a value.  note: return empty string so client API draws the distinction between deliberately empty and a genuine NULL value
        return ret == null ? "" : ret;
    }
}