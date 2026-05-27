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
package org.labkey.api.laboratory.query;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerType;
import org.labkey.api.data.PropertyManager;
import org.labkey.api.data.TableInfo;
import org.labkey.api.laboratory.NavItem;
import org.labkey.api.laboratory.TabbedReportItem;
import org.labkey.api.module.Module;
import org.labkey.api.query.FieldKey;
import org.labkey.api.security.User;
import org.labkey.api.view.template.ClientDependency;

import java.util.Collection;
import java.util.Map;

public interface TabbedReportFilterProvider
{
    default boolean isAvailable(Container c, User u)
    {
        return c.getActiveModules().contains(getOwningModule());
    }

    Module getOwningModule();

    Collection<ClientDependency> getClientDependencies();

    String getXType();

    String getLabel();

    String getInputValue();

    default JSONObject toJSON(Container c, User u)
    {
        JSONObject ret = new JSONObject();
        ret.put("xtype", getXType());
        ret.put("label", getLabel());
        ret.put("inputValue", getInputValue());
        ret.put("isAvailable", isAvailable(c, u));
        ret.put("isVisible", isVisible(c, u));
        ret.put("key", getPropertyManagerKey());

        return ret;
    }

    default boolean isVisible(Container c, User u)
    {
        Container targetContainer = c.getContainerFor(ContainerType.DataType.navVisibility);
        if (getOwningModule() != null)
        {
            if (!targetContainer.getActiveModules().contains(getOwningModule()))
                return false;
        }

        Map<String, String> map = new CaseInsensitiveHashMap<>(PropertyManager.getProperties(targetContainer, NavItem.PROPERTY_CATEGORY));
        if (map.containsKey(getPropertyManagerKey()))
            return Boolean.parseBoolean(map.get(getPropertyManagerKey()));

        return true;
    }

    default String getPropertyManagerKey()
    {
        return "tabReportFilterProvider||" + getClass().getSimpleName() + "||" + getLabel();
    }

    @NotNull
    Map<String, FieldKey> getAdditionalFieldKeys(TableInfo ti, TabbedReportItem tri, Map<String, FieldKey> overrides);
}
