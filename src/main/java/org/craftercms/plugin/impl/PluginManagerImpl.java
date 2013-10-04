package org.craftercms.plugin.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import static java.nio.file.StandardCopyOption.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.craftercms.plugin.Context;
import org.craftercms.plugin.Plugin;
import org.craftercms.plugin.PluginException;
import org.craftercms.plugin.PluginInfo;
import org.craftercms.plugin.PluginManager;
import org.craftercms.plugin.PluginRegistry;


import org.craftercms.plugin.constants.ManifestFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin Manager.
 */
public final class PluginManagerImpl implements PluginManager {

    // <editor-fold defaultstate="collapsed" desc="Constants">

    private static final String TEMP_FILE_SUFFIX = "tmp";

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attributes">

    private Map<String, PluginInfo> pluginInfoRegister;

    private Map<String, Plugin> pluginRegister;

    private Map<String, Context> contextRegistry;

    private Logger log = LoggerFactory.getLogger(PluginManagerImpl.class);

    private String pluginsXMLPath;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public PluginManagerImpl(final String... propertiesPath) {
        loadPropertiesValues(propertiesPath);
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="API">

    @Override
    public void init(final List<String> pluginFolders, final Map<String, Context> contextRegistry) throws
        PluginException {
        pluginInfoRegister = load();
        loadFolders(pluginFolders);
        this.contextRegistry = contextRegistry;
    }

    @Override
    public void destroy() throws PluginException {
        save(pluginInfoRegister);
        for (String pluginId : pluginRegister.keySet()) {
            Plugin plugin = pluginRegister.get(pluginId);
            plugin.destroy();
        }
    }

    @Override
    public void registerContext(final String type, final Context context) throws PluginException {
        this.contextRegistry.put(type, context);
    }

    @Override
    public void installPlugin(final InputStream plugin) throws PluginException {
        try {
            JarInputStream jarInputStream = new JarInputStream(plugin);
            PluginInfo pluginInfo = pluginInfoFromManifest(jarInputStream.getManifest());
            pluginInfo.setState(PluginState.ENABLED_INACTIVE);
            Plugin pluginInstance = (Plugin)pluginInfo.getClazz().newInstance();
            pluginInstance.init(contextRegistry.get(pluginInfo.getType()));
            pluginRegister.put(pluginInfo.getId(), pluginInstance);
        } catch (IOException ioE) {
            //TODO: log
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException ex) {
            //TODO: log
            // No es dele tipo cast
        }
    }

    @Override
    public void uninstallPlugin(final String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginInfoRegister.remove(pluginId);
        Plugin plugin = pluginRegister.get(pluginId);
        if (pluginInfo != null && plugin != null) {
            plugin.destroy();
        } else {
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
        //TODO: Check to save changes or do it at the end.
    }

    @Override
    public void enablePlugin(final String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);

        if (pluginInfo != null) {
            pluginInfo.setState(PluginState.ENABLED_INACTIVE);
            Plugin plugin = pluginRegister.get(pluginId);
            if (plugin == null) {
                try {
                    plugin = (Plugin) pluginInfo.getClazz().newInstance();
                    plugin.init(contextRegistry.get(pluginInfo.getType()));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException ex) {
                    // No es dele tipo cast
                }
            }
            pluginRegister.put(pluginId, plugin);
        } else {
            log.error("Plugin with ID: " + pluginId + " doesn't exist.");
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
    }

    @Override
    public void disablePlugin(final String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
        Plugin plugin = pluginRegister.get(pluginId);

        if (pluginInfo != null && plugin != null) {
            pluginInfo.setState(PluginState.DISABLED);
            plugin.destroy();
            pluginInfoRegister.remove(pluginId);
        } else {
            log.error("Plugin with ID: " + pluginId + " doesn't exist.");
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
    }

    @Override
    public void activatePlugin(final String pluginId) throws PluginException {
        //TODO Check rules if active has to be enable
        PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
        Plugin plugin = pluginRegister.get(pluginId);

        if (pluginInfo != null) {
            pluginInfo.setState(PluginState.ENABLED_ACTIVE);
            plugin.activate();
        } else {
            log.error("Plugin with ID: " + pluginId + " doesn't exist.");
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
    }

    @Override
    public void deactivatePlugin(final String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);

        if (pluginInfo != null) {
            pluginInfo.setState(PluginState.ENABLED_INACTIVE);
            Plugin plugin = getPlugin(pluginInfo.getId(), pluginInfo.getClazz());
            plugin.deactivate();
        } else {
            log.error("Plugin with ID: " + pluginId + " doesn't exist.");
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
    }

    @Override
    public PluginInfo getPluginInfo(final String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);

        if (pluginInfo != null) {
            return pluginInfo;
        } else {
            log.error("Plugin with ID: " + pluginId + " doesn't exist.");
            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
        }
    }

    @Override
    public List<PluginInfo> listAllPlugins() throws PluginException {
        List<PluginInfo> plugins = new ArrayList<>();

        for (String pluginId : pluginInfoRegister.keySet()) {
            PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
            plugins.add(pluginInfo);
        }

        return plugins;
    }

    @Override
    public List<PluginInfo> listPluginsByType(final String pluginType) throws PluginException {
        List<PluginInfo> plugins = new ArrayList<>();

        for (String pluginId : pluginInfoRegister.keySet()) {
            PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
            if (pluginInfo.getType().equals(pluginType)) {
                plugins.add(pluginInfo);
            }
        }

        return plugins;
    }

    @Override
    public List<PluginInfo> listPluginsByState(final PluginState state) throws PluginException {
        List<PluginInfo> plugins = new ArrayList<>();

        for (String pluginId : pluginInfoRegister.keySet()) {
            PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
            if (pluginInfo.getState().equals(state)) {
                plugins.add(pluginInfo);
            }
        }

        return plugins;
    }

    @Override
    public List<PluginInfo> listPluginsByTypeByState(final String pluginType, final PluginState state) throws
        PluginException {
        List<PluginInfo> plugins = new ArrayList<>();

        for (String pluginId : pluginInfoRegister.keySet()) {
            PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
            if (pluginInfo.getType().equals(pluginType) && pluginInfo.getState().equals(state)) {
                plugins.add(pluginInfo);
            }
        }

        return plugins;
    }

    @Override
    public <T extends Plugin> T getPlugin(final String pluginId, final Class<T> clazz) throws PluginException {

        Plugin plugin = pluginRegister.get(pluginId);

        if (plugin == null) {
            PluginInfo pluginInfo = pluginInfoRegister.get(pluginId);
            if (pluginInfo != null) {
                try {
                    plugin = (Plugin) pluginInfo.getClazz().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                log.error("Plugin with ID: " + pluginId + " doesn't exist.");
                throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
            }
        }

        return clazz.cast(plugin);

    }

    // </editor-fold>editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Internal Functionality">

    private void save(Map<String, PluginInfo> pluginList) {

        try {
            File tempfile = File.createTempFile(this.pluginsXMLPath, TEMP_FILE_SUFFIX);//new File("TestRegistry.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(PluginRegistry.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            PluginRegistry pluginRegistry = new PluginRegistry(pluginList);

            jaxbMarshaller.marshal(pluginRegistry, tempfile);
            File file = new File(this.pluginsXMLPath);
            Files.move(file.toPath(), file.toPath(), REPLACE_EXISTING);
        } catch (IOException ex) {
            //Todo: Log
        } catch (JAXBException jbe) {
            log.error("Error converting saving xml file");
            jbe.printStackTrace();
        }

    }

    private Map<String, PluginInfo> load() {

        PluginRegistry pluginRegistry = null;

        try {

            File file = new File(this.pluginsXMLPath);
            JAXBContext jaxbContext = JAXBContext.newInstance(PluginRegistry.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            pluginRegistry = (PluginRegistry)jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException jbe) {
            log.error("Error converting loading xml file");
            jbe.printStackTrace();
        }

        return pluginRegistry.getPluginRegistry();
    }

    private void loadFolders(final List<String> pluginFolders) throws PluginException {
        try {
            for (String folder : pluginFolders) {
                File f = new File(folder);
                String[] carFiles = f.list(new FilenameFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".car");
                    }
                });

                for (String carFilePath : carFiles) {
                    JarFile carFile = new JarFile(carFilePath);
                    PluginInfo pluginInfo = pluginInfoFromManifest(carFile.getManifest());
                    pluginInfo.setFolder(folder);
                    pluginInfo.setId("TO GENERATe ID");
                    if (this.pluginRegister.containsKey(pluginInfo.getId())) {
                        Plugin pluginInstance = (Plugin) pluginInfo.getClazz().newInstance();
                        if (pluginInfo.getState() == PluginState.ENABLED_INACTIVE) {
                            pluginInstance.init(this.contextRegistry.get(pluginInfo.getType()));
                            pluginInstance.deactivate();
                        }
                        if (pluginInfo.getState() == PluginState.ENABLED_ACTIVE) {
                            pluginInstance.init(this.contextRegistry.get(pluginInfo.getType()));
                            pluginInstance.activate();
                        }
                    }

                }
            }
        } catch (IOException ioE) {
            log.error("Could not load Jar Files");
            throw new PluginException("Could not load Jar Files");
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void loadPropertiesValues(final String... propertiesPath) {

        Properties properties = new Properties();

        try {
            for (String propertyFile : propertiesPath) {
                properties.load(PluginManagerImpl.class.getClassLoader().getResourceAsStream(propertyFile));
            }
            this.pluginsXMLPath = properties.getProperty(this.pluginsXMLPath);
        } catch (IOException e) {
            log.debug("Error processing the properties map");
        } catch (NullPointerException e) {
            log.debug("Error loading the properties file");
        }

    }

    private PluginInfo pluginInfoFromManifest(final Manifest carFileManifest) throws PluginException {
        PluginInfo pluginInfo = null;

        try {
            pluginInfo = new PluginInfo();

            Attributes attributes = carFileManifest.getMainAttributes();

            pluginInfo.setName(attributes.getValue(ManifestFields.PLUGIN_NAME));
            pluginInfo.setVersion(attributes.getValue(ManifestFields.PLUGIN_VERSION));
            pluginInfo.setDevelopers(new String[] {attributes.getValue(ManifestFields.PLUGIN_DEVELOPER)});
            pluginInfo.setUrl(attributes.getValue(ManifestFields.PLUGIN_URL));
            pluginInfo.setLicense(attributes.getValue(ManifestFields.PLUGIN_LICENSE));
            pluginInfo.setLicenseUrl(attributes.getValue(ManifestFields.PLUGIN_LICENSE_URL));
            pluginInfo.setCost(attributes.getValue(ManifestFields.PLUGIN_COST));
            pluginInfo.setType(attributes.getValue(ManifestFields.PLUGIN_TYPE));
            pluginInfo.setCompatibility(attributes.getValue(ManifestFields.PLUGIN_COMPATIBILITY));
            pluginInfo.setState(PluginManager.PluginState.DISABLED);
            pluginInfo.setClazz(Class.forName(attributes.getValue(ManifestFields.PLUGIN_CLASS_NAME)));

        } catch (ClassNotFoundException cnfe) {
            log.error("Class not found");
            throw new PluginException("Class not found");
        }
        return pluginInfo;
    }

    // </editor-fold>editor-fold>
}
