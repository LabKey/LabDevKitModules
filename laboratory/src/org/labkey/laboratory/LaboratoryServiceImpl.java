/*
 * Copyright (c) 2012 LabKey Corporation
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
package org.labkey.laboratory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.labkey.api.assay.AssayFileWriter;
import org.labkey.api.assay.AssayProvider;
import org.labkey.api.assay.AssayService;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.PropertyManager;
import org.labkey.api.data.PropertyManager.WritablePropertyMap;
import org.labkey.api.data.TableCustomizer;
import org.labkey.api.data.TableInfo;
import org.labkey.api.exp.ChangePropertyDescriptorException;
import org.labkey.api.exp.ExperimentException;
import org.labkey.api.exp.api.ExpExperiment;
import org.labkey.api.exp.api.ExpProtocol;
import org.labkey.api.exp.api.ExpRun;
import org.labkey.api.exp.api.ExperimentService;
import org.labkey.api.laboratory.DataProvider;
import org.labkey.api.laboratory.DemographicsProvider;
import org.labkey.api.laboratory.LaboratoryService;
import org.labkey.api.laboratory.NavItem;
import org.labkey.api.laboratory.TabbedReportItem;
import org.labkey.api.laboratory.assay.AssayDataProvider;
import org.labkey.api.laboratory.assay.SimpleAssayDataProvider;
import org.labkey.api.ldk.table.ButtonConfigFactory;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.module.ModuleProperty;
import org.labkey.api.pipeline.PipelineService;
import org.labkey.api.query.ValidationException;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.Pair;
import org.labkey.api.view.ViewContext;
import org.labkey.laboratory.assay.AssayHelper;
import org.labkey.laboratory.query.DefaultAssayCustomizer;
import org.labkey.laboratory.query.LaboratoryTableCustomizer;
import org.labkey.vfs.FileLike;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: bimber
 * Date: 9/15/12
 * Time: 6:27 AM
 */
public class LaboratoryServiceImpl extends LaboratoryService
{
    private static final LaboratoryServiceImpl _instance = new LaboratoryServiceImpl();
    private static final Logger _log = LogManager.getLogger(LaboratoryServiceImpl.class);

    private final Set<Module> _registeredModules = new HashSet<>();
    private final Map<String, Map<String, List<ButtonConfigFactory>>> _assayButtons = new CaseInsensitiveHashMap<>();
    private final Map<String, DataProvider> _dataProviders = new HashMap<>();
    private final Map<String, Map<String, List<Pair<Module, Class<? extends TableCustomizer>>>>> _tableCustomizers = new CaseInsensitiveHashMap<>();
    private final List<DemographicsProvider> _demographicsProviders = new ArrayList<>();

    public static final String DEMOGRAPHICS_PROPERTY_CATEGORY = "laboratory.demographicsSource";
    public static final String DATASOURCE_PROPERTY_CATEGORY = "laboratory.additionalDataSource";
    public static final String URL_DATASOURCE_PROPERTY_CATEGORY = "laboratory.urlDataSource";

    private LaboratoryServiceImpl()
    {

    }

    public static LaboratoryServiceImpl get()
    {
        return _instance;
    }

    @Override
    public void registerModule(Module module)
    {
        _registeredModules.add(module);
    }

    @Override
    public Set<Module> getRegisteredModules()
    {
        return _registeredModules;
    }

    @Override
    public void registerDataProvider(DataProvider dp)
    {
        if (_dataProviders.containsKey(dp.getKey())){
            _log.error("A DataProvider with the name: " + dp.getName() + " has already been registered");
        }

        _dataProviders.put(dp.getKey(), dp);
    }

    @Override
    public synchronized Set<DataProvider> getDataProviders()
    {
        Set<DataProvider> providers = new HashSet<>(_dataProviders.values());

        Set<AssayProvider> registeredProviders = new HashSet<>();
        for (DataProvider dp : _dataProviders.values())
        {
            if (dp instanceof AssayDataProvider)
            registeredProviders.add(((AssayDataProvider) dp).getAssayProvider());
        }

        // also append any assays not explicitly registered with LaboratoryService
        // this first time we encounter this assayProvider, register it
        for (AssayProvider ap : AssayService.get().getAssayProviders())
        {
            if (!registeredProviders.contains(ap))
            {
                DataProvider provider = new SimpleAssayDataProvider(ap.getName());
                if (!_dataProviders.containsKey(provider.getKey()))
                {
                    registerDataProvider(provider);
                    providers.add(provider);
                }
                else
                    providers.add(_dataProviders.get(provider.getKey()));
            }
        }

        return providers;
    }

    @Override
    public Set<AssayDataProvider> getRegisteredAssayProviders()
    {
        Set<AssayDataProvider> providers = new HashSet<>();
        for (DataProvider dp : _dataProviders.values())
        {
            if (dp instanceof AssayDataProvider)
            {
                providers.add((AssayDataProvider)dp);
            }
        }
        return providers;
    }

    @Override
    public AssayDataProvider getDataProviderForAssay(int protocolId)
    {
        ExpProtocol protocol = ExperimentService.get().getExpProtocol(protocolId);
        if (protocol == null)
            return null;

        AssayProvider ap = AssayService.get().getProvider(protocol);
        return getDataProviderForAssay(ap);
    }

    @Override
    public AssayDataProvider getDataProviderForAssay(AssayProvider ap)
    {
        for (AssayDataProvider dp : getRegisteredAssayProviders())
        {
            if (dp.getAssayProvider().equals(ap))
                return dp;
        }
        return new SimpleAssayDataProvider(ap.getName());
    }

    @Override
    public Pair<ExpExperiment, ExpRun> saveAssayBatch(List<Map<String, Object>> results, JSONObject json, String basename, ViewContext ctx, AssayProvider provider, ExpProtocol protocol) throws ValidationException
    {
        if (!PipelineService.get().hasValidPipelineRoot(protocol.getContainer()))
            throw new ValidationException("Pipeline root must be configured before uploading assay files");

        try
        {
            FileLike targetDirectory = AssayFileWriter.ensureUploadDirectory(ctx.getContainer());
            FileLike file = AssayFileWriter.findUniqueFileName(basename, targetDirectory);

            return this.saveAssayBatch(results, json, file.toNioPathForRead().toFile(), ctx, provider, protocol);
        }
        catch (ExperimentException e)
        {
            _log.error(e);
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Pair<ExpExperiment, ExpRun> saveAssayBatch(List<Map<String, Object>> results, JSONObject json, File file, ViewContext ctx, AssayProvider provider, ExpProtocol protocol) throws ValidationException
    {
        try
        {
            return AssayHelper.get().saveAssayBatch(results, json, file, ctx, provider, protocol);
        }
        catch (ExperimentException e)
        {
            _log.error(e);
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public List<NavItem> getSettingsItems(Container c, User u)
    {
        List<NavItem> items = new ArrayList<>();
        for (DataProvider dp : getDataProviders())
        {
            items.addAll(dp.getSettingsItems(c, u));
        }
        sortNavItems(items);
        return Collections.unmodifiableList(items);
    }

    @Override
    public List<NavItem> getSampleItems(Container c, User u)
    {
        List<NavItem> navItems = new ArrayList<>();
        for (DataProvider dp : getDataProviders()){
            navItems.addAll(dp.getSampleNavItems(c, u));
        }
        sortNavItems(navItems);
        return Collections.unmodifiableList(navItems);
    }

    @Override
    public List<NavItem> getMiscItems(Container c, User u)
    {
        List<NavItem> navItems = new ArrayList<>();
        for (DataProvider dp : getDataProviders()){
            navItems.addAll(dp.getMiscItems(c, u));
        }
        sortNavItems(navItems);
        return Collections.unmodifiableList(navItems);
    }

    @Override
    public List<NavItem> getReportItems(Container c, User u)
    {
        List<NavItem> navItems = new ArrayList<>();
        for (DataProvider dp : getDataProviders()){
            navItems.addAll(dp.getReportItems(c, u));
        }
        sortNavItems(navItems);

        return Collections.unmodifiableList(navItems);
    }

    @Override
    public List<NavItem> getDataItems(Container c, User u)
    {
        List<NavItem> navItems = new ArrayList<>();
        for (DataProvider dp : LaboratoryService.get().getDataProviders())
        {
            navItems.addAll(dp.getDataNavItems(c, u));
        }

        sortNavItems(navItems);
        return Collections.unmodifiableList(navItems);
    }

    @Override
    public DataProvider getDataProvider(String name)
    {
        return _dataProviders.get(name);
    }

    @Override
    public void ensureAssayColumns(User u, String providerName) throws ChangePropertyDescriptorException
    {
        AssayHelper.ensureAssayFields(u, providerName);
    }

    @Override
    public void sortNavItems(List<? extends NavItem> navItems)
    {
        navItems.sort((Comparator<NavItem>) (o1, o2) ->
        {
            //Issue 18751: NullPointerException from org.labkey.laboratory.LaboratoryServiceImpl
            if (o1.getLabel() == null)
            {
                _log.error("NavItem has a null label: " + o1.getPropertyManagerKey());
                return -1;
            }

            if (o2.getLabel() == null)
            {
                _log.error("NavItem has a null label: " + o2.getPropertyManagerKey());
                return 1;
            }

            return o1.getLabel() == null ? -1 : o1.getLabel().compareToIgnoreCase(o2.getLabel());
        });
    }

    @Override
    public String getDefaultWorkbookFolderType(Container c)
    {
        Module labModule = ModuleLoader.getInstance().getModule(LaboratoryModule.class);
        ModuleProperty mp = labModule.getModuleProperties().get(LaboratoryManager.DEFAULT_WORKBOOK_FOLDERTYPE_PROPNAME);
        return mp.getEffectiveValue(c);
    }

    @Override
    public void registerAssayButton(ButtonConfigFactory btn, String providerName, String domain)
    {
        Map<String, List<ButtonConfigFactory>> schemaMap = _assayButtons.get(providerName);
        if (schemaMap == null)
            schemaMap = new CaseInsensitiveHashMap<>();

        List<ButtonConfigFactory> list = schemaMap.get(domain);
        if (list == null)
            list = new ArrayList<>();

        list.add(btn);

        schemaMap.put(domain, list);
        _assayButtons.put(providerName, schemaMap);
    }

    @Override
    public List<ButtonConfigFactory> getAssayButtons(TableInfo ti, String providerName, String domain)
    {
        List<ButtonConfigFactory> buttons = new ArrayList<>();

        Map<String, List<ButtonConfigFactory>> factories = _assayButtons.get(providerName);
        if (factories == null)
            return buttons;

        List<ButtonConfigFactory> list = factories.get(domain);
        if (list == null)
            return  buttons;

        for (ButtonConfigFactory fact : list)
        {
            if (fact.isAvailable(ti))
                buttons.add(fact);
        }

        return Collections.unmodifiableList(buttons);
    }

    @Override
    public TableCustomizer getLaboratoryTableCustomizer()
    {
        return new LaboratoryTableCustomizer();
    }

    @Override
    public TableCustomizer getAssayTableCustomizer()
    {
        return new DefaultAssayCustomizer();
    }

    public Set<DemographicsSource> getDemographicsSources(Container c, User u) throws IllegalArgumentException
    {
        Set<DemographicsSource> qds = new HashSet<>();

        Container target = c.isWorkbookOrTab() ? c.getParent() : c;
        Map<String, String> properties = PropertyManager.getProperties(target, DEMOGRAPHICS_PROPERTY_CATEGORY);
        for (String key : properties.keySet())
        {
            try
            {
                DemographicsSource source = DemographicsSource.getFromPropertyManager(target, u, key, properties.get(key));
                if (source != null)
                {
                    qds.add(source);
                }
            }
            catch (IllegalArgumentException e)
            {
                _log.error("Invalid stored demographics source from container: " + c.getPath(), e);
            }
        }

        return qds;
    }

    public void setDemographicsSources(Container c, User u, Set<DemographicsSource> sources) throws IllegalArgumentException
    {
        Container target = c.isWorkbookOrTab() ? c.getParent() : c;
        WritablePropertyMap props = PropertyManager.getWritableProperties(target, DEMOGRAPHICS_PROPERTY_CATEGORY, true);
        props.clear();

        Set<String> labels = new HashSet<>();
        for (DemographicsSource qd : sources)
        {
            String name = ColumnInfo.legalNameFromName(qd.getLabel());
            if (labels.contains(name))
                throw new IllegalArgumentException("All demographics sources must have unique names.  Duplicate was: " + name);


            labels.add(name);

            props.put(qd.getPropertyManagerKey(), qd.getPropertyManagerValue());
        }
        props.save();
    }

    //enforce read permission silently.  expect the action to limit this to admins
    public Map<Container, Set<AdditionalDataSource>> getAllAdditionalDataSources(User u) throws IllegalArgumentException
    {
        Map<Container, Set<AdditionalDataSource>> map = new HashMap<>();
        PropertyManager.PropertyEntry[] entries = PropertyManager.findPropertyEntries(null, null, DATASOURCE_PROPERTY_CATEGORY, null);
        for (PropertyManager.PropertyEntry entry : entries)
        {
            Container c = ContainerManager.getForId(entry.getObjectId());
            if (c == null || !c.hasPermission(u, ReadPermission.class))
                continue;

            Set<AdditionalDataSource> set = map.get(c);
            if (set == null)
                set = new HashSet<>();

            AdditionalDataSource source = AdditionalDataSource.getFromPropertyManager(c, u, entry.getKey(), entry.getValue());
            if (source == null)
            {
                continue;
            }

            Container targetContainer = source.getTargetContainer(c);
            if (targetContainer == null || !targetContainer.hasPermission(u, ReadPermission.class))
            {
                continue;
            }

            set.add(source);
            map.put(c, set);
        }

        return Collections.unmodifiableMap(map);
    }

    //enforce read permission silently.  expect the action to limit this to admins
    public Map<Container, Set<DemographicsSource>> getAllDemographicsSources(User u) throws IllegalArgumentException
    {
        Map<Container, Set<DemographicsSource>> map = new HashMap<>();
        PropertyManager.PropertyEntry[] entries = PropertyManager.findPropertyEntries(null, null, DEMOGRAPHICS_PROPERTY_CATEGORY, null);
        for (PropertyManager.PropertyEntry entry : entries)
        {
            Container c = ContainerManager.getForId(entry.getObjectId());
            if (c == null || !c.hasPermission(u, ReadPermission.class))
            {
                continue;
            }

            Set<DemographicsSource> set = map.get(c);
            if (set == null)
            {
                set = new HashSet<>();
            }

            DemographicsSource source = DemographicsSource.getFromPropertyManager(c, u, entry.getKey(), entry.getValue());
            if (source == null)
            {
                continue;
            }

            Container targetContainer = source.getTargetContainer(c);
            if (targetContainer == null || !targetContainer.hasPermission(u, ReadPermission.class))
            {
                continue;
            }

            set.add(source);
            map.put(c, set);
        }

        return Collections.unmodifiableMap(map);
    }

    public Set<URLDataSource> getURLDataSources(Container c, User u) throws IllegalArgumentException
    {
        Set<URLDataSource> qds = new HashSet<>();

        Container target = c.isWorkbookOrTab() ? c.getParent() : c;
        Map<String, String> properties = PropertyManager.getProperties(target, URL_DATASOURCE_PROPERTY_CATEGORY);
        for (String key : properties.keySet())
        {
            try
            {
                URLDataSource source = URLDataSource.getFromPropertyManager(c, key, properties.get(key));
                if (source != null)
                    qds.add(source);
            }
            catch (IllegalArgumentException e)
            {
                _log.error("Invalid stored URL data source from container: " + c.getPath(), e);
            }
        }

        return qds;
    }

    public Set<AdditionalDataSource> getAdditionalDataSources(Container c, User u) throws IllegalArgumentException
    {
        Set<AdditionalDataSource> qds = new HashSet<>();

        Container target = c.isWorkbookOrTab() ? c.getParent() : c;
        Map<String, String> properties = PropertyManager.getProperties(target, DATASOURCE_PROPERTY_CATEGORY);
        for (String key : properties.keySet())
        {
            try
            {
                AdditionalDataSource source = AdditionalDataSource.getFromPropertyManager(target, u, key, properties.get(key));
                if (source != null)
                    qds.add(source);
            }
            catch (IllegalArgumentException e)
            {
                _log.error("Invalid stored data source from container: " + c.getPath(), e);
            }
        }

        return qds;
    }

    public void setURLDataSources(Container c, User u, Set<URLDataSource> sources)
    {
        Container cc = c.isWorkbookOrTab() ? c.getParent() : c;
        WritablePropertyMap props = PropertyManager.getWritableProperties(cc, URL_DATASOURCE_PROPERTY_CATEGORY, true);
        props.clear();

        for (URLDataSource qd : sources)
        {
            props.put(qd.getPropertyManagerKey(), qd.getPropertyManagerValue());
        }
        props.save();
    }

    public void setAdditionalDataSources(Container c, User u, Set<AdditionalDataSource> sources)
    {
        Container cc = c.isWorkbookOrTab() ? c.getParent() : c;
        WritablePropertyMap props = PropertyManager.getWritableProperties(cc, DATASOURCE_PROPERTY_CATEGORY, true);
        props.clear();

        for (AdditionalDataSource qd : sources)
        {
            props.put(qd.getPropertyManagerKey(), qd.getPropertyManagerValue());
        }
        props.save();
    }

    @Override
    public List<TabbedReportItem> getTabbedReportItems(Container c, User u)
    {
        List<TabbedReportItem> items = new ArrayList<>();
        for (DataProvider dp : getDataProviders())
        {
            items.addAll(dp.getTabbedReportItems(c, u));
        }
        sortNavItems(items);
        return items;
    }

    private final Map<String, List<List<String>>> _assayResultIndexes = new HashMap<>();
    private final Map<String, Map<String, List<List<String>>>> _tableIndexes = new HashMap<>();

    @Override
    public void registerAssayResultsIndex(String providerName, List<String> columnsToIndex)
    {
        List<List<String>> indexes = _assayResultIndexes.get(providerName);
        if (indexes == null)
            indexes = new ArrayList<>();

        indexes.add(columnsToIndex);

        _assayResultIndexes.put(providerName, indexes);
    }

    @Override
    public void registerTableIndex(String schemaName, String queryName, List<String> columnsToIndex)
    {
        Map<String, List<List<String>>> indexesForSchema = _tableIndexes.get(schemaName);
        if (indexesForSchema == null)
            indexesForSchema = new HashMap<>();

        List<List<String>> indexes = indexesForSchema.get(queryName);
        if (indexes == null)
            indexes = new ArrayList<>();

        indexes.add(columnsToIndex);

        indexesForSchema.put(queryName, indexes);
        _tableIndexes.put(schemaName, indexesForSchema);
    }

    public Map<String, List<List<String>>> getAssayIndexes()
    {
        return Collections.unmodifiableMap(_assayResultIndexes);
    }

    public Map<String, Map<String, List<List<String>>>> getTableIndexes()
    {
        return Collections.unmodifiableMap(_tableIndexes);
    }

    public static String ALL = "*";

    @Override
    public void registerTableCustomizer(Module owner, Class<? extends TableCustomizer> customizerClass, String schemaName, String queryName)
    {
        Map<String, List<Pair<Module, Class<? extends TableCustomizer>>>> schemaMap = _tableCustomizers.get(schemaName);
        if (schemaMap == null)
            schemaMap = new CaseInsensitiveHashMap<>();

        List<Pair<Module, Class<? extends TableCustomizer>>> list = schemaMap.get(queryName);
        if (list == null)
            list = new ArrayList<>();

        list.add(Pair.of(owner, customizerClass));

        schemaMap.put(queryName, list);
        _tableCustomizers.put(schemaName, schemaMap);
    }

    public List<TableCustomizer> getCustomizers(Container c, String schemaName, String queryName)
    {
        List<TableCustomizer> list = new ArrayList<>();
        Set<Module> modules = c.getActiveModules();

        for (String sn : Arrays.asList(ALL, schemaName))
        {
            for (String qn : Arrays.asList(ALL, queryName))
            {
                if (_tableCustomizers.containsKey(sn))
                {
                    if (_tableCustomizers.get(sn).containsKey(qn))
                    {
                        for (Pair<Module, Class<? extends TableCustomizer>> pair : _tableCustomizers.get(sn).get(qn))
                        {
                            if (modules.contains(pair.first))
                            {
                                TableCustomizer tc = instantiateCustomizer(pair.second);
                                if (tc != null)
                                    list.add(tc);
                            }
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(list);
    }

    private TableCustomizer instantiateCustomizer(Class<? extends TableCustomizer> customizerClass)
    {
        try
        {
            return customizerClass.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            _log.error("Unable to create instance of class '" + customizerClass.getName() + "'", e);
        }

        return null;
    }

    @Override
    public void registerDemographicsProvider(DemographicsProvider provider)
    {
        _demographicsProviders.add(provider);
    }

    @Override
    public List<DemographicsProvider> getDemographicsProviders(final Container c, final User u)
    {
        return _demographicsProviders.stream().filter(d -> d.isAvailable(c, u)).toList();
    }

    @Override
    public @Nullable DemographicsProvider getDemographicsProviderByName(Container c, User u, String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("The DemographicsProvider name cannot be null");
        }

        for (DemographicsProvider d : getDemographicsProviders(c, u))
        {
            if (name.equals(d.getLabel()))
            {
                return d;
            }
        }

        return null;
    }
}
