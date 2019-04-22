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

package io.jmix.remoting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When added to a bean implementation, results in the following:
 * <ul>
 *     <li>If an active profile matches the one specified in {@link #profile()} attribute ("server" by default),
 *          the framework exports the bean as HttpInvoker endpoint.
 *     <li>If no active profiles match, the framework creates a client proxy for invoking the remote bean via HttpInvoker.
 * </ul>
 * The annotation takes effect only if the "remoting" profile is also active.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {

    String profile() default "server";
}
