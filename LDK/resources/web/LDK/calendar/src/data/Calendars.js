/*
 * Copyright (c) 2013-2026 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
(function(Ext){

Ext.define('Ext.calendar.data.Calendars', {
    statics: {
        getData: function(){
            return {
                "calendars":[{
                    "id":    1,
                    "title": "Home"
                },{
                    "id":    2,
                    "title": "Work"
                },{
                    "id":    3,
                    "title": "School"
                }]
            };    
        }
    }
});

})(Ext4)