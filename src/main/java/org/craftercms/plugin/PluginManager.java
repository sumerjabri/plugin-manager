package org.craftercms.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin Manager.
 */
public final class PluginManager {

    /**
     * Plugin Info Map.
     */
    private Map<String, PluginInfo> registerPlugins;
    /**
     * Register Context for given pluginTypes.
     */
    private Map<String, Context> typeContexts;
    private Logger log = LoggerFactory.getLogger(PluginManager.class);

    public PluginManager(final String pluginFolder) {
        this.typeContexts = new HashMap<String, Context>();
        this.registerPlugins = new HashMap<String, PluginInfo>();
        load();
    }

    /**
     * Get's and Cast Plugin to de given Class instance.
     * @param pluginId Plugin id to get
     * @param clazz Class to be Cast
     * @param <T> Class to be wanted.
     * @return A instance of the given class.
     * @throws PluginException if Plugin can't be cast to it
     */
    public <T> T get(final String pluginId, final Class<T> clazz) throws PluginException {
        try {
            return clazz.cast(getPluginById(pluginId));
        } catch (ClassCastException ex) {
            this.log.error("Unable cast ", ex);
            throw new PluginException("Unable to convert Plugin in to given class");
        }
    }

    /**
     * Gets PluginInfo for all plugins of the given type.
     *
     * @param pluginType PluginType to get Metadata
     * @return A list of PluginInfo for the given type <b>Or</b> empty list
     *         if there are not plugins of the given type
     */
    public List<PluginInfo> getPluginsInfo(String pluginType) {
        return null;
    }

    /**
     * Register a Context for the Given plugin type.
     * A context will be to the plugin to call {@link Plugin#init(Context)}
     * <b>Each plugin type will have a different context</b>
     *
     * @param type    Plugin Type.
     * @param context Context impl.
     */
    public void registerContext(final String type, final Context context) {
        this.typeContexts.put(type, context);
    }

    /**
     * Gets a plugin by its name.
     *
     * @param pluginId Plugin Id
     * @return A plugin instance <b>or</b> Null if there not a plugin with the given id.
     */
    private Plugin getPluginById(String pluginId) {
        //TODO Add some cache
        final PluginInfo info = this.registerPlugins.get(pluginId);
        Plugin plugin = null;
        try {
            plugin = (Plugin) info.getClazz().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassCastException ex) {
            this.log.error("Unable to get plugin by due a exception", ex);
            this.log.error("This is most likely to be due bad "
                    + "configuration on manifest check that {} is a impl of Plugin class", info.getClazz()
                    .getCanonicalName());
            //Todo Handel this ex
        }
        return plugin;
    }

    public PluginInfo getPluginInfo(String pluginType) {
        return null;
    }

    /**
     * Enables A plugin, If plugin init throws exception
     * this plugin will be automatically disable.
     *
     * @param pluginId Plugin Id
     */
    public void enable(String pluginId) {
        // plugin is Register
        if ( this.registerPlugins.containsKey(pluginId) ) {
            final PluginInfo pluginInfo = this.registerPlugins.get(pluginId);
            if ( pluginInfo.getState() == PluginState.DISABLED ) {
                try {
                    getPluginById(pluginId).init(this.typeContexts.get(pluginInfo.getType()));
                } catch (PluginException ex) {
                    this.log.error("Unable to enable this plugin due exception, disabling plugin", ex);
                    disable(pluginId);
                }
            }
        }

    }

    /**
     * Disables a plugin.
     * @param pluginId PluginId to disable
     */
    private void disable(String pluginId) {
        getPluginInfo(pluginId).setState(PluginState.DISABLED);
        //TODO Unload Stuff
        //TODO Save to XML FILE
    }

    /**
     * Gets All active Plugins.
     * @return List of All Plugins in  PluginState.ENABLED_ACTIVE
     */
    public List<PluginInfo> getActivePlugins() {
        return getPluginsByState(PluginState.ENABLED_ACTIVE);
    }

    public List<PluginInfo> getInactivePlugins() {
        return getPluginsByState(PluginState.ENABLED_INACTIVE);
    }

    public List<PluginInfo> getEnabledPlugin() {
        return getPluginsByState(PluginState.ENABLED_ACTIVE);
    }

    public List<PluginInfo> getDisabledPlugin() {
        return getPluginsByState(PluginState.DISABLED);
    }

    public List<PluginInfo> getPluginsByState(PluginState state) {
        final List<PluginInfo> activePlugins = new ArrayList<>();
        for (String key : this.registerPlugins.keySet()) {
            final PluginInfo info = this.registerPlugins.get(key);
            if ( info.getState() == state ) {
                activePlugins.add(info);
            }
        }
        return Collections.unmodifiableList(activePlugins);
    }

    private void load() {
        // Loads Plugins from Paths
        // Fill registerPlugins with ManifestInfo
        // Open INFO XML.
        // Update registerPlugins with states
        // Update INFO XML (in case o new files)
    }

    private String getPluginType(final Plugin plugin) {
        return "";
    }


}
