# Creating a Folder Listing Java-backed web script

### Expected result

![Alt](/result.png "Expected result")

### Creating the scripted components of a Folder Listing web script

1. สร้างไฟล์ list.get.desc.xml ที่ webscripts-tutorial-day2-platform-jar/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials/
```
       <webscript>
           <shortname>List folder contents</shortname>
           <description>Lists entries in a folder</description>
           <url>/sample/folder/list?path={folderPath}</url>
           <authentication>user</authentication>
           <format default="json"></format>
           <family>Training</family>
       </webscrip>
```

2. สร้างไฟล์ list.get.json.ftl ที่ webscripts-tutorial-day2-platform-jar/src/main/resources/alfresco/extension/templates/webscripts/alfresco/tutorials/
```
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
```

### Developing a controller for a Folder Listing Java-backed web script

1. สร้าง java class FolderListWebScript ที่ com.tutorial.platformsample
```
package com.tutorial.platformsample;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderUtil;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;

import java.util.*;

public class FolderListWebscript extends DeclarativeWebScript {
    private static Log logger = LogFactory.getLog(FolderListWebscript.class);

    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<String, Object>();

        String path = req.getParameter("path");

        NodeRef companyHome = serviceRegistry.getNodeLocatorService().getNode("companyhome", null, null);

        FileInfo fileInfo;
        try {

            fileInfo = serviceRegistry.getFileFolderService().resolveNamePath(companyHome, Arrays.asList(path.split("/")));

        } catch (FileNotFoundException e) {
            throw new WebScriptException(Status.STATUS_NOT_FOUND,
                    "Folder [" + path + "] not found");
        }
        List<ChildAssociationRef> childAssociationRefList =  serviceRegistry.getNodeService().getChildAssocs(fileInfo.getNodeRef());

        List<NodeRef> nodeRefs = new ArrayList<>();

        for(ChildAssociationRef childAssociationRef : childAssociationRefList) {
            nodeRefs.add(childAssociationRef.getChildRef());
        }

        model.put("folder", fileInfo.getNodeRef());
        model.put("children", nodeRefs);

        return model;
    }
}

```

2. ประกาศ spring bean เพื่อ register java class ที่สร้างเอาไว้ ที่ไฟล์ webscripts-tutorial-day2-platform-jar/src/main/resources/alfresco/module/webscripts-tutorial-day2-platform-jar/context/webscript-context.xml
```
<bean id="webscript.alfresco.tutorials.list.get"
		class="com.tutorial.platformsample.FolderListWebscript"
		parent="webscript">
		<property name="serviceRegistry" ref="ServiceRegistry" />
</bean>
```

3. run service ไปที่ root project แล้ว execute run.sh หรือ run.bat
