/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.cloud.oracle.adm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cloud.oracle.OCIManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Pisl
 */

@ActionID(
        category = "Tools",
        id = "org.netbeans.modules.cloud.oracle.adm.RunFileADMAction"
)
@ActionRegistration(
        displayName = "#CTL_RunFileAudit",
        asynchronous = true
)

@ActionReferences(value = {
    @ActionReference(position = 251, path = "Loaders/text/x-maven-pom+xml/Actions"),
    @ActionReference(position = 1800, path = "Projects/org-netbeans-modules-maven/Actions")
})

@NbBundle.Messages({
    "CTL_RunFileAudit=Vulnerability Audit",})
public class RunFileADMAction implements ActionListener{

    private final FileObject file;

    public RunFileADMAction(FileObject file) {
        this.file = file;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Project project = FileOwnerQuery.getOwner(file);
        System.out.println("Running adm action:");
        System.out.println("  Project: " + project.toString());
        System.out.println("  File: " + file.getPath());
//        CloudResourcesStorage storage = project.getLookup().lookup(CloudResourcesStorage.class);
        KnowledgeBaseItem kbItem = VulnerabilityWorker.getKnowledgeBaseForProject(project);
        if (kbItem != null) {
            VulnerabilityWorker.getInstance().findVulnerability(project, true);
        } else {
            System.out.println("!!!!!! KnowledgeBase neni");
            if (OCIManager.getDefault().getConfigProvider() == null
                    || OCIManager.getDefault().getTenancy().equals(Optional.empty())) {
                String message = "Project Audits in GraalVM Ext Pack performs check for "
                        + "vulnerable dependencies using Vulnerabilities knowledgebase in Oracle OCI. "
                        + "If you want to learn more and setup OCI account, go to https://www.oracle.com/cloud/free/ .";
                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(message));
            } else {
                String message = "No Knowlege Base assigned for " + ProjectUtils.getInformation(project).getDisplayName() 
                        + ".\n Select or create in Cloud Explorer a Knowledge Base, where to run Vulnerability Audit for this project.";
                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(message));
            }
            System.out.println("Config provider: " + OCIManager.getDefault().getConfigProvider());
            System.out.println("Tenancy: " + OCIManager.getDefault().getTenancy());
        }
    }
    
}
