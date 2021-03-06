Ext4.define('Laboratory.panel.ManageDataSourcesPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.laboratory-managedatasourcespanel',

    initComponent: function(){
        Ext4.apply(this, {
            //width: 600,
            bodyStyle: 'padding: 5px;',
            border: false,
            buttonAlign: 'left',
            buttons: [{
                text: 'Add New',
                hidden: !Laboratory.Utils.isLaboratoryAdmin(),
                scope: this,
                handler: this.showAddEditWindow
            },{
                text: 'Add Default Sources',
                itemId: 'defaultSources',
                hidden: !Laboratory.Utils.isLaboratoryAdmin() || LDK.Utils.isSharedProject(),
                menu: []
            }],
            defaults: {
                border: false
            },
            items: [{
                html: 'Loading...'
            }]
        });

        this.callParent();

        Ext4.QuickTips.init();
        this.doLoad();
    },

    doLoad: function(){
        Laboratory.Utils.getAdditionalDataSources({
            scope: this,
            includeTotals: false,
            success: this.onLoad
        });

        this.removeAll();
        this.add({
            html: 'Loading...'
        });
    },

    onLoad: function(results){
        this.removeAll();

        var toAdd = {
            itemId: 'itemsPanel',
            layout: {
                type: 'table',
                columns: 9
            },
            defaults: {
                border: false,
                style: 'padding: 5px;'
            },
            bodyStyle: 'margin-bottom: 10px',
            items: [{
                html: '<b>Item Type</b>'
            },{
                html: '<b>Label</b>'
            },{
                html: '<b>Table Name</b>'
            },{
                html: '<b>Report Category</b>'
            },{
                html: '<b>Subject Field</b>'
            },{
                html: '<b>Date Field</b>'
            },{
                html: '<b>Use Workbooks?</b>'
            },{
                html: ''
            },{
                html: ''
            }]
        };

        if (results.sources && results.sources.length){
            results.sources = LDK.Utils.sortByProperty(results.sources, 'label', false);
            Ext4.each(results.sources, function(source, idx){
                if (source){
                    toAdd.items = toAdd.items.concat([{
                        source: source,
                        html: Laboratory.ITEM_CATEGORY[source.itemType].label
                    },{
                        html: source.label
                    },{
                        html: (source.canRead ? '<a href="' + source.browseUrl + '">' : '') + source.schemaName + '.' + source.queryName + (!source.containerPath ? "" : ' (' + source.containerPath + ')') + (source.canRead ? '</a>' : '')
                    },{
                        html: source.reportCategory
                    },{
                        html: source.subjectFieldKey
                    },{
                        html: source.sampleDateFieldKey
                    },{
                        html: Ext4.isEmpty(source.importIntoWorkbooks) ? '' : String(source.importIntoWorkbooks)
                    },{
                        xtype: 'button',
                        text: 'Edit',
                        hidden: !Laboratory.Utils.isLaboratoryAdmin(),
                        style: 'padding: 0px;',
                        border: true,
                        sourceIdx: idx,
                        handler: function(btn){
                            var panel = btn.up('laboratory-managedatasourcespanel');
                            var sources = panel.getSources();

                            panel.showAddEditWindow(btn, sources[btn.sourceIdx]);
                        }
                    },{
                        xtype: 'button',
                        text: 'Remove',
                        hidden: !Laboratory.Utils.isLaboratoryAdmin(),
                        style: 'padding: 0px;',
                        border: true,
                        sourceIdx: idx,
                        handler: function(btn){
                            var panel = btn.up('laboratory-managedatasourcespanel');
                            panel.removeSource(btn.sourceIdx);
                            var sources = panel.getSources();
                            panel.saveSources(sources);
                        }
                    }]);
                }
            }, this);
        }

        if (results.sources.length)
            this.add(toAdd);
        else
            this.add({
                html: 'No sources have been registered'
            });

        var btn = this.down('#defaultSources');
        btn.menu.removeAll();

        if (results.sharedSources){
            results.sharedSources = LDK.Utils.sortByProperty(results.sharedSources, 'label', false);
            var menuItems = [];
            Ext4.each(results.sharedSources, function(source){
                menuItems.push({
                    text: source.label + ' (' + (source.containerPath ? '"' + source.containerPath + '".' : '') + source.schemaName + '.' + source.queryName + ')',
                    xtype: 'menuitem',
                    source: source,
                    handler: function(btn){
                        var panel = btn.up('laboratory-managedatasourcespanel');
                        var sources = panel.getSources();

                        var present = false;
                        Ext4.each(sources, function(source){
                            if (source.schemaName == btn.source.schemaName && source.queryName == btn.source.queryName && (!btn.source.containerId ? source.containerId == LABKEY.Security.currentContainer.id : source.containerId == btn.source.containerId)){
                                present = true;
                                return false;
                            }
                        }, this);

                        if (!present){
                            var newSource = Ext4.apply({}, btn.source);
                            if (!newSource.containerId)
                                newSource.containerId = LABKEY.Security.currentContainer.id;

                            sources.push(newSource);
                            panel.saveSources(sources);
                        }
                        else
                        {
                            Ext4.Msg.alert('Error', 'This table has already been enabled in this folder');
                        }
                    }
                })
            }, this);

            if (menuItems.length){
                btn.menu.add(menuItems);
            }
        }
    },

    removeSource: function(idx){
        var panel = this.down('#itemsPanel');
        var size = panel.layout.columns;
        var start = (idx + 1) * size;

        for (var i=1;i<=size;i++){
            panel.remove(panel.items.get(size + start - i));
        }
    },

    showAddEditWindow: function(btn, existingItem){
        var dataTypes = [];
        for (var i in Laboratory.ITEM_CATEGORY){
            if ([Laboratory.ITEM_CATEGORY.tabbedReports.name].indexOf(i) == -1)
                dataTypes.push([Laboratory.ITEM_CATEGORY[i].name, Laboratory.ITEM_CATEGORY[i].label]);
        }

        var initialValues;
        if (existingItem){
            initialValues = {};
            initialValues.containerId = existingItem.containerId;
            initialValues.schemaName = existingItem.schemaName;
            initialValues.queryName = existingItem.queryName;
        }

        var panel = Ext4.define('Laboratory.panel.AddNewDataSourcePanel', {
            extend: 'Laboratory.panel.QueryPickerPanel',
            initialValues: initialValues,
            getItemsCfg: function(){
                var items = [];

                items.push({
                    xtype: 'combo',
                    editable: false,
                    fieldLabel: 'Item Type',
                    itemId: 'itemType',
                    helpPopup: 'This determines the section of the UI where this item will appear',
                    allowBlank: false,
                    queryMode: 'local',
                    valueField: 'name',
                    displayField: 'caption',
                    store: {
                        type: 'array',
                        fields: ['name', 'caption'],
                        data: dataTypes
                    }
                });

                items.push({
                    xtype: 'textfield',
                    fieldLabel: 'Label',
                    allowBlank: false,
                    itemId: 'label'
                });

                items.push({
                    xtype: 'textfield',
                    fieldLabel: 'Report Category',
                    helpPopup: 'This is the category used for reports.  On the reports tab, this is the section name.  On the data browser, this is the top-level tab.',
                    allowBlank: false,
                    itemId: 'reportCategory'
                });

                items.push({
                    xtype: 'textfield',
                    fieldLabel: 'Subject Field Name',
                    helpPopup: 'This is the name of the field holding the Subject Id, which will be used by the Data Browser for some filter types.  It can be left blank.',
                    itemId: 'subjectFieldKey'
                });

                items.push({
                    xtype: 'textfield',
                    fieldLabel: 'Date Field Name',
                    helpPopup: 'This is the name of the field holding the sample date, which will be used by the Data Browser for some filter types.  It can be left blank.',
                    itemId: 'sampleDateFieldKey'
                });

                items.push({
                    xtype: 'checkbox',
                    fieldLabel: 'Import Using Workbooks',
                    helpPopup: 'If selected (and the table supports workbooks), when importing into this table the user will be prompted to choose a workbook.  Otherwise the data will always be saved into the top-level folder and this data source will not be shown in workbooks',
                    itemId: 'importIntoWorkbooks'
                });

                items = items.concat(this.callParent());

                if (existingItem){
                    Ext4.Array.forEach(items, function(i){
                        if (i.itemId && existingItem[i.itemId]){
                            i.value = existingItem[i.itemId];
                        }
                    }, this);
                }

                return items;
            },

            onQueryChange: function(queryDef){

            }
        });

        Ext4.create('Ext.window.Window', {
            existingIndex: existingItem ? btn.sourceIdx : null,
            width: 400,
            title: 'Add Data Source',
            closeAction: 'destroy',
            modal: true,
            items: [Ext4.create('Laboratory.panel.AddNewDataSourcePanel')],
            buttons: [{
                text: 'Save',
                scope: this,
                handler: function(btn){
                    var win = btn.up('window');

                    //remove original item
                    if (win.existingIndex)
                        this.removeSource(win.existingIndex);

                    this.addSource(btn);
                }
            },{
                text: 'Cancel',
                handler: function(btn){
                    btn.up('window').close();
                }
            }]
        }).show(btn);
    },

    addSource: function(btn){
        var win = btn.up('window');
        var containerId = win.down('#containerId').getValue();
        var schemaName = win.down('#schemaName').getValue();
        var queryName = win.down('#queryName').getValue();
        var reportCategory = win.down('#reportCategory').getValue();
        var itemType = win.down('#itemType').getValue();
        var label = win.down('#label').getValue();
        var subjectFieldKey = win.down('#subjectFieldKey').getValue();
        var sampleDateFieldKey = win.down('#sampleDateFieldKey').getValue();
        var importIntoWorkbooks = win.down('#importIntoWorkbooks').getValue();

        if (!schemaName || !queryName || !label || !reportCategory || !itemType){
            Ext4.Msg.alert('Error', 'Must provide a schema, query, label, itemType and reportCategory');
            return;
        }

        var sources = this.getSources();
        sources.push({
            containerId: containerId,
            schemaName: schemaName,
            queryName: queryName,
            itemType: itemType,
            reportCategory: reportCategory,
            label: label,
            subjectFieldKey: subjectFieldKey,
            sampleDateFieldKey: sampleDateFieldKey,
            importIntoWorkbooks: importIntoWorkbooks
        });

        this.saveSources(sources, win);
    },

    getSourceIdx: function(label){
        var sources = this.getSources();
        for (var i=0;i<sources.length;i++){
            if (sources[i].label == label)
                return i;
        }
    },

    getSources: function(){
        var sources = [];
        var panel = this.down('#itemsPanel');
        if (panel){
            panel.items.each(function(item){
                if (item.source)
                    sources.push(item.source);
            }, this);
        }
        return sources;
    },

    saveSources: function(sources, win){
        Ext4.Msg.wait('Saving...');
        LABKEY.Ajax.request({
            url : LABKEY.ActionURL.buildURL('laboratory', 'setAdditionalDataSources'),
            params: {
                tables: Ext4.encode(sources)
            },
            method : 'POST',
            scope: this,
            failure: LDK.Utils.getErrorCallback(),
            success: function(result){
                Ext4.Msg.hide();

                if (win)
                    win.close();

                Ext4.Msg.alert('Success', 'Data Sources Updated', function(){
                    this.doLoad();
                }, this);
            }
        });
    }
});