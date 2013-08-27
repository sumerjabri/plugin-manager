//package org.craftercms.plugin.impl;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.craftercms.plugin.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Plugin Manager.
// */
//public final class PluginManagerImpl implements PluginManager {
//    /**
//     * Plugin registry containing info for all registered plugins
//     */
//    private Map<String, PluginInfo> pluginRegistry;
//    /**
//     * API Contexts for various plugin types
//     */
//    private Map<String, Context> typeContexts;
//    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManagerImpl.class);
//
//    /**
//     * Construct a plugin manager
//     *
//     * @param pluginFolders   folders that contain plugins
//     * @param contextRegistry API contexts registry for various plugin types
//     */
//    public PluginManagerImpl(List<String> pluginFolders, Map<String, Context> contextRegistry) {
//        this.typeContexts = new HashMap<String, Context>();
//        this.pluginRegistry = new HashMap<String, PluginInfo>();
//
//        // register the listener to the plugin folders so we can load new plugins/remove plugins in realtime
//        // and load all plugins present at this time
//        //loadPlugins();
//    }
//
//    /**
//     * @inheritDoc
//     */
//    public <T extends Plugin> T get(final String pluginId, final Class<T> clazz) throws PluginException {
//        Object plugin = pluginRegistry.get(pluginId);
//
//        if (plugin != null) {
//            try {
//                return clazz.cast(plugin);
//            } catch (ClassCastException ex) {
//                this.LOGGER.error("Unable cast " + plugin + " to " + clazz.toString() + " ", ex);
//                throw new PluginException("Unable to convert Plugin in to given class");
//            }
//        } else {
//            this.LOGGER.error("Plugin with ID: " + pluginId + " doesn't exist.");
//            throw new PluginException("Plugin with ID: " + pluginId + " doesn't exist.");
//        }
//    }
//}
