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

package io.jmix.remoting.impl

import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

class AuthHeaderTest extends Specification {

    def "encode"() {
        def sessionId = UUID.randomUUID()
        def clientToken = RandomStringUtils.randomAlphanumeric(20)

        when:

        def val = AuthHeader.encode(sessionId)

        then:

        val == "JmixRmt s=$sessionId"

        when:

        def val1 = AuthHeader.encode(clientToken, 'user1')

        then:

        val1 == "JmixRmt c=${Base64.encoder.encodeToString(clientToken.bytes)},u=user1"
    }

    def "decode"() {

        when:

        def header = AuthHeader.decode('JmixRmt s=b3ac0907-a352-447b-b059-64be1d52999c')

        then:

        header.sessionId == 'b3ac0907-a352-447b-b059-64be1d52999c'
        header.user == null
        header.clientToken == null

        when:

        def header1 = AuthHeader.decode(
                'JmixRmt c=YWJjZGVmMTIzNDU=,u=user1')

        then:

        header1.sessionId == null
        header1.user == 'user1'
        header1.clientToken == 'abcdef12345'
    }
}
