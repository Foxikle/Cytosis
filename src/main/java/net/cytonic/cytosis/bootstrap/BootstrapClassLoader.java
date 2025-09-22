package net.cytonic.cytosis.bootstrap;

import java.net.URL;
import java.net.URLClassLoader;

import org.jetbrains.annotations.Contract;

/**
 * The class loader used by the {@link Bootstrapper} to load Cytosis classes & dependencies
 */
public class BootstrapClassLoader extends URLClassLoader {

    /**
     * The default constructor. Uses this class' loader as the parent
     */
    public BootstrapClassLoader() {
        super("Bootstrap Class Loader", new URL[]{}, BootstrapClassLoader.class.getClassLoader());
    }

    @Override
    @Contract(pure = true)
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.") || name.startsWith("javax.")) {
            return super.loadClass(name, resolve); // delegate to bootstrap for core classes
        }

        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name); // load from our JARs
                } catch (ClassNotFoundException e) {
                    c = super.loadClass(name, resolve);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    /**
     * Adds a URL to this loader, therefore loading the classes contained and depended on
     *
     * @param url the URL to be added to the search path of URLs
     */
    @Override
    public void addURL(final URL url) {
        super.addURL(url);
    }
}
