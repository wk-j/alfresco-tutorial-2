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
