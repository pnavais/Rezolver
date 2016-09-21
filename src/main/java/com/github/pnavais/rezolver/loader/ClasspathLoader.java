/*
 * Copyright 2016 Pablo Navais
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pnavais.rezolver.loader;

import com.github.pnavais.rezolver.Context;

import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * <b>classpathLoader</b>
 * <p>
 *  Resolves the location of a given resource either on the classpath.
 *  In case no schema is specified, this loader will append a valid one to the
 *  specified resource location string and try to resolve it as last resort.
 * </p>
 */
public class ClasspathLoader extends AbstractLoader {

    /** The classloader for classpath lookup */
    private ClassLoader classLoader;

    /**
     * Constructor with default application path
     */
    public ClasspathLoader() {
        this("META-INF");
    }

    /**
     * Constructor with custom fallback path.
     *
     * @param path the path to append when resolution fails.
     */
    public ClasspathLoader(String path) {
        this.fallbackPath = path;
        this.classLoader = getClass().getClassLoader();
    }

    @Override
    public URL resolveResource(String resourcePath) {
        // Check the resource in the same class loader
        URL resourceURL = classLoader.getResource(resourcePath);
        // Fallback to the system class loader
        if (resourceURL == null) {
            resourceURL = ClassLoader.getSystemResource(resourcePath);
        }
        return resourceURL;
    }

    /**
     * Retrieves the URL scheme associated to the loader
     *
     * @return the URL scheme
     */
    @Override
    public String getUrlScheme() {
        return "classpath";
    }

    /**
     * Retrieves the path separator for the loader.
     *
     * @return the path separator
     */
    @Override
    protected String getPathSeparator() {
        return "/";
    }

    /**
     * Sets the classloader for classpath resolution
     *
     * @param classLoader the classloader
     */
    public void setClassLoader(ClassLoader classLoader) {
        requireNonNull(classLoader);
        this.classLoader = classLoader;
    }

}
