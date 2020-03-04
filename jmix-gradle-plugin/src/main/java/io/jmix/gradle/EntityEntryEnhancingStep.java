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

package io.jmix.gradle;

import javassist.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static io.jmix.gradle.MetaModelUtil.*;

public class EntityEntryEnhancingStep extends BaseEnhancingStep {

    @Override
    protected boolean isAlreadyEnhanced(CtClass ctClass) throws NotFoundException {
        return isEntityEntryEnhanced(ctClass);
    }

    @Override
    protected String getEnhancingType() {
        return "Entity Entry Enhancer";
    }

    @Override
    protected void executeInternal(CtClass ctClass) throws IOException, CannotCompileException, NotFoundException {

        CtField primaryKey = getPrimaryKey(ctClass);

        if (primaryKey != null) {

            makeEntityEntryClass(ctClass, primaryKey);

            makeEntityEntryField(ctClass);

            makeEntityEntryMethod(ctClass);
        }

        ctClass.addInterface(classPool.get(ENTITY_ENTRY_ENHANCED_TYPE));
    }

    protected void makeEntityEntryClass(CtClass ctClass, CtField primaryKey) throws CannotCompileException, NotFoundException, IOException {
        CtClass nestedCtClass = ctClass.makeNestedClass(GEN_ENTITY_ENTITY_CLASS_NAME, true);
        nestedCtClass.setSuperclass(classPool.get(BASE_ENTITY_ENTRY_TYPE));

        CtMethod getIdMethod = CtNewMethod.make(classPool.get(OBJECT_TYPE), "getEntityId",
                null, null,
                String.format("return ((%s)getSource()).get%s();",
                        ctClass.getName(),
                        StringUtils.capitalize(primaryKey.getName())),
                nestedCtClass);
        nestedCtClass.addMethod(getIdMethod);

        CtMethod setIdMethod = CtNewMethod.make(CtClass.voidType, "setEntityId",
                new CtClass[]{classPool.get(OBJECT_TYPE)}, null,
                String.format("((%s)getSource()).set%s((%s)$1);",
                        ctClass.getName(),
                        StringUtils.capitalize(primaryKey.getName()),
                        primaryKey.getType().getName()),
                nestedCtClass);
        nestedCtClass.addMethod(setIdMethod);

        nestedCtClass.writeFile(outputDir);
    }

    protected void makeEntityEntryField(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtField ctField = new CtField(classPool.get(ENTITY_ENTRY_TYPE), GEN_ENTITY_ENTRY_VAR_NAME, ctClass);
        ctField.setModifiers(Modifier.PROTECTED);
        ctClass.addField(ctField);
    }

    protected void makeEntityEntryMethod(CtClass ctClass) throws NotFoundException, CannotCompileException {
        ctClass.addInterface(classPool.get(GENERIC_ENTITY_TYPE));

        CtMethod entryMethod = CtNewMethod.make(classPool.get(ENTITY_ENTRY_TYPE), "__getEntityEntry", null, null,
                String.format("return %s == null ? %s = new %s.%s(this) : %s;",
                        GEN_ENTITY_ENTRY_VAR_NAME, GEN_ENTITY_ENTRY_VAR_NAME, ctClass.getName(), GEN_ENTITY_ENTITY_CLASS_NAME, GEN_ENTITY_ENTRY_VAR_NAME),
                ctClass);

        ctClass.addMethod(entryMethod);
    }
}
