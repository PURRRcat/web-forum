package ru.forum;

import org.apache.catalina.startup.Tomcat;
import java.io.File;

/**
 * Точка входа для запуска форума на встроенном Tomcat.
 * Используется задачей Gradle {@code appRun}.
 * Путь к WAR передаётся через системное свойство {@code war.path}.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String warPath = System.getProperty("war.path", "build/libs/forum.war");
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir") + "/tomcat-forum");
        tomcat.getConnector(); // инициализирует коннектор по умолчанию

        tomcat.addWebapp("/forum", new File(warPath).getAbsolutePath());

        tomcat.start();
        System.out.println("Форум запущен: http://localhost:" + port + "/forum");
        tomcat.getServer().await();
    }
}
