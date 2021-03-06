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

package net.contextfw.web.application.internal.development;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.contextfw.web.application.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

public class ReloadingClassLoaderConf {

    private final Set<Pattern> reloadablePackages = new HashSet<Pattern>();
    
    private final Set<String> reloadablePackageNames = new HashSet<String>();
    
    private final Set<Class<?>> excludedClasses = new HashSet<Class<?>>();
    
    public ReloadingClassLoaderConf(Configuration conf) {
        
        for (Object obj : conf.get(Configuration.RELOADABLE_CLASSES)) {
            if (obj instanceof Set<?>) {
                for (Object value : (Set<?>) obj) {
                    if (value instanceof Class<?>) {
                        excludedClasses.add((Class<?>) value);
                    }
                }
            } else if (obj instanceof String) {
                String str = (String) obj;
                addReloadablePackage(str);
            }
        }
        
        for (String viewPackage : conf.get(Configuration.VIEW_COMPONENT_ROOT_PACKAGE)) {
            addReloadablePackage(viewPackage + ":true");
        }
    }

    private void addReloadablePackage(String str) {
        String name = StringUtils.substringBefore(str, ":");
        boolean recursive = Boolean.parseBoolean(
                StringUtils.substringAfter(str, ":"));
        
        String postfix = recursive ? "\\..+" : "\\.[^\\.]+";
        
        reloadablePackageNames.add(name);
        reloadablePackages.add(
                    Pattern.compile("^" + name.replaceAll("\\.", "\\.") + postfix));
    }

    public Set<Class<?>> getExcludedClasses() {
        return excludedClasses;
    }

    public boolean isInReloadablePackage(String className) {
        for (Pattern pattern : reloadablePackages) {
            if (pattern.matcher(className).matches()) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getReloadablePackageNames() {
        return reloadablePackageNames;
    }
}