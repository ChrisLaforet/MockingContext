// This code is based on Martin Häusler's articles on DEV entitled "Understanding Dependency Injection."
// Originally licensed under Apache License 2.0

package com.chrislaforetsoftware.mockingcontext.ioc;

import java.io.File;
import java.net.URL;
import java.util.*;

public class PathScanner {

    // NOTE: this code is very much simplified;
    // it works, but do not use it in production!
    public static Set<Class<?>> getAllClassesInPackage(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName == null ?
                "" :
                packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        Set<Class<?>> classes = new HashSet<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file,  assembleNameFrom(packageName, file.getName())));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(assembleNameFrom(packageName, file.getName().substring(0, file.getName().length() - 6))));
            }
        }
        return classes;
    }

    private static String assembleNameFrom(String packageName, String fileName) {
        if (packageName == null) {
            return fileName;
        }
        return packageName + fileName;
    }
}
