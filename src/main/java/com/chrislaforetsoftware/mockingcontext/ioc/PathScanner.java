// This code is based on Martin Häusler's articles on DEV entitled "Understanding Dependency Injection."
// Originally licensed under Apache License 2.0

package com.chrislaforetsoftware.mockingcontext.ioc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PathScanner {

    public static Set<Class<?>> getAllClassesInPackage(String packageName) throws Exception {
        final Enumeration<URL> resources = getPackageTree(packageName);
        final List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }

        final Set<Class<?>> classes = new HashSet<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static Enumeration<URL> getPackageTree(String packageName) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String path = packageName == null ?
                "" :
                packageName.replace('.', '/');
        return classLoader.getResources(path);
    }

    public static Set<String> getAllPackages(String rootPackage) throws Exception {
        final Enumeration<URL> resources = getPackageTree(rootPackage);
        final Set<String> packages = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            packages.addAll(findPackages(new File(resource.getFile()), rootPackage));
        }
        return packages;
    }

    private static Set<String> findPackages(File directory, String packageName) {
        final Set<String> packages = new HashSet<>();

        if (!directory.exists()) {
            return packages;
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            return packages;
        }
        boolean isPackageLogged = false;
        for (File file : files) {
            if (file.isDirectory()) {
                packages.addAll(findPackages(file,  assembleNameFrom(packageName, file.getName())));
            } else if (!isPackageLogged && file.getName().endsWith(".class")) {
                packages.add(packageName);
                isPackageLogged = true;
            }
        }
        return packages;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
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
        return packageName + "." + fileName;
    }
}
