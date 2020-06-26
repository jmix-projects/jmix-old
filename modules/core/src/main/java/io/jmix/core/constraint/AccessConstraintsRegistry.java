package io.jmix.core.constraint;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccessConstraintsRegistry {

    List<AccessConstraint<?>> constraints;

    public void register(Class<? extends AccessConstraint<?>> constraintClass) {

    }

    public List<AccessConstraint<?>> getConstraints() {
        return constraints;
    }

    public Collection<? extends AccessConstraint<?>> getConstraintsOfType(Class<?> accessConstraintClass) {
        return constraints.stream()
                .filter(constraint -> accessConstraintClass.isAssignableFrom(constraint.getClass()))
                .collect(Collectors.toList());
    }
}
