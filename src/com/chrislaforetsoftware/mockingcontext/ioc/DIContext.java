// This code is based on Martin Häusler's articles on DEV entitled "Understanding Dependency Injection."
// Originally licensed under Apache License 2.0

package com.chrislaforetsoftware.mockingcontext.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DIContext {
    // the Dependency Injection Container in formal terms
    // Spring refers to this as the ApplicationContext

    private final Set<Object> serviceInstances = new HashSet<>();

    public DIContext(Collection<Class<?>> serviceClasses) throws Exception {
        // step 1: create an instance of each service class
        for (Class<?> serviceClass : serviceClasses) {
            Constructor<?> constructor = serviceClass.getConstructor();
            constructor.setAccessible(true);            // defensive programming to ensure private constructors will work too
            Object serviceInstance = constructor.newInstance();
            this.serviceInstances.add(serviceInstance);        // creates the instance for use below
        }

        // step 2: wire them together
        for (Object serviceInstance : serviceInstances) {
            for (Field field : serviceInstance.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    // this field is none of our business
                    continue;
                }

                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                // find a suitable matching service instance
                for (Object matchPartner : serviceInstances) {
                    if (fieldType.isInstance(matchPartner)) {
                        field.set(serviceInstance, matchPartner);
                    }
                }
            }
        }
    }

    public static DIContext createContextForPackage(String rootPackageName) throws Exception {
        Set<Class<?>> allClassesInPackage = PathScanner.getAllClassesInPackage(rootPackageName);
        Set<Class<?>> serviceClasses = new HashSet<>();
        for (Class<?> aClass : allClassesInPackage) {
//            if (aClass.isAnnotationPresent(Service.class)) {	// only interested in our @Services!
//                serviceClasses.add(aClass);
//            }
        }
        return new DIContext(serviceClasses);
    }

    @SuppressWarnings("unchecked")
    public <T> T getServiceInstance(Class<T> serviceClass) {
        for (Object serviceInstance : this.serviceInstances) {
            if (serviceClass.isInstance(serviceInstance)) {
                return (T) serviceInstance;
            }
        }
        return null;
    }
}

