/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
Ext4.define('Laboratory.button.IconButton', {
    extend: 'LDK.button.LinkButton',
    alias: 'widget.laboratory-iconbutton',

    renderTpl: [
        '<div id="{id}-wrap" class="tool-icon">',
        '<a style="display: block;cursor: pointer;" ' +
        '<tpl if="href">href="{href}"</tpl>' +
        '<tpl if="tooltip"> data-qtip="{tooltip}"</tpl>' +
            '>' +
            '<tpl if="icon"><div><img alt="{text}" id="{id}-btnIconEl" src="{icon}"/ ></div></tpl>' +
            '{text}</a>',
        '</div>'
    ],
    renderSelectors: {
        linkEl: 'a'
    },

    initComponent: function() {
        this.callParent(arguments);

        Ext4.apply(this.renderData, {
            icon: this.icon
        });
    }

});