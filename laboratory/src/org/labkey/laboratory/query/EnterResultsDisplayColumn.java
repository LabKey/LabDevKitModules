package org.labkey.laboratory.query;

import org.labkey.api.assay.AssayService;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DataColumn;
import org.labkey.api.data.RenderContext;
import org.labkey.api.exp.api.ExpProtocol;
import org.labkey.api.exp.api.ExperimentService;
import org.labkey.api.util.LinkBuilder;
import org.labkey.api.view.ActionURL;
import org.labkey.api.writer.HtmlWriter;

import static org.labkey.api.util.DOM.TD;
import static org.labkey.api.util.DOM.cl;

/**
 * User: bimber
 * Date: 11/6/12
 * Time: 10:26 AM
 */
public class EnterResultsDisplayColumn extends DataColumn
{
    public EnterResultsDisplayColumn(ColumnInfo col)
    {
        super(col);
        addDisplayClass("labkey-details");
    }

    @Override
    public String renderURL(RenderContext ctx)
    {
        Integer assayId = (Integer)ctx.get("assayId");
        Integer runid = (Integer)ctx.get("runid");
        Container c = ContainerManager.getForId((String)ctx.get("container"));

        ExpProtocol protocol = ExperimentService.get().getExpProtocol(assayId);
        if (runid == null && protocol != null)
        {
            ActionURL url = AssayService.get().getProvider(protocol).getImportURL(c, protocol);
            url.addParameter("templateId", ctx.get("rowid").toString());
            return url.toString();
        }
        return null;
    }

    @Override
    public void renderGridCellContents(RenderContext ctx, HtmlWriter out)
    {
        Object value = getValue(ctx);
        String url = renderURL(ctx);

        if (value != null && url != null)
        {
            out.write(LinkBuilder.labkeyLink(value.toString(), url).target(_linkTarget));
        }
    }

    @Override
    public void renderGridHeaderCell(RenderContext ctx, HtmlWriter out, String headerClass)
    {
        TD(cl("labkey-column-header")).appendTo(out);
    }
}
