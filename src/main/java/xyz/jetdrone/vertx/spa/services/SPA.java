/*
 * Copyright 2019 Paulo Lopes
 *
 * ES4X licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package xyz.jetdrone.vertx.spa.services;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import xyz.jetdrone.vertx.spa.services.impl.SPAImpl;

/**
 * A set of utilities to work with SPA and vert.x
 */
@VertxGen
public interface SPA {

    /**
     * Runs the SPA build tool in development mode, e.g.: npm start
     *
     * @param workingDir the location of the client SPA application
     * @param port the port where the build tool will listen
     * @return a redirect handler to the configured server.
     */
    static Handler<RoutingContext> serve(String workingDir, int port) {
        return SPAImpl.serve(workingDir, port);
    }

    /**
     * Runs the SPA build tool in development mode, given a custom command line e.g.: yarn start
     *
     * @param workingDir the location of the client SPA application
     * @param port the port where the build tool will listen
     * @return a redirect handler to the configured server.
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static Handler<RoutingContext> serve(String workingDir, int port, String... commandLine) {
        return SPAImpl.serve(workingDir, port, commandLine);
    }

    /**
     * Stops the build dev server if any.
     */
    static void stop() {
        SPAImpl.stop();
    }
}
