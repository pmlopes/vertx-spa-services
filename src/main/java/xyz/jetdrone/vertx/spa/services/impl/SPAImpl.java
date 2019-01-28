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
package xyz.jetdrone.vertx.spa.services.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SPAImpl {

    private static final String SYSTEM_PROPERTY_NAME = "vertx.environment";
    private static final String ENV_VARIABLE_NAME = "VERTX_ENVIRONMENT";

    private static Process proc;

    /**
     * The current mode from the system properties with fallback to environment variables
     *
     * @return String with mode value or null
     */
    private static String mode() {
        return System.getProperty(SYSTEM_PROPERTY_NAME, System.getenv(ENV_VARIABLE_NAME));
    }

    /**
     * Will return true if the mode is not null and equals ignoring case the string "dev"
     *
     * @return always boolean
     */
    private static boolean development() {
        final String mode = mode();
        return "dev".equalsIgnoreCase(mode);
    }

    /**
     * Creates a redirect handler to the angular server using the defaults
     */
    public static Handler<RoutingContext> serve(String spaDir, int port) {
        return serve(spaDir, port, "npm", "start", "--");
    }

    public static synchronized Handler<RoutingContext> serve(String spaDir, int port, String... commandLine) {
        if (!development()) {
            // NOOP
            return RoutingContext::next;
        }

        try {
            if (proc == null) {
                final File wd = new File(System.getProperty("user.dir"), spaDir);

                final List<String> args = new ArrayList<>(Arrays.asList(commandLine));

                // if there is a proxy conf we append the args: --proxy-config proxy.conf.json
                if (new File(wd, "proxy.conf.json").exists()) {
                    args.add("--proxy-config");
                    args.add("proxy.conf.json");
                }
                // if there is a angular.json we append the args: --port port
                if (new File(wd, "angular.json").exists()) {
                    args.add("--port");
                    args.add(Integer.toString(port));
                }

                ProcessBuilder pb = new ProcessBuilder(args);
                pb.environment().put("PORT", Integer.toString(port));
                pb.directory(wd);
                pb.inheritIO();

                proc = pb.start();
            }

            return ctx -> ctx.response()
                            .setStatusCode(307)
                            .putHeader("Location", "http://localhost:" + port + ctx.request().uri())
                            .end();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the running process if any
     */
    public static synchronized void stop() {

        if (proc != null) {
            proc.destroy();
            try {
                // wait for the process to really stop
                proc.waitFor();
            } catch (InterruptedException e) {
                // ignore
            }
            try {
                // leeway since sometimes the unbind happens after
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }
            proc = null;
        }
    }
}
