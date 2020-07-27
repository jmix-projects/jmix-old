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

package io.jmix.ui.component.pagination;

import com.google.common.base.Splitter;
import io.jmix.ui.UiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(PaginationDelegate.NAME)
public class PaginationDelegate {
    public static final String NAME = "ui_PaginationDelegate";

    protected UiProperties uiProperties;

    @Autowired
    public void setUiProperties(UiProperties uiProperties) {
        this.uiProperties = uiProperties;
    }

    public int findClosestValue(int maxResults, List<Integer> optionsList) {
        int minimumValue = Integer.MAX_VALUE;
        int closest = maxResults;

        for (int option : optionsList) {
            int diff = Math.abs(option - maxResults);
            if (diff < minimumValue) {
                minimumValue = diff;
                closest = option;
            }
        }

        return closest;
    }

    public List<Integer> getMaxResultsOptions() {
        String maxResultsProperty = uiProperties.getPaginationMaxResults();
        Iterable<String> split = Splitter.on(",").trimResults().split(maxResultsProperty);

        List<Integer> result = new ArrayList<>();
        for (String option : split) {
            if (!"NULL".equals(option)) {
                try {
                    Integer value = Integer.valueOf(option);
                    result.add(value);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return result;
    }

    public boolean isNullOptionVisible() {
        String maxResultsProperty = uiProperties.getPaginationMaxResults();
        return maxResultsProperty.startsWith("NULL");
    }
}
