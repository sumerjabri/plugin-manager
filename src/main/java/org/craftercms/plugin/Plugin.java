package org.craftercms.plugin;

/**
 * Plugin Interface.
 * <p/>
 * Implement this interface when developing a plugin that can be loaded and managed by {@link PluginManager}.
 * The plugin will then be loaded and provided with an API wrapper in a {@link Context} that provides the plugin
 * with access to the hosting system.
 * <p/>
 * Life cycle methods are:
 * Init, Activate, Deactivate and Destroy.
 * <p/>
 * Init is reversed by Destroy and Activate is reversed by Deactivate.
 */
public interface Plugin {
    /**
     * Initialize the plugin and provide it an API. Don't start the plugin here, just initialize it and allocate
     * required resources.
     *
     * @param context the API provided to this type of plugin
     * @throws PluginException
     */
    void init(Context context) throws PluginException;

    /**
     * Activate an already initialized plugin. Start the plugin here.
     *
     * @throws PluginException
     */
    void activate() throws PluginException;

    /**
     * Deactivate the plugin. Stop the plugin here, but expect the possibility of being started again.
     *
     * @throws PluginException
     */
    void deactivate() throws PluginException;

    /**
     * Destroy the plugin. Free up any resources allocated during Init and shutdown the plugin. Init will be invoked
     * if the plugin is required again.
     *
     * @throws PluginException
     */
    void destroy() throws PluginException;
}
