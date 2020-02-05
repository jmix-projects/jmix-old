/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package com.haulmont.cuba.core.testsupport;

import com.haulmont.cuba.core.model.common.User;
import io.jmix.data.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Service for integration testing. Don't use it in application code!
 */
@Service(TestingTransactionsOnMethodService.NAME)
public class TestingTransactionsOnMethodServiceBean implements TestingTransactionsOnMethodService {

    private final Logger log = LoggerFactory.getLogger(TestingTransactionsOnMethodServiceBean.class);

    @Inject
    private Persistence persistence;

    @Transactional
    @Override
    public void declarativeTransaction(UUID userId) {
        //noinspection ResultOfMethodCallIgnored
        persistence.getEntityManager().find(User.class, userId);

        persistence.getEntityManagerContext().setAttribute("test", "test_value");
    }
}
