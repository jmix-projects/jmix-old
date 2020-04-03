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

package io.jmix.ui.persistence;

import io.jmix.core.*;
import io.jmix.core.commons.xmlparsing.Dom4jTools;
import io.jmix.core.entity.EntityValues;
import io.jmix.core.entity.User;
import io.jmix.core.security.UserSession;
import io.jmix.core.security.UserSessionSource;
import io.jmix.ui.components.Component;
import io.jmix.ui.components.ComponentsHelper;
import io.jmix.ui.presentations.Presentations;
import io.jmix.ui.presentations.PresentationsChangeListener;
import io.jmix.ui.presentations.model.Presentation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.inject.Inject;
import java.util.*;

public class PersistencePresentations implements Presentations {

    @Inject
    protected Metadata metadata;
    @Inject
    protected FetchPlanRepository fetchPlanRepository;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Dom4jTools dom4jTools;
    @Inject
    protected UserSessionSource sessionSource;
    @Inject
    protected EntityStates entityStates;

    protected String name;
    protected Map<Object, Presentation> presentations;
    protected Presentation current;
    protected Presentation def;

    protected Set<Presentation> needToUpdate = new HashSet<>();
    protected Set<Presentation> needToRemove = new HashSet<>();

    protected List<PresentationsChangeListener> listeners;


    public PersistencePresentations(Component c) {
        name = ComponentsHelper.getComponentPath(c);
    }

    @Override
    public void add(Presentation p) {
        checkLoad();
        presentations.put(EntityValues.<UUID>getId(p), p);
        if (entityStates.isNew(p)) {
            needToUpdate.add(p);

            if (BooleanUtils.isTrue(p.getDefault())) {
                def = p;
            }
        }
        firePresentationsSetChanged();
    }

    @Override
    public Presentation getCurrent() {
        checkLoad();
        return current;
    }

    @Override
    public void setCurrent(Presentation p) {
        checkLoad();
        if (p == null) {
            Object old = current;
            current = null;
            fireCurrentPresentationChanged(old);
        } else if (presentations.containsKey(EntityValues.<UUID>getId(p))) {
            Object old = current;
            current = p;
            fireCurrentPresentationChanged(old);
        } else {
            throw new IllegalStateException(String.format("Invalid presentation: %s", EntityValues.<UUID>getId(p)));
        }
    }

    @Override
    public Element getSettings(Presentation p) {
        p = getPresentation(EntityValues.<UUID>getId(p));
        if (p != null) {
            Document doc;
            if (!StringUtils.isEmpty(p.getXml())) {
                doc = dom4jTools.readDocument(p.getXml());
            } else {
                doc = DocumentHelper.createDocument();
                doc.setRootElement(doc.addElement("presentation"));
            }
            return doc.getRootElement();
        } else {
            return null;
        }
    }

    @Override
    public void setSettings(Presentation p, Element e) {
        p = getPresentation(EntityValues.<UUID>getId(p));
        if (p != null) {
            p.setXml(dom4jTools.writeDocument(e.getDocument(), false));
            modify(p);
        }
    }

    @Override
    public Presentation getPresentation(Object id) {
        checkLoad();
        return presentations.get(id);
    }

    @Override
    public String getCaption(Object id) {
        Presentation p = getPresentation(id);
        if (p != null) {
            return p.getName();
        }
        return null;
    }

    @Override
    public Collection<Object> getPresentationIds() {
        checkLoad();
        return Collections.unmodifiableCollection(presentations.keySet());
    }

    @Override
    public void setDefault(Presentation p) {
        checkLoad();
        if (p == null) {
            Object old = def;
            def = null;
            fireDefaultPresentationChanged(old);
        } else if (presentations.containsKey(EntityValues.<UUID>getId(p))) {
            Object old = def;
            if (def != null) {
                def.setDefault(false);
            }
            p.setDefault(true);
            def = p;
            fireDefaultPresentationChanged(old);
        } else {
            throw new IllegalStateException(String.format("Invalid presentation: %s", EntityValues.<UUID>getId(p)));
        }
    }

    @Override
    public Presentation getDefault() {
        return def;
    }

    @Override
    public void remove(Presentation p) {
        checkLoad();
        if (presentations.remove(EntityValues.<UUID>getId(p)) != null) {
            if (entityStates.isNew(p)) {
                needToUpdate.remove(p);
            } else {
                needToUpdate.remove(p);
                needToRemove.add(p);
            }

            if (p.equals(def)) {
                def = null;
            }

            if (p.equals(current)) {
                current = null;
            }

            firePresentationsSetChanged();
        }
    }

    @Override
    public void modify(Presentation p) {
        checkLoad();
        if (presentations.containsKey(EntityValues.<UUID>getId(p))) {
            needToUpdate.add(p);
            if (BooleanUtils.isTrue(p.getDefault())) {
                setDefault(p);
            } else if (def != null && EntityValues.<UUID>getId(def).equals(EntityValues.<UUID>getId(p))) {
                setDefault(null);
            }
        } else {
            throw new IllegalStateException(String.format("Invalid presentation: %s", EntityValues.<UUID>getId(p)));
        }
    }

    @Override
    public boolean isAutoSave(Presentation p) {
        p = getPresentation(EntityValues.<UUID>getId(p));
        return p != null && BooleanUtils.isTrue(p.getAutoSave());
    }

    @Override
    public boolean isGlobal(Presentation p) {
        p = getPresentation(EntityValues.<UUID>getId(p));
        return p != null && !entityStates.isNew(p) && p.getUserLogin() == null;
    }

    @Override
    public void commit() {
        if (!needToUpdate.isEmpty() || !needToRemove.isEmpty()) {
            SaveContext ctx = new SaveContext().saving(needToUpdate).removing(needToRemove);
            Set<Entity> commitResult = dataManager.save(ctx);
            commited(commitResult);

            clearCommitList();

            firePresentationsSetChanged();
        }
    }

    public void commited(Set<Entity> entities) {
        for (Entity entity : entities) {
            if (entity.equals(def))
                setDefault((Presentation) entity);
            else if (entity.equals(current))
                current = (Presentation) entity;

            if (presentations.containsKey(EntityValues.getId(entity))) {
                presentations.put(EntityValues.getId(entity), (Presentation) entity);
            }
        }
    }

    @Override
    public void addListener(PresentationsChangeListener listener) {
        if (listeners == null) {
            listeners = new LinkedList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(PresentationsChangeListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listeners = null;
            }
        }
    }

    @Override
    public Presentation getPresentationByName(String name) {
        for (Presentation p : presentations.values()) {
            if (name.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Presentation create() {
        return metadata.create(io.jmix.ui.persistence.entity.Presentation.class);
    }

    @Override
    public boolean isPresentationsAvailable() {
        return true;
    }

    protected void fireCurrentPresentationChanged(Object oldPresentationId) {
        if (listeners != null) {
            for (final PresentationsChangeListener listener : listeners) {
                listener.currentPresentationChanged(this, oldPresentationId);
            }
        }
    }

    protected void firePresentationsSetChanged() {
        if (listeners != null) {
            for (final PresentationsChangeListener listener : listeners) {
                listener.presentationsSetChanged(this);
            }
        }
    }

    protected void fireDefaultPresentationChanged(Object oldPresentationId) {
        if (listeners != null) {
            for (final PresentationsChangeListener listener : listeners) {
                listener.defaultPresentationChanged(this, oldPresentationId);
            }
        }
    }

    protected void checkLoad() {
        if (presentations == null) {
            LoadContext<io.jmix.ui.persistence.entity.Presentation> ctx
                    = new LoadContext<>(io.jmix.ui.persistence.entity.Presentation.class);

            ctx.setFetchPlan(fetchPlanRepository.getFetchPlan(
                    io.jmix.ui.persistence.entity.Presentation.class, "app"));

            UserSession session = sessionSource.getUserSession();
            // todo user substitution
            User user = session.getUser();

            ctx.setQueryString("select p from sec$Presentation p " +
                    "where p.componentId = :component and (p.userLogin is null or p.userLogin = :userLogin)")
                    .setParameter("component", name)
                    .setParameter("userLogin", user.getLogin());

            final List<io.jmix.ui.persistence.entity.Presentation> list = dataManager.loadList(ctx);

            presentations = new LinkedHashMap<>(list.size());
            for (final Presentation p : list) {
                presentations.put(EntityValues.<UUID>getId(p), p);
            }
        }
    }

    protected void clearCommitList() {
        needToUpdate.clear();
        needToRemove.clear();
    }
}
