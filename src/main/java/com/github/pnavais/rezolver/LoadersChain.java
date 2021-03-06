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

package com.github.pnavais.rezolver;

import com.github.pnavais.rezolver.loader.IResourceLoader;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A {@link LoadersChain} contains several context aware loader implementations
 * and is intended for sequential iteration.
 */
public class LoadersChain {

    /**
     * The Loaders chain.
     */
    private Collection<IResourceLoader> chain;

    /**
     * Instantiates a new Loaders chain.
     */
    public LoadersChain() {
        this.chain = new ArrayDeque<>();
    }

    /**
     * Instantiates a new Loaders chain with the given
     * items.
     * @param loadersChain the loader items
     */
    public LoadersChain(Collection<IResourceLoader> loadersChain) {
        requireNonNull(loadersChain);
        this.chain = loadersChain;
    }

    /**
     * Adds a new loader to the chain
     *
     * @param loader the loader to add
     */
    public LoadersChain add(IResourceLoader loader) {
        this.chain.add(loader);
        return this;
    }

    /**
     * Removes a loader from the chain
     *
     * @param loader the loader to remove
     */
    public void remove(IResourceLoader loader) {
        this.chain.remove(loader);
    }

    /**
     * Creates a loaders chain from the given parameterized varargs
     * of loaders
     * @param loaders the loaders
     * @return the loaders chain
     */
    public static LoadersChain from(Collection<IResourceLoader> loaders) {
        return new LoadersChain(loaders);
    }

    /**
     * Handles the request by passing the resourcePath
     * through the loaders in the chain stopping at the first
     * match found.
     *
     * @param resourcePath the path to the resource to be resolved
     * @return the resource information
     */
    public ResourceInfo process(String resourcePath) {
        ResourceInfo resInfo = null;

        for (IResourceLoader loader : chain) {
            resInfo = loader.resolve(resourcePath);
            if (resInfo.isResolved()) {
                break;
            }
        }

        return resInfo;
    }

    /**
     * Clears the list of loaders
     */
    public void clear() {
        Optional.ofNullable(this.chain).ifPresent(Collection::clear);
    }
}
