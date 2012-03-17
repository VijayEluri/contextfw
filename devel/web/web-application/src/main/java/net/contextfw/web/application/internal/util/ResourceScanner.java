/**
 * Copyright 2010 Marko Lavikainen
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
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Locates all resources with given extension from paths
 * 
 * @author marko
 * 
 */
public class ResourceScanner extends AbstractScanner {

    public static List<ResourceEntry> findResources(List<String> resourcePaths,
            Pattern acceptor) {
        
        List<ResourceEntry> entries = findResourceEntries(resourcePaths);
        List<ResourceEntry> rv = new ArrayList<ResourceEntry>();
        
        for (ResourceEntry entry : entries) {
            if (acceptor.matcher(entry.getPath()).matches()) {
                rv.add(entry);
            }
        }        
        
        return rv;
    }
}
