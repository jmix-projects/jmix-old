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

package io.jmix.data


import io.jmix.core.impl.jpql.DomainModel
import io.jmix.core.impl.jpql.model.EntityBuilder
import io.jmix.core.impl.jpql.transform.QueryTransformerAstBased
import spock.lang.Specification

class JpqlQueryTransformationTest extends Specification {

    DomainModel domainModel

    void setup() {
        def user = EntityBuilder.create()
                .startNewEntity('sec_User')
                .addStringAttribute("login")
                .addSingleValueAttribute(Long.class, "version")
                .produce()

        domainModel = new DomainModel(user)
    }


    def "transform case insensitive parameter"() {

        when: "with JPQL function and string arguments"

        def transformer = new QueryTransformerAstBased(domainModel, "select u from sec_User u where concat(u.name, ' ', u.login) = :name")
        transformer.handleCaseInsensitiveParam("name")

        def result = transformer.getResult()

        then:

        result == "select u from sec_User u where concat( lower ( u.name), ' ', lower ( u.login)) = :name"

        when: "with JPQL function and number arguments"

        transformer = new QueryTransformerAstBased(domainModel, "select u from sec_User u where concat(u.name, ' ', u.version) = :name")
        transformer.handleCaseInsensitiveParam("name")

        result = transformer.getResult()

        then:

        result == "select u from sec_User u where concat( lower ( u.name), ' ', u.version) = :name"
    }
}
