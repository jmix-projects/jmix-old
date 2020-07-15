/*
 * Copyright 2020 Haulmont.
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

package io.jmix.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.spring.annotation.VaadinSessionScope;
import io.jmix.core.CoreProperties;
import io.jmix.core.WebFilterHelper;
import io.jmix.core.security.LoginException;
import io.jmix.core.security.LogoutRequestMatcher;
import io.jmix.core.security.SecurityContextHelper;
import io.jmix.core.security.UserRepository;
import io.jmix.ui.util.OperationResult;
import org.atmosphere.util.AtmosphereFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component(App.NAME)
@VaadinSessionScope
public class JmixApp extends App {

    private Logger log = LoggerFactory.getLogger(JmixApp.class);

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CoreProperties coreProperties;

    @Autowired
    protected WebFilterHelper webFilterHelper;

    @Override
    public void loginOnStart() throws LoginException {
        initializeUi();
    }

    @Override
    protected String routeTopLevelWindowId() {
        if (isAnonymousAuthentication()) {
            String screenId = uiProperties.getLoginScreenId();
            if (!windowConfig.hasWindow(screenId)) {
                screenId = uiProperties.getMainScreenId();
            }
            return screenId;
        } else {
            return uiProperties.getMainScreenId();
        }
    }

    @Override
    public OperationResult logout() {
        closeWindowsInternal(true);
        removeAllWindows(Collections.singletonList(AppUI.getCurrent()));

        LogoutFilter logoutFilter = webFilterHelper.findFilter(LogoutFilter.class);
        try {
            if (logoutFilter != null) {
                VaadinRequest.getCurrent().setAttribute(LogoutRequestMatcher.LOGOUT_ATTRIBUTE, true);
                logoutFilter.doFilter(VaadinServletRequest.getCurrent(), VaadinServletResponse.getCurrent(), new AtmosphereFilterChain());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        //todo MG authorities
        AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                coreProperties.getAnonymousAuthenticationTokenKey(),
                userRepository.getAnonymousUser(),
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContextHelper.setAuthentication(anonymousToken);
        initializeUi();
        return OperationResult.success();
    }

    protected void initializeUi() {
        AppUI currentUi = AppUI.getCurrent();
        if (currentUi != null) {
            createTopLevelWindow(currentUi);
        }
    }

    private boolean isAnonymousAuthentication() {
        Authentication authentication = SecurityContextHelper.getAuthentication();
        return authentication == null ||
                authentication instanceof AnonymousAuthenticationToken;
    }
}
