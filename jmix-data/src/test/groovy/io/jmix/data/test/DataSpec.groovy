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

package io.jmix.data.test

import com.sample.app.TestAppConfiguration
import io.jmix.core.JmixCoreConfiguration
import io.jmix.core.commons.db.QueryRunner
import io.jmix.data.JmixDataConfiguration
import io.jmix.data.Persistence
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.inject.Inject

@ContextConfiguration(classes = [JmixCoreConfiguration, JmixDataConfiguration, JmixDataTestConfiguration, TestAppConfiguration])
class DataSpec extends Specification {

    @Inject Persistence persistence

    void setup() {
        persistence.createTransaction().commit()
    }

    void cleanup() {
        def runner = new QueryRunner(persistence.getDataSource())
        runner.update('delete from TEST_APP_ENTITY_ITEM')
        runner.update('delete from TEST_SECOND_APP_ENTITY')
        runner.update('delete from TEST_APP_ENTITY')
        runner.update('delete from TEST_IDENTITY_ID_ENTITY')
        runner.update('delete from TEST_IDENTITY_UUID_ENTITY')
    }

}
