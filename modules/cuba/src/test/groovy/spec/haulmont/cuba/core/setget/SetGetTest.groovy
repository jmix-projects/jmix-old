/*
 * Copyright (c) 2008-2018 Haulmont.
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

package spec.haulmont.cuba.core.setget

import com.haulmont.cuba.core.model.Many2ManyA
import com.haulmont.cuba.core.model.Many2ManyB
import com.haulmont.cuba.core.model.SetGetEntity
import io.jmix.core.Metadata
import io.jmix.core.entity.StandardEntity
import spec.haulmont.cuba.core.CoreTestSpecification

import javax.inject.Inject

class SetGetTest extends CoreTestSpecification {
    @Inject
    private Metadata metadata

    private SetGetEntity<String> setGetEntity

    void setup() {
        setGetEntity = metadata.create(SetGetEntity.class)
    }

    def "ID and UUID"() {
        when:
        UUID uuid = setGetEntity.__getEntityEntry().getEntityValue("uuid")
        setGetEntity.__getEntityEntry().setEntityValue("id", uuid)
        UUID afterId = setGetEntity.__getEntityEntry().getEntityValue("id")

        then:
        uuid == afterId && uuid != null
    }

    def "Map"() {
        when:
        Map<String, Integer> map = new HashMap<>()
        map.put("key", 12)
        setGetEntity.__getEntityEntry().setEntityValue("map", map)
        Map<String, Integer> afterMap = setGetEntity.__getEntityEntry().getEntityValue("map")
        then:
        map.identity { afterMap }
    }

    def "Array"() {
        when:
        int[] intArray = new int[1]
        intArray[0] = 12
        setGetEntity.__getEntityEntry().setEntityValue("intArray", intArray)
        int[] afterIntArray = setGetEntity.__getEntityEntry().getEntityValue("intArray")

        then:
        intArray == afterIntArray
    }

    def "StandardEntity"() {
        when:
        StandardEntity[] standardEntityArray = new StandardEntity[1]
        standardEntityArray[0] = new SetGetEntity()
        setGetEntity.__getEntityEntry().setEntityValue("standardEntityArray", standardEntityArray)
        StandardEntity[] afterStandardEntityArray = setGetEntity.__getEntityEntry().getEntityValue("standardEntityArray")

        then:
        standardEntityArray == afterStandardEntityArray
    }

    def "Generic field"() {
        when:
        String genericField = "12"
        setGetEntity.__getEntityEntry().setEntityValue("genericField", genericField)
        String afterGenericValue = setGetEntity.__getEntityEntry().getEntityValue("genericField")

        then:
        genericField == afterGenericValue
    }

    def "Generic map"() {
        when:
        Map<String, Integer> genericMap = new HashMap<>()
        genericMap.put("key", 12)
        setGetEntity.__getEntityEntry().setEntityValue("genericMap", genericMap)
        Map<String, Integer> afterGenericMap = setGetEntity.__getEntityEntry().getEntityValue("genericMap")

        then:
        genericMap.identity { afterGenericMap }
    }

    def "Generic array"() {
        when:
        String[] genericArray = new String[1]
        genericArray[0] = "12"
        setGetEntity.__getEntityEntry().setEntityValue("genericArray", genericArray)
        String[] afterGenericArray = setGetEntity.__getEntityEntry().getEntityValue("genericArray")

        then:
        genericArray == afterGenericArray
    }

    def "Many to many"() {
        when:
        Set<Many2ManyB> collectionOfB = new HashSet<>()
        collectionOfB.add(metadata.create(Many2ManyB.class))
        Many2ManyA many2ManyA = metadata.create(Many2ManyA.class)
        many2ManyA.__getEntityEntry().setEntityValue("collectionOfB", collectionOfB)
        Set<Many2ManyB> afterCollectionOfB = many2ManyA.__getEntityEntry().getEntityValue("collectionOfB")

        then:
        collectionOfB == afterCollectionOfB
    }

    def "byte field"() {
        when:
        byte byteValue = 1
        setGetEntity.__getEntityEntry().setEntityValue("byteField", byteValue)
        byte afterByteValue = setGetEntity.__getEntityEntry().getEntityValue("byteField")

        then:
        byteValue == afterByteValue
    }

    def "char field"() {
        when:
        char charValue = 'a'
        setGetEntity.__getEntityEntry().setEntityValue("charField", charValue)
        char afterCharValue = setGetEntity.__getEntityEntry().getEntityValue("charField")

        then:
        charValue == afterCharValue
    }

    def "short field"() {
        when:
        short shortValue = 12
        setGetEntity.__getEntityEntry().setEntityValue("shortField", shortValue)
        short afterShortValue = setGetEntity.__getEntityEntry().getEntityValue("shortField")

        then:
        shortValue == afterShortValue
    }

    def "int field"() {
        when:
        int intValue = 12
        setGetEntity.__getEntityEntry().setEntityValue("intField", intValue)
        int afterIntValue = setGetEntity.__getEntityEntry().getEntityValue("intField")

        then:
        intValue == afterIntValue
    }

    def "long field"() {
        when:
        long longValue = 12L
        setGetEntity.__getEntityEntry().setEntityValue("longField", longValue)
        long afterLongValue = setGetEntity.__getEntityEntry().getEntityValue("longField")

        then:
        longValue == afterLongValue
    }

    def "float field"() {
        when:
        float floatValue = 12F
        setGetEntity.__getEntityEntry().setEntityValue("floatField", floatValue)
        float afterFloatValue = setGetEntity.__getEntityEntry().getEntityValue("floatField")

        then:
        floatValue == afterFloatValue
    }

    def "double field"() {
        when:
        double doubleValue = 12D
        setGetEntity.__getEntityEntry().setEntityValue("doubleField", doubleValue)
        double afterDoubleValue = setGetEntity.__getEntityEntry().getEntityValue("doubleField")

        then:
        doubleValue == afterDoubleValue
    }

    def "boolean field"() {
        when:
        boolean booleanValue = true
        setGetEntity.__getEntityEntry().setEntityValue("booleanField", booleanValue)
        boolean afterBooleanValue = setGetEntity.__getEntityEntry().getEntityValue("booleanField")

        then:
        booleanValue == afterBooleanValue
    }
}
