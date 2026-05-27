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

import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.query.ValidationException;
import org.labkey.api.security.User;

public class WorkbookModel
{
    private Integer _workbookId;
    private String _containerId;
    private String _materials;
    private String _methods;
    private String _results;
    private String _exptGroup;
    private String[] _tags;

    public WorkbookModel()
    {

    }

    public Integer getWorkbookId()
    {
        return _workbookId;
    }

    public void setWorkbookId(Integer workbookId)
    {
        _workbookId = workbookId;
    }

    public String getContainer()
    {
        return _containerId;
    }

    public void setContainer(String container)
    {
        _containerId = container;
    }

    public String getMaterials()
    {
        return _materials;
    }

    public void setMaterials(String materials)
    {
        _materials = materials;
    }

    public String getMethods()
    {
        return _methods;
    }

    public void setMethods(String methods)
    {
        _methods = methods;
    }

    public String getResults()
    {
        return _results;
    }

    public void setResults(String results)
    {
        _results = results;
    }

    public String getExptGroup()
    {
        return _exptGroup;
    }

    public void setExptGroup(String exptGroup)
    {
        _exptGroup = exptGroup;
    }

    private Container _getContainer()
    {
        if (_containerId == null)
            throw new IllegalArgumentException("The containerId has not been set");

        Container c = ContainerManager.getForId(_containerId);
        if (c == null)
            throw new IllegalArgumentException("Unknown container: " + _containerId);

        return c;
    }

    public void setDescription(String description, User u)
    {
        try
        {
            ContainerManager.updateDescription(_getContainer(), description, u);
        }
        catch (ValidationException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getDescription()
    {
        return _getContainer().getDescription();
    }

    public String[] getTags()
    {
        return _tags;
    }

    public void setTags(String[] tags)
    {
        _tags = tags;
    }

    public static WorkbookModel createNew(Container c)
    {
        if (!c.isWorkbook())
        {
            throw new IllegalArgumentException("Container is not a workbook: " + c.getPath());
        }

        WorkbookModel model = new WorkbookModel();
        model.setWorkbookId(Integer.parseInt(c.getName()));
        model.setContainer(c.getId());

        return model;
    }
}
