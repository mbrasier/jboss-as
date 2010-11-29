/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.deployment.processor;

import org.jboss.as.deployment.module.ModuleDeploymentProcessor;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceActivatorContextImpl;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * Deployment processor responsible for executing any ServiceActivator instances for a deployment.
 *
 * @author John Bailey
 */
public class ServiceActivatorProcessor implements DeploymentUnitProcessor {

    /**
     * If the deployment has a module attached it will ask the module to load the ServiceActivator services.
     *
     * @param context the deployment unit context
     */
    public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
        if(context.getAttachment(ServiceActivatorMarker.ATTACHMENT_KEY) == null)
            return; // Skip it if it has not been marked

        final Module module = context.getAttachment(ModuleDeploymentProcessor.MODULE_ATTACHMENT_KEY);
        if (module == null)
            return; // Skip deployments with no module

        final ServiceActivatorContext serviceActivatorContext = new ServiceActivatorContextImpl(context.getBatchBuilder(), null /* TODO: add a service registry to DeploymentUnitContext */);
        for(ServiceActivator serviceActivator : module.loadService(ServiceActivator.class)) {
            try {
                serviceActivator.activate(serviceActivatorContext);
            } catch (ServiceRegistryException e) {
                throw new DeploymentUnitProcessingException(e);
            }
        }
    }
}
