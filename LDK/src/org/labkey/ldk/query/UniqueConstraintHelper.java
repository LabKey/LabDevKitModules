/*
 * Copyright (c) 2014-2026 LabKey Corporation
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.UserSchema;
import org.labkey.api.security.User;
import org.labkey.api.security.UserManager;
import org.labkey.api.util.MemTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**

 */
public class UniqueConstraintHelper
{
    private Container _container;
    private final User _user;
    private final TableInfo _table;
    private final String _targetColName;

    private final List<Object> _encounteredKeys = new ArrayList<>();

    private static final Logger _log = LogManager.getLogger(UniqueConstraintHelper.class);

    private UniqueConstraintHelper(String containerId, int userId, String schemaName, String queryName, String targetColName)
    {
        _container = ContainerManager.getForId(containerId);
        if (_container == null)
            throw new IllegalArgumentException("Unknown container: " + containerId);

        _container = _container.isWorkbook() ? _container.getParent() : _container;

        _user = UserManager.getUser(userId);
        if (_user == null)
            throw new IllegalArgumentException("Unknown user: " + userId);

        UserSchema us = QueryService.get().getUserSchema(_user, _container, schemaName);
        if (us == null)
            throw new IllegalArgumentException("Unknown schema: " + schemaName);

        _table = us.getTable(queryName);
        if (_table == null)
            throw new IllegalArgumentException("Unknown table: " + schemaName + "." + queryName);

        _targetColName = targetColName;

        MemTracker.getInstance().put(this);
    }

    public static UniqueConstraintHelper create(String containerId, int userId, String schemaName, String queryName, String targetColName)
    {
        return new UniqueConstraintHelper(containerId, userId, schemaName, queryName, targetColName);
    }

    public boolean validateKey(Object value, @Nullable Object oldValue)
    {
        //allow for updates that change the value
        if (oldValue != null)
        {
            if (!oldValue.equals(value))
            {
                _encounteredKeys.remove(oldValue);
            }
            else
            {
                //if this row already existed with this value, allow it
                _encounteredKeys.add(value);
                return true;
            }
        }

        if (_encounteredKeys.contains(value))
        {
            return false;
        }

        //check the DB
        boolean exists = new TableSelector(_table, Collections.singleton(_targetColName), new SimpleFilter(FieldKey.fromString(_targetColName), value), null).exists();
        _encounteredKeys.add(value);

        return !exists;
    }
}
