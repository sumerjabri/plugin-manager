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
package org.craftercms.plugin.impl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.craftercms.plugin.PluginManager.PluginState;

/**
 * @author David Escalante López
 */
@XmlRootElement(name = "plugin-registry")
public class PluginRegistry {

    // <editor-fold defaultstate="collapsed" desc="Attributes">

    private Map<String, PluginState> pluginRegistry;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">


    public PluginRegistry() {
        pluginRegistry = new HashMap<>();
    }

    public PluginRegistry(final Map<String, PluginState> pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="API">

    public void add(final String pluginId, final PluginState pluginInfo) {
        pluginRegistry.put(pluginId, pluginInfo);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters and Getters">

    @XmlJavaTypeAdapter(MapAdapter.class)
    @XmlElement(name="plugins")
    public Map<String, PluginState> getPluginRegistry() {
        return pluginRegistry;
    }

    public void setPluginRegistry(final Map<String, PluginState> pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }


    // </editor-fold>
}
