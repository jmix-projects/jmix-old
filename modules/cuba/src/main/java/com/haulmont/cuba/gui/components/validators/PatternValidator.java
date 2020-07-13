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
package com.haulmont.cuba.gui.components.validators;

import com.haulmont.cuba.gui.components.Field;
import io.jmix.core.AppBeans;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.ui.component.ValidationException;
import io.jmix.ui.component.validation.RegexpValidator;
import org.dom4j.Element;

import java.util.regex.Pattern;

/**
 * @deprecated Use {@link RegexpValidator}
 */
@Deprecated
public class PatternValidator implements Field.Validator {

    protected Pattern pattern;
    protected String message;
    protected String messagesPack;
    protected Messages messages = AppBeans.get(Messages.NAME);
    protected MessageTools messageTools = AppBeans.get(MessageTools.NAME);

    public PatternValidator(Element element, String messagesPack) {
        this(element.attributeValue("pattern"));
        message = element.attributeValue("message");
        this.messagesPack = messagesPack;
    }

    public PatternValidator(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public void validate(Object value) throws ValidationException {
        if (value == null || !pattern.matcher(((String) value)).matches()) {
            String msg = message != null ? messageTools.loadString(messagesPack, message) : "Invalid value '%s'";
            throw new ValidationException(String.format(msg, value != null ? value : ""));
        }
    }
}