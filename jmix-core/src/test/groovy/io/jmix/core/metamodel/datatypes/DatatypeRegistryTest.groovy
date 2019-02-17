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

package io.jmix.core.metamodel.datatypes


import io.jmix.core.JmixCoreConfiguration
import io.jmix.core.metamodel.datatypes.impl.BigDecimalDatatype
import io.jmix.core.metamodel.datatypes.impl.BooleanDatatype
import io.jmix.core.metamodel.datatypes.impl.DateTimeDatatype
import io.jmix.core.metamodel.datatypes.impl.IntegerDatatype
import io.jmix.core.metamodel.datatypes.impl.LongDatatype
import io.jmix.core.metamodel.datatypes.impl.StringDatatype
import io.jmix.core.metamodel.datatypes.impl.UuidDatatype
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration])
class DatatypeRegistryTest extends Specification {

    @Autowired
    ApplicationContext context

    @Inject
    DatatypeRegistry registry

    def "context contains beans"() {
        when:

        context.getBean('jmix_BooleanDatatype', BooleanDatatype)
        context.getBean('jmix_IntegerDatatype', IntegerDatatype)
        context.getBean('jmix_LongDatatype', LongDatatype)
        context.getBean('jmix_BigDecimalDatatype', BigDecimalDatatype)
        context.getBean('jmix_StringDatatype', StringDatatype)
        context.getBean('jmix_DateTimeDatatype', DateTimeDatatype)
        context.getBean('jmix_UuidDatatype', UuidDatatype)

        then:

        noExceptionThrown()
    }

    def "test"() {

        def booleanDatatype = context.getBean('jmix_BooleanDatatype', BooleanDatatype)
        def integerDatatype = context.getBean('jmix_IntegerDatatype', IntegerDatatype)
        def longDatatype = context.getBean('jmix_LongDatatype', LongDatatype)
        def bigDecimalDatatype = context.getBean('jmix_BigDecimalDatatype', BigDecimalDatatype)
        def stringDatatype = context.getBean('jmix_StringDatatype', StringDatatype)
        def dateTimeDatatype = context.getBean('jmix_DateTimeDatatype', DateTimeDatatype)
        def uuidDatatype = context.getBean('jmix_UuidDatatype', UuidDatatype)

        expect:

        registry.get('boolean') == booleanDatatype
        registry.get('int') == integerDatatype
        registry.get('long') == longDatatype
        registry.get('decimal') == bigDecimalDatatype
        registry.get('string') == stringDatatype
        registry.get('dateTime') == dateTimeDatatype
        registry.get('uuid') == uuidDatatype

        registry.get(Boolean) == booleanDatatype
        registry.get(Long) == longDatatype
        registry.get(BigDecimal) == bigDecimalDatatype
        registry.get(String) == stringDatatype
        registry.get(Date) == dateTimeDatatype
        registry.get(UUID) == uuidDatatype
    }
}
