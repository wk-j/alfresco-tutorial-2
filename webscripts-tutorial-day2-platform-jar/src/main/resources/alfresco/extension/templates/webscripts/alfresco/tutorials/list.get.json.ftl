{
    "name": "${folder.name}",
    "nodeId": "${folder.id}",
    "children": [
    <#list children as child>
            {
                "name": "${child.name}",
                "type": "<#if child.isContainer>Folder<#else>File</#if>",
                "nodeId": "${child.id}",
                "creator": "${child.properties["cm:creator"]}"
            }
            <#if (child_has_next)>,</#if>
    </#list>
    ]
}