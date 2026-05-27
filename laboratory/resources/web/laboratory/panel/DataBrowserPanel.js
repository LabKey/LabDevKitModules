/*
 * Copyright (c) 2025-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('Laboratory.panel.DataBrowserPanel', {
    extend: 'LDK.panel.TabbedReportPanel',

    initComponent: function(){
        Ext4.ns('Laboratory.tabbedReports');

        Ext4.apply(this, {
            reportNamespace: Laboratory.tabbedReports
        });

        Ext4.Msg.wait('Loading...');
        Laboratory.Utils.getDataItems({
            types: ['tabbedReports'],
            scope: this,
            success: this.onDataLoad,
            failure: LDK.Utils.getErrorCallback()
        });

        this.callParent();
    },

    onDataLoad: function(results){
        Ext4.Msg.hide();
        this.reports = [];
        var foundDefault = false;
        Ext4.each(results.tabbedReports, function(report){
            LDK.Assert.assertNotEmpty('Tabbed Report is null', report);
            if (report && report.key){
                report.id = report.key.replace(/:/g, '_');
                report.category = report.reportCategory;

                if (report.targetContainer){
                    report.containerPath = report.targetContainer;
                }
                this.reports.push(report);

                if (report.isDefaultReport) {
                    this.defaultReport = report.id;
                    if (foundDefault) {
                        log.error('More than one TabbedReport marked as default!');
                    }

                    foundDefault = true;
                    console.log(this.defaultReport);
                }
            }
        }, this);

        this.reports = LDK.Utils.sortByProperty(this.reports, 'name', false);
        this.reports = LDK.Utils.sortByProperty(this.reports, 'reportCategory', false);

        this.createTabPanel();
    },

    filterTypes: [{
        xtype: 'ldk-singlesubjectfiltertype',
        inputValue: LDK.panel.SingleSubjectFilterType.filterName,
        label: LDK.panel.SingleSubjectFilterType.DEFAULT_LABEL
    },{
        xtype: 'ldk-multisubjectfiltertype',
        inputValue: LDK.panel.MultiSubjectFilterType.filterName,
        label: LDK.panel.MultiSubjectFilterType.label
    },{
        xtype: 'ldk-nofiltersfiltertype',
        inputValue: LDK.panel.NoFiltersFilterType.filterName,
        label: LDK.panel.NoFiltersFilterType.label
    }]
});