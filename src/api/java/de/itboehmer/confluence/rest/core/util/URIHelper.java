/**
 * Copyright 2016 Micromata GmbH
 * Modifications Copyright 2017 Martin Böhmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.itboehmer.confluence.rest.core.util;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;

/**
 * @author Christian Schulze (c.schulze@micromata.de)
 * @author Martin Böhmer
 */
public class URIHelper {

    public static URIBuilder buildPath(URI baseUri, String... paths) {
        URIBuilder uriBuilder = new URIBuilder(baseUri);
        String basePath = uriBuilder.getPath();
        for (String path : paths) {
            int parameterIndicatorPosition = path.indexOf("?");
            if (parameterIndicatorPosition >= 0) {
                String parameters = path.substring(parameterIndicatorPosition + 1);
                path = path.substring(0, parameterIndicatorPosition);
                uriBuilder.setQuery(parameters);
            }
            if (path.startsWith("/")) {
                basePath = basePath.concat(path);
            } else {
                basePath = basePath.concat("/").concat(path);
            }
        }
        uriBuilder.setPath(basePath);
        return uriBuilder;
    }
}
