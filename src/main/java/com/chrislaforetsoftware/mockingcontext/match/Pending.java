package com.chrislaforetsoftware.mockingcontext.match;

import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(of = "classComponents")
public class Pending {

    private final ClassComponents classComponents;

    private final Set<String> pendingDependencies = new HashSet<>();

    public Pending(ClassComponents classComponents) {
        this.classComponents = classComponents;
    }

    public void addPendingDependency(String className) {
        this.pendingDependencies.add(className);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean checkAndCancelWaitingFor(String className) {
        if (this.pendingDependencies.contains(className)) {
            this.pendingDependencies.remove(className);
            return true;
        }
        return false;
    }

    public boolean isPending() {
        return !this.pendingDependencies.isEmpty();
    }

    public List<String> getPendingDependencies() {
        return new ArrayList<>(pendingDependencies);
    }

    public ClassComponents getClassComponents() {
        return this.classComponents;
    }

    public String getClassName() {
        return this.classComponents.getClassName();
    }
}

