package com.chrislaforetsoftware.mockingcontext.ioc;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "className")
public class Injectable {
    private String className;
    private Object instance;

    public Injectable(String className, Object instance) {
        this.className = className;
        this.instance = instance;
    }
}
