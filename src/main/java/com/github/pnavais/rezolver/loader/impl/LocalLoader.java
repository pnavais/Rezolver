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

package com.github.pnavais.rezolver.loader.impl;

import com.github.pnavais.rezolver.loader.IFileSystemLoader;
import lombok.extern.java.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.regex.Matcher;

import static java.util.Objects.requireNonNull;

/**
 * <b>FileLoader</b>
 * <p>
 *  Resolves the location of a given resource on the local
 *  file system. In case no schema is specified
 *  in the given path, this loader will append a valid local one to the
 *  specified resource location string and try to resolve it as last resort.
 * </p>
 */
@Log
public class LocalLoader extends UrlLoader implements IFileSystemLoader {

    /** The file system for lookups */
    protected FileSystem fileSystem;

    /**
     * Constructor with default fallback path.
     */
    public LocalLoader() {
        fileSystem  = FileSystems.getDefault();
    }

    /**
     * Retrieves the URL from the given resource path
     * in the filesystem.
     *
     * @param location the path to the resource in the filesystem
     * @return the URL to the resource
     */
    @Override
    public URL lookup(String location) {
        URL resourceURL = null;
        try {
            if (location != null) {
                Path path = fileSystem.getPath(location);
                if (Files.exists(path)) {
                    resourceURL = path.toUri().toURL();
                }
            }
        } catch (MalformedURLException|InvalidPathException e) {
            // Last resort, remove trailing slashes
            String newLocation = location.replaceFirst("^[\\\\|/]+", "");
            if (!newLocation.equals(location)) {
                return lookup(newLocation);
            }
            log.throwing(getClass().getSimpleName(), "lookup", e);
        }

        return resourceURL;
    }

    /**
     * Strips the scheme from the given URL location.
     * Applies a last fallback location cleanup if
     * applicable e.g. in Windows the following
     * URI is valid (file:///c:/file).
     *
     * @param location the location
     * @return the location without scheme
     */
    @Override
    public String stripScheme(String location) {
        String newLocation;
        try {
            location = normalizePath(location);
            URL url = new URL(location);
            location = url.toExternalForm();
            String host = url.getHost();
            String auth = url.getAuthority();

            boolean emptyHost = (host != null) && (host.isEmpty());
            boolean emptyAuth = (auth != null) && (auth.isEmpty());

            String filter = ((emptyHost && emptyAuth) || (emptyHost && auth == null)) ? ":/*" : "://";
            String prefix = ((emptyHost && emptyAuth) || (emptyHost && auth == null)) ? url.getFile() : "";
            newLocation = prefix + location.replaceAll("^" + url.getProtocol() + filter + prefix, "");
        } catch (MalformedURLException e) {
            newLocation = location;
        }

        return newLocation;
    }

    /**
     * Extracts the scheme from the given location.
     * Initially check if the location represents a valid path,
     * otherwise try to check the URL by calling the base implementation.
     *
     * @param location the location
     * @return the extracted scheme
     */
    @Override
    public String extractScheme(String location) {
        String scheme = "";
        try {
            Path p = Paths.get(location);
            scheme = p.toUri().toURL().getProtocol();
        } catch (InvalidPathException | MalformedURLException ipe) {
            scheme = super.extractScheme(location);
        }
        return scheme;
    }

    /**
     * Retrieves the loader schema for
     * local URL resources.
     *
     * @return the loader schema
     */
    @Override
    public String getUrlScheme() {
        return "file";
    }

    /**
     * Retrieves the path separator for the loader.
     *
     * @return the path separator
     */
    @Override
    public String getPathSeparator() {
        return fileSystem.getSeparator();
    }

    /**
     * Sets the file system for file resolutions
     *
     * @param fileSystem the file system
     */
    @Override
    public void setFileSystem(FileSystem fileSystem) {
        requireNonNull(fileSystem);
        this.fileSystem = fileSystem;
    }

    /**
     * Fixes possible issues in the path
     * like backward slashes and missing escapes.
     *
     * @param path the path to normalize
     *
     * @return the normalized path
     */
    private String normalizePath(String path) {
        return Matcher.quoteReplacement(path.replace("\\", "/"));
    }

}
