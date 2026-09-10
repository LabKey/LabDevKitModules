/*
 * Copyright (c) 2012 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
var console = require("console");
var LABKEY = require("labkey");

var LDK = {};
exports.LDK = LDK;

LDK.Server = {};
LDK.Server.Utils = new function(){

    return {
        /**
         * The purpose of this method is to normalize the passed object is a JS date object.
         */
        normalizeDate: function(val, supppressErrors){
            if (!val){
                return null;
            }

            var normalizedVal;

            if (typeof val === '[object Date]'){
                normalizedVal = val;
            }
            else if (LABKEY.ExtAdapter.isString(val)){
                if (!supppressErrors)
                    console.warn('trigger script is being passed a date object as a string: ' + val);

                var javaDate = org.labkey.api.data.ConvertHelper.convert(val, java.util.Date);
                if (javaDate){
                    normalizedVal = new Date(javaDate.getTime());
                }
                else {
                    console.error('Unable to parse date string: ' + val);
                }
            }
            else if (!isNaN(val)){
                // NOTE: i'm not sure if we should really attempt this.  this should really never happen,
                // and it's probably an error if it does
                normalizedVal = new Date(val);
            }
            else {
                if (val['getTime']){
                    normalizedVal = new Date(val.getTime());
                }
                else {
                    if (!supppressErrors)
                        console.error('Unknown datatype for date value.  Type was: ' + (typeof val) + ' and value was: ' + val);
                }
            }

            // NOTE: in cases where dates are expected to match, like contiguous housing, it is important
            // for dates to line up exactly
            if (normalizedVal && normalizedVal.setMilliseconds)
                normalizedVal.setMilliseconds(0);

            return normalizedVal;
        },

        /**
         * A helper to remove the time-portion of a datetime field.
         */
        removeTimeFromDate: function(date){
            date = LDK.Server.Utils.normalizeDate(date);
            if (!date){
                return;
            }

            //normalize to a javascript date object
            date = new Date(date.getTime());
            return new Date(date.getFullYear(), date.getMonth(), date.getDate());
        },

        /**
         * Replaces each named field's value with the case-normalized value from its lookup target, adding an error for any value the target does not contain.
         * The helper must be created at script scope: it caches each target's allowable values per instance, so creating one per call would re-read the target table for every row.
         */
        normalizeLookupFields: function(helper, row, errors, lookupFields){
            for (var i=0;i<lookupFields.length;i++){
                var f = lookupFields[i];
                var val = row[f];
                if (!LABKEY.ExtAdapter.isEmpty(val)){
                    var normalizedVal = helper.getLookupValue(val, f);

                    if (LABKEY.ExtAdapter.isEmpty(normalizedVal))
                        errors[f] = 'Unknown value for field: ' + f + '. Value was: ' + val;
                    else
                        row[f] = normalizedVal;
                }
            }
        }
    }
}

