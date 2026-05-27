/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LDK.panel.CalendarPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ldk-calendarpanel',

    initComponent: function(){
        if (!this.store.events){
            console.log('creating store');
            this.store = Ext4.ComponentManager.create(this.store);
        }

        Ext4.apply(this, {
            title: 'Calendar Panel',
            calendarMap: {}
        });

        this.store.on('load', this.onStoreLoad);
        this.callParent();
    },

    onStoreLoad: function(store){

    },

    getCalendarConfig: function(){

    },

    getCalendarForRecord: function(rec){

    }
})