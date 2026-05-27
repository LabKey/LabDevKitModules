/*
 * Copyright (c) 2023-2026 LabKey Corporation
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
package org.labkey.api.laboratory.assay;

import org.labkey.api.assay.AssayProvider;
import org.labkey.api.data.Container;
import org.labkey.api.exp.api.ExpProtocol;
import org.labkey.api.laboratory.DataProvider;
import org.labkey.api.security.User;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used by LaboratoryService to register assays.  It wraps the core assay description to provide additional
 * details used specifically in the laboratory module
 */
public interface AssayDataProvider extends DataProvider
{
    String getProviderName();

    AssayProvider getAssayProvider();

    /**
     * Returns the set of AssayImportMethods supported by this assay.  If none are provided, a default import method
     * will be used
     */
    Collection<AssayImportMethod> getImportMethods();

    /**
     * Return true if this import pathway can be used with assay run templates, which allows runs to be prepared ahead of importing results
     */
    boolean supportsRunTemplates();

    List<ExpProtocol> getProtocols(Container c);

    AssayImportMethod getImportMethodByName(String methodName);

    String getDefaultImportMethodName(Container c, User u, long protocolId);

    boolean isModuleEnabled(Container c);
}
