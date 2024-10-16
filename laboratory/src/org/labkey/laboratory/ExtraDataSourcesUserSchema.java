package org.labkey.laboratory;

import org.labkey.api.collections.CaseInsensitiveTreeSet;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerFilter;
import org.labkey.api.data.DbSchema;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.Module;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.SimpleUserSchema;
import org.labkey.api.security.User;
import org.labkey.laboratory.table.WrappingTableCustomizer;

import java.util.Collections;
import java.util.Set;

/**
 * User: bimber
 * Date: 1/20/13
 * Time: 7:57 AM
 */
public class ExtraDataSourcesUserSchema extends SimpleUserSchema
{
    public static final String NAME = "labdatasources";
    private DbSchema _labDbSchema;

    private ExtraDataSourcesUserSchema(User user, Container container)
    {
        super(NAME, null, user, container, null);
    }

    public static void register(final Module m)
    {
        DefaultSchema.registerProvider(NAME, new DefaultSchema.SchemaProvider(m)
        {
            @Override
            public QuerySchema createSchema(final DefaultSchema schema, Module module)
            {
                return new ExtraDataSourcesUserSchema(schema.getUser(), schema.getContainer());
            }
        });
    }

    @Override
    public TableInfo createTable(String name, ContainerFilter cf)
    {
        LaboratoryServiceImpl service = LaboratoryServiceImpl.get();
        Set<AdditionalDataSource> sources = service.getAdditionalDataSources(getContainer(), getUser());
        for (AdditionalDataSource source : sources)
        {
            if (name.equalsIgnoreCase(source.getQueryName()))
            {
                if (!getContainer().isWorkbook() || source.isImportIntoWorkbooks())
                {
                    TableInfo ti = createWrappedTable(name, source.getTableInfo(getContainer(), getUser()), cf);
                    new WrappingTableCustomizer().customize(ti);

                    return ti;
                }
            }
        }

        return null;
    }

    @Override
    protected void afterConstruct(TableInfo info)
    {
        // No-op to avoid double-adding query metadata. Rely on the source table for this.
        // This bug appears in the schema browser in production mode only. If there is a container with any extra data sources present, the table details
        // page will asynchronously load an 'Error: null' message, coming from the query analyzer
    }

    @Override
    public DbSchema getDbSchema()
    {
        if (_labDbSchema == null)
        {
            _labDbSchema = LaboratorySchema.getInstance().getSchema();
        }

        return _labDbSchema;
    }

    @Override
    public Set<String> getVisibleTableNames()
    {
        return Collections.unmodifiableSet(getTableNames());
    }

    @Override
    public Set<String> getTableNames()
    {
        Set<String> tables = new CaseInsensitiveTreeSet();
        LaboratoryServiceImpl service = LaboratoryServiceImpl.get();
        Set<AdditionalDataSource> sources = service.getAdditionalDataSources(getContainer(), getUser());
        for (AdditionalDataSource source : sources)
        {
            if (!getContainer().isWorkbook() || source.isImportIntoWorkbooks())
                tables.add(source.getQueryName());
        }

        return tables;
    }
}
