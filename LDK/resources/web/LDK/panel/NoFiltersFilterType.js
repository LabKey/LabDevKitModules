/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LDK.panel.NoFiltersFilterType', {
    extend: 'LDK.panel.AbstractFilterType',
    alias: 'widget.ldk-nofiltersfiltertype',

    statics: {
        filterName: 'none',
        label: 'Entire Database'
    },

    initComponent: function(){
        this.callParent();
    },

    getFilters: function(){
        return null;
    },

    getTitle: function(){
        return '';
    }
});