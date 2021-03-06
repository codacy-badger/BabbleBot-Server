package uk.co.bjdavies.plugins;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import uk.co.bjdavies.api.IApplication;
import uk.co.bjdavies.api.plugins.ICustomPluginConfig;
import uk.co.bjdavies.api.plugins.IPlugin;
import uk.co.bjdavies.api.plugins.PluginConfig;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.2.7
 */
@Slf4j
public class PluginConfigParser {

    public static void parsePlugin(IApplication application, IPlugin settings, Object plugin) {
        Field[] fields = plugin.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (field.isAnnotationPresent(PluginConfig.class)) {
                PluginConfig pluginConfig = field.getAnnotationsByType(PluginConfig.class)[0];
                Class<?> clazz;
                if (pluginConfig.value() != ICustomPluginConfig.class) {
                    clazz = pluginConfig.value();
                } else {
                    clazz = field.getType();
                }

                log.info("Parsing Config class: " + clazz.getSimpleName());

                String fileName = "";
                boolean autoGenerate = true;

                if (clazz.isAnnotationPresent(PluginConfig.Setup.class)) {
                    //Then handle a setup class
                    PluginConfig.Setup setup = clazz.getAnnotationsByType(PluginConfig.Setup.class)[0];
                    fileName = setup.fileName().equals("") ? settings.getName() : setup.fileName();
                    autoGenerate = setup.autoGenerate();
                } else {
                    //Handle a interface
                    try {
                        ICustomPluginConfig customPluginConfig = (ICustomPluginConfig) application.get(clazz);
                        autoGenerate = customPluginConfig.autoGenerateConfig();
                        fileName = customPluginConfig.fileName();
                    } catch (ClassCastException exception) {
                        log.error("Your config field does meet the type requirements it must either use PluginConfig.Setup or implement ICustomPluginConfig...", exception);
                        return;
                    }

                }

                //noinspection ResultOfMethodCallIgnored
                new File("config").mkdirs();
                File file = new File("config/" + fileName + ".config.json");
                if (!file.exists()) {
                    try {
                        if (file.createNewFile()) {
                            if (autoGenerate) {
                                FileWriter writer = new FileWriter(file);
                                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(application.get(clazz)));
                                writer.flush();
                                writer.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    FileReader reader = new FileReader(file);

                    BufferedReader bufferedReader = new BufferedReader(reader);

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }


                    field.setAccessible(true);

                    field.set(plugin, new GsonBuilder().create().fromJson(stringBuilder.toString(), clazz));
                } catch (IOException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
