package com.chrislaforetsoftware.mockingcontext.ioc;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "className")
class Pending {

    private final String className;

    private final Set<String> pendingDependencies = new HashSet<>();

    public Pending(String className) {
        this.className = className;
    }

    public void addPendingDependency(String className) {
        this.pendingDependencies.add(className);
    }

    public boolean isWaitingFor(String className) {
        if (this.pendingDependencies.contains(className)) {
            this.pendingDependencies.remove(className);
            return true;
        }
        return false;
    }

    public boolean isPending() {
        return !this.pendingDependencies.isEmpty();
    }
}

