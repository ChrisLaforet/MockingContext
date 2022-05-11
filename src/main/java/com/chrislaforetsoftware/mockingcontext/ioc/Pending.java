package com.chrislaforetsoftware.mockingcontext.ioc;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "className")
class Pending {

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

    public ClassComponents getClassComponents() {
        return this.classComponents;
    }

    public String getClassName() {
        return this.classComponents.getClassName();
    }
}

