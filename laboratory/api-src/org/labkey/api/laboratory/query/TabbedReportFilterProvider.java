package org.labkey.api.laboratory.query;

import org.labkey.api.data.Container;
import org.labkey.api.security.User;
import org.labkey.api.view.template.ClientDependency;

import java.util.Collection;

public interface TabbedReportFilterProvider
{
    boolean isAvailable(Container c, User u);

    Collection<ClientDependency> getClientDependencies();

    String getXType();

    String getLabel();

    String getInputValue();
}
