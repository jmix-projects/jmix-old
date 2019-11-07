/*
 * Copyright 2019 Haulmont.
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

package io.jmix.ui.components.validators;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.BeanValidation;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.Field;

/**
 * Validator that applies JSR303 rules for {@link Field} instance using {@link BeanValidation}.
 *
 * @deprecated Use {@link BeanPropertyValidator} instead.
 */
@Deprecated
public class BeanValidator extends AbstractBeanValidator {
    public BeanValidator(Class beanClass, String beanProperty) {
        super(beanClass, beanProperty);

        init();
    }

    public BeanValidator(Class beanClass, String beanProperty, Class[] validationGroups) {
        super(beanClass, beanProperty, validationGroups);

        init();
    }

    protected void init() {
        this.messages = AppBeans.get(Messages.NAME);
        this.metadata = AppBeans.get(Metadata.NAME);
        this.beanValidation = AppBeans.get(BeanValidation.NAME);
    }
}