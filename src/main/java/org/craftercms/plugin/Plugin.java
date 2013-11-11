/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.plugin;

import org.craftercms.plugin.exception.PluginManagerException;

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
     * @throws org.craftercms.plugin.exception.PluginManagerException
     */
    void init(Context context) throws PluginManagerException;

    /**
     * Activate an already initialized plugin. Start the plugin here.
     *
     * @throws org.craftercms.plugin.exception.PluginManagerException
     */
    void activate() throws PluginManagerException;

    /**
     * Deactivate the plugin. Stop the plugin here, but expect the possibility of being started again.
     *
     * @throws org.craftercms.plugin.exception.PluginManagerException
     */
    void deactivate() throws PluginManagerException;

    /**
     * Destroy the plugin. Free up any resources allocated during Init and shutdown the plugin. Init will be invoked
     * if the plugin is required again.
     *
     * @throws org.craftercms.plugin.exception.PluginManagerException
     */
    void destroy() throws PluginManagerException;
}
