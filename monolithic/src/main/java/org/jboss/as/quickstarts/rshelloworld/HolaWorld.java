/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.rshelloworld;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.InetAddress;
import java.net.UnknownHostException;
 

/**
 * A simple REST service which is able to say hola to someone using HolaService Please take a look at the web.xml where JAX-RS
 * is enabled
 *
 * @author gbrey@redhat.com
 *
 */

@Path("/")
public class HolaWorld {
    @Inject
    HolaService holaService;

    String myHostname = "Not Found";

    public HolaWorld(){
        try {
            this.myHostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.myHostname = "Error";
        }
    }

    @GET
    @Path("/hola")
    @Produces({ "text/plain" })
    public String getHolaWorldText() {
        return holaService.createHolaMessage("de " + myHostname);
    }

}
