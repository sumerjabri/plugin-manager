package org.craftercms.plugin;

/**
 * Plugin Interface
 */
public interface Plugin {

    // If PluginException is thrown , disable org.craftercms.plugin.Plugin (manager will do this)
     void init(Context context) throws PluginException;
     void activate();
     void deactivate();
     void destroy();
}
