/**
 * Copyright 2014 AppDynamics, Inc.
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
package com.appdynamics.extensions.business;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class ConfigUtil<T> {

    private static Logger logger = Logger.getLogger(ConfigUtil.class);

    public T readConfig(String configFilename, Class<T> clazz) throws FileNotFoundException {
        logger.info("Reading config file::" + configFilename);
        MyYamlConstructor constructor = new MyYamlConstructor(Configuration.class);
        constructor.addClassInfo(Configuration.class);
        Yaml yaml = new Yaml(constructor);
        T config = (T) yaml.load(new FileInputStream(configFilename));
        return config;
    }


    public static class MyYamlConstructor extends Constructor {
        private HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();

        public MyYamlConstructor(Class<? extends Object> theRoot) {
            super(theRoot);
        }

        public void addClassInfo(Class<? extends Object> c) {
            classMap.put(c.getName(), c);
        }

        /*
            * This is a modified version of the Constructor. Rather than using a class loader to
            * get external classes, they are already predefined above. This approach works similar to
            * the typeTags structure in the original constructor, except that class information is
            * pre-populated during initialization rather than runtime.
            *
            * @see org.yaml.snakeyaml.constructor.Constructor#getClassForNode(org.yaml.snakeyaml.nodes.Node)
            */
        protected Class<?> getClassForNode(Node node) {
            String name = node.getTag().getClassName();
            Class<?> cl = classMap.get(name);
            if (cl == null)
                throw new YAMLException("Class not found: " + name);
            else
                return cl;
        }
    }
}

