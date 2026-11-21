<%
/*
* Copyright (c) 2013-2026 LabKey Corporation
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
%>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.labkey.api.laboratory.query.TabbedReportFilterProvider" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.laboratory.LaboratoryServiceImpl" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>
<%!
    @Override
    public void addClientDependencies(ClientDependencies dependencies)
    {
        dependencies.add("laboratory.context");
        dependencies.add("/laboratory/panel/DataBrowserPanel.js");
        dependencies.add("LDK/panel/AbstractFilterType.js");
        dependencies.add("LDK/panel/SingleSubjectFilterType.js");
        dependencies.add("LDK/panel/MultiSubjectFilterType.js");
        dependencies.add("LDK/panel/NoFiltersFilterType.js");
    }

    private JSONArray getFilterConfig()
    {
        JSONArray ret = new JSONArray();

        for (TabbedReportFilterProvider p : LaboratoryServiceImpl.get().getTabbedReportFilterProviderProviders(getContainer(), getUser()))
        {
            if (p.isVisible(getContainer(), getUser()))
            {
                ret.put(new JSONObject()
                    .put("xtype", p.getXType())
                    .put("inputValue", p.getInputValue())
                    .put("label", p.getLabel())
                );
            }
        }

        return ret;
    }
%>
<%
    JspView<?> me = HttpView.currentView();
    String wpId = "wp_" + me.getWebPartRowId();
%>

<div id="<%=h(wpId)%>"></div>

<script type="text/javascript" nonce="<%=getScriptNonce()%>">
    var subjectId = LABKEY.ActionURL.getParameter('subjectId');

    Ext4.onReady(function(){
        Ext4.create('Laboratory.panel.DataBrowserPanel', {
            initialContext: subjectId ? {
                subjects: subjectId
            } : null,
            filterTypes: [{
                xtype: 'ldk-singlesubjectfiltertype',
                inputValue: LDK.panel.SingleSubjectFilterType.filterName,
                label: LDK.panel.SingleSubjectFilterType.DEFAULT_LABEL
            },{
                xtype: 'ldk-multisubjectfiltertype',
                inputValue: LDK.panel.MultiSubjectFilterType.filterName,
                label: LDK.panel.MultiSubjectFilterType.label
            }].concat(<%=json(getFilterConfig(), 0)%>, [{
                xtype: 'ldk-nofiltersfiltertype',
                inputValue: LDK.panel.NoFiltersFilterType.filterName,
                label: LDK.panel.NoFiltersFilterType.label
            }])
        }).render(<%=q(wpId)%>);
    });
</script>