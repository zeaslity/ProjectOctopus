package io.wdd.agent.executor.config;

import java.io.File;
import java.net.URISyntaxException;

public class GetClassPath {

    public static final File getFileForClass(Class<?> clazz) {
        File file;

        try {
            file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    public static final String getBasePathForClass(Class<?> clazz) {
        File file;
        try {
            String basePath = null;
            file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());


            if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
                basePath = file.getParent();
            } else {
                basePath = file.getPath();
            }

            // fix to run inside eclipse
            if (basePath.endsWith(File.separator + "lib") || basePath.endsWith(File.separator + "bin")
                    || basePath.endsWith("bin" + File.separator) || basePath.endsWith("lib" + File.separator)) {
                basePath = basePath.substring(0, basePath.length() - 4);
            }
            // fix to run inside netbean
            if (basePath.endsWith(File.separator + "build" + File.separator + "classes")) {
                basePath = basePath.substring(0, basePath.length() - 14);
            }
            // end fix
            if (!basePath.endsWith(File.separator)) {
                basePath = basePath + File.separator;
            }
            return basePath;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot firgue out base path for class: " + clazz.getName());
        }
    }
}
