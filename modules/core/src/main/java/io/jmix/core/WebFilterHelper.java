/*
 * Copyright 2020 Haulmont.
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

package io.jmix.core;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;

@Component(WebFilterHelper.NAME)
public class WebFilterHelper {
    public static final String NAME = "core_WebFilterHelper";

    @Autowired
    protected ObjectProvider<FilterChainProxy> filterChainProvider;

    @Autowired
    protected ObjectProvider<FilterChain> filterChainsProvider;

    @SuppressWarnings("unchecked")
    @Nullable
    public  <T extends Filter> T findFilter(Class<T> filterClass) {
        return filterChainProvider.getObject().getFilterChains().stream()
                .flatMap(chain -> chain.getFilters().stream())
                .filter(f -> f.getClass().equals(filterClass))
                .map(f -> (T) f)
                .findFirst().orElse(null);
    }

    public FilterChainProxy getRootFilter(){
        return filterChainProvider.getObject();
    }

    public FilterChain getRootChain(){
        return filterChainsProvider.getObject();
    }
}
