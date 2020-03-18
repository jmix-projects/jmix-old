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
import java.io.ObjectOutputStream;

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
        boolean embeddable = isJpaEmbeddable(ctClass);

        if (primaryKey != null) {

            makeEntityEntryClass(ctClass, primaryKey);

            makeEntityEntryField(ctClass);

            makeEntityEntryMethods(ctClass, String.format("%s.%s",ctClass.getName(), GEN_ENTITY_ENTRY_CLASS_NAME));

            if (!embeddable) {
                makeEqualsMethod(ctClass);

                makeHashCodeMethod(ctClass);

                makeToStringMethod(ctClass);

                makeWriteObjectMethod(ctClass);
            }
        } else if (embeddable) {
            makeEntityEntryField(ctClass);

            makeEntityEntryMethods(ctClass, EMBEDDABLE_ENTITY_ENTRY_TYPE);
        }

        ctClass.addInterface(classPool.get(ENTITY_ENTRY_ENHANCED_TYPE));
    }

    protected void makeEntityEntryClass(CtClass ctClass, CtField primaryKey) throws CannotCompileException, NotFoundException, IOException {
        CtClass nestedCtClass = ctClass.makeNestedClass(GEN_ENTITY_ENTRY_CLASS_NAME, true);

        if (primaryKey != null) {
            boolean pkGeneratedValue = isPkGeneratedValue(primaryKey);

            String getterName;
            String setterName;
            CtClass idType;
            if (pkGeneratedValue) {
                nestedCtClass.setSuperclass(classPool.get(BASE_DB_GENERATED_ID_ENTITY_ENTRY_TYPE));
                getterName = "getDbGeneratedId";
                setterName = "setDbGeneratedId";
                idType = classPool.get(Number.class.getName());
            } else {
                nestedCtClass.setSuperclass(classPool.get(BASE_ENTITY_ENTRY_TYPE));
                getterName = "getEntityId";
                setterName = "setEntityId";
                idType = classPool.get(Object.class.getName());
            }

            CtMethod getIdMethod = CtNewMethod.make(idType, getterName,
                    null, null,
                    String.format("return ((%s)getSource()).get%s();",
                            ctClass.getName(),
                            StringUtils.capitalize(primaryKey.getName())),
                    nestedCtClass);
            nestedCtClass.addMethod(getIdMethod);

            CtMethod setIdMethod = CtNewMethod.make(CtClass.voidType, setterName,
                    new CtClass[]{idType}, null,
                    String.format("((%s)getSource()).set%s((%s)$1);",
                            ctClass.getName(),
                            StringUtils.capitalize(primaryKey.getName()),
                            primaryKey.getType().getName()),
                    nestedCtClass);
            nestedCtClass.addMethod(setIdMethod);
        }

        nestedCtClass.writeFile(outputDir);
    }

    protected void makeEntityEntryField(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtField ctField = new CtField(classPool.get(ENTITY_ENTRY_TYPE), GEN_ENTITY_ENTRY_VAR_NAME, ctClass);
        ctField.setModifiers(Modifier.PROTECTED);
        ctClass.addField(ctField);
    }

    protected void makeEntityEntryMethods(CtClass ctClass, String entryClassName) throws NotFoundException, CannotCompileException {

        CtMethod entryMethod = CtNewMethod.make(classPool.get(ENTITY_ENTRY_TYPE), GET_ENTITY_ENTRY_METHOD_NAME, null, null,
                String.format("return %s == null ? %s = new %s(this) : %s;",
                        GEN_ENTITY_ENTRY_VAR_NAME, GEN_ENTITY_ENTRY_VAR_NAME, entryClassName, GEN_ENTITY_ENTRY_VAR_NAME),
                ctClass);

        ctClass.addMethod(entryMethod);

        CtMethod copyEntryMethod = CtNewMethod.make(CtClass.voidType, COPY_ENTITY_ENTRY_METHOD_NAME, null, null,
                String.format("{ %s newEntityEntry = new %s(this); newEntityEntry.copy(%s) ; %s = newEntityEntry; }",
                        entryClassName,
                        entryClassName,
                        GEN_ENTITY_ENTRY_VAR_NAME, GEN_ENTITY_ENTRY_VAR_NAME),
                ctClass);

        ctClass.addMethod(copyEntryMethod);
    }

    protected void makeEqualsMethod(CtClass ctClass) throws NotFoundException, CannotCompileException {
        if (findEqualsMethod(ctClass) == null) {
            CtMethod entryMethod = CtNewMethod.make(CtClass.booleanType, "equals", new CtClass[]{classPool.get(Object.class.getName())}, null,
                    "return io.jmix.core.impl.EntityInternals.equals(this, $1);", ctClass);
            ctClass.addMethod(entryMethod);
        }
    }

    protected void makeHashCodeMethod(CtClass ctClass) throws NotFoundException, CannotCompileException {
        if (findHashCodeMethod(ctClass) == null) {
            CtMethod entryMethod = CtNewMethod.make(CtClass.intType, "hashCode", null, null,
                    String.format("return %s().getEntityId() != null ? %s().getEntityId().hashCode() : super.hashCode();", GET_ENTITY_ENTRY_METHOD_NAME, GET_ENTITY_ENTRY_METHOD_NAME),
                    ctClass);
            ctClass.addMethod(entryMethod);
        }
    }

    protected void makeToStringMethod(CtClass ctClass) throws NotFoundException, CannotCompileException {
        if (findToStringMethod(ctClass) == null) {
            CtMethod entryMethod = CtNewMethod.make(classPool.get(String.class.getName()), "toString", null, null,
                    "return io.jmix.core.impl.EntityInternals.toString(this);", ctClass);
            ctClass.addMethod(entryMethod);
        }
    }

    protected void makeWriteObjectMethod(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod ctMethod = findWriteObjectMethod(ctClass);
        if (ctMethod != null) {
            ctMethod.insertBefore("io.jmix.core.impl.EntityInternals.writeObject(this, $1)");
        } else {
            CtMethod entryMethod = CtNewMethod.make(Modifier.PRIVATE,
                    CtClass.voidType, WRITE_OBJECT_METHOD_NAME,
                    new CtClass[]{classPool.get(ObjectOutputStream.class.getName())},
                    new CtClass[]{classPool.get(IOException.class.getName())},
                    "{ io.jmix.core.impl.EntityInternals.writeObject(this, $1); $1.defaultWriteObject(); }", ctClass);
            ctClass.addMethod(entryMethod);
        }
    }
}
