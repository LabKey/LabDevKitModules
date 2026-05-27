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
package org.labkey.laboratory.security;

import org.labkey.api.laboratory.security.LaboratoryAdminPermission;
import org.labkey.api.ldk.security.DataAdminPermission;
import org.labkey.api.security.permissions.DeletePermission;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.security.permissions.UpdatePermission;
import org.labkey.api.security.roles.AbstractModuleScopedRole;
import org.labkey.laboratory.LaboratoryModule;

/**

 */
public class LaboratoryAdminRole extends AbstractModuleScopedRole
{
    public LaboratoryAdminRole()
    {
        super("Laboratory Admin", "Grants users the ability to manage folder-level settings in DISCVR and the laboratory module.",
                LaboratoryModule.class,
                ReadPermission.class,
                InsertPermission.class,
                UpdatePermission.class,
                DeletePermission.class,
                DataAdminPermission.class,
                LaboratoryAdminPermission.class
        );
    }
}
