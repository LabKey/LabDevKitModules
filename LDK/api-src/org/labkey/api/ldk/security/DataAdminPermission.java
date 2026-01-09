package org.labkey.api.ldk.security;

import org.labkey.api.security.permissions.AbstractPermission;

public class DataAdminPermission extends AbstractPermission
{
    public DataAdminPermission() {
        super("DataAdminPermission", "Required for certain operations involving large-scale management of data");
    }
}