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

package io.jmix.remoting.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import javax.inject.Inject;

public class ClientProxyFactoryBean extends HttpInvokerProxyFactoryBean {

    @Inject
    protected ApplicationContext applicationContext;

    public void setHttpInvokerRequestExecutorBeanName(String beanName) {
        setHttpInvokerRequestExecutor((HttpInvokerRequestExecutor) applicationContext.getBean(beanName));
    }

}
