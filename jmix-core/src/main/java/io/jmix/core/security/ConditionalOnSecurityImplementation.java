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

package io.jmix.core.security;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Indicates that a component is only eligible for registration when specified security implementation profile is selected.
 *
 * <p><strong>NOTE</strong>: Inheritance of {@code @OnSecurityImplementationCondition} annotations is not supported;
 * any conditions from superclasses or from overridden methods will not be considered.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnSecurityImplementationCondition.class)
public @interface ConditionalOnSecurityImplementation {

    String value();
}