package org.craftercms.plugin;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Sumer Jabri
 */
public interface PluginManager {
    public enum PluginState {
        /**
         * Enabled and Active
         */
        ENABLED_ACTIVE,
        /**
         * Enabled and Inactive
         */
        ENABLED_INACTIVE,
        /**
         * Disabled
         */
        DISABLED
    }

    /**
     * Initialize the plugin manager
     *
     * @param pluginFolders   folders that contain plugins
     * @param contextRegistry API contexts registry for various plugin types
     * @throws PluginException
     */
    public void init(List<String> pluginFolders, Map<String, Context> contextRegistry) throws PluginException;

    /**
     * Destroy the plugin manager
     *
     * @throws PluginException
     */
    public void destroy() throws PluginException;

    /**
     * Register a Context for the Given plugin type.
     * A context will be to the plugin to call {@link Plugin#init(Context)}
     * <b>Each plugin type will probably have a different context</b>
     *
     * @param type    Plugin Type.
     * @param context Context impl.
     * @throws PluginException
     */
    public void registerContext(final String type, final Context context) throws PluginException;

    /**
     * Install a plugin on disk and load it as disabled
     *
     * @param plugin input stream of the plugin binary
     * @throws PluginException
     */
    public void installPlugin(final InputStream plugin) throws PluginException;

    /**
     * Deactivate, destroy and delete a plugin from disk.
     *
     * @param pluginId ID of the plugin to remove
     * @throws PluginException
     */
    public void uninstallPlugin(final String pluginId) throws PluginException;

    /**
     * Load a plugin from disk if not already loaded and initialize it by calling its init method, upon success
     * set the state of the plugin to enabled
     * If the init method of the plugin fails, the plugin is marked as disabled
     *
     * @param pluginId plugin Id
     * @throws PluginException
     */
    public void enablePlugin(String pluginId) throws PluginException;

    /**
     * Call the destroy method of the plugin and mark it as disabled
     *
     * @param pluginId
     * @throws PluginException
     */
    public void disablePlugin(String pluginId) throws PluginException;

    /**
     * Call the activate method of the plugin and mark it as active
     *
     * @param pluginId
     * @throws PluginException
     */
    public void activatePlugin(String pluginId) throws PluginException;

    /**
     * Call the deactivate method of the plugin and mark it as inactive
     *
     * @param pluginId
     * @throws PluginException
     */
    public void deactivatePlugin(String pluginId) throws PluginException;

    /**
     * Return the plugin information
     * @param pluginId Id of the plugin
     * @return plugin information of type {@link PluginInfo}
     * @throws PluginException
     */
    public PluginInfo getPluginInfo(String pluginId) throws PluginException;

    /**
     * List all plugins in the system
     * @return list of all plugins' {@link PluginInfo}
     * @throws PluginException
     */
    public List<PluginInfo> listAllPlugins() throws PluginException;

    /**
     * List all plugins of a certain type
     * @param pluginType type of plugin
     * @return list of {@link PluginInfo}
     * @throws PluginException
     */
    public List<PluginInfo> listPluginsByType(final String pluginType) throws PluginException;

    /**
     * List all plugins by plugin state
     * @param state plugin state, see {@link PluginManager.PluginState}
     * @return list of {@link PluginInfo}
     * @throws PluginException
     */
    public List<PluginInfo> listPluginsByState(final PluginState state) throws PluginException;

    /**
     * List plugins given a type and state
     * @param pluginType plugin type
     * @param state plugin state
     * @return list of {@link PluginInfo}
     * @throws PluginException
     */
    public List<PluginInfo> listPluginsByTypeByState(final String pluginType, final PluginState state)
            throws PluginException;


    /**
     * Get a plugin instance cast to the class of the plugin
     *
     * @param pluginId Plugin id to get
     * @param clazz    Class to be Cast
     * @param <T>      Class to be wanted.
     * @return A instance of the given class that implements {@link Plugin}
     * @throws PluginException
     */
    public <T extends Plugin> T getPlugin(final String pluginId, final Class<T> clazz) throws PluginException;
}
