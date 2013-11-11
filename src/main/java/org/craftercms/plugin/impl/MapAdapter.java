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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.craftercms.plugin.PluginManager.PluginState;

/**
 * @author David Escalante López
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, PluginState>> {

    @Override
    public Map<String, PluginState> unmarshal(final MapAdapter.AdaptedMap adaptedMap) throws Exception {
        Map<String, PluginState> map = new HashMap<>();
        for (Entry entry : adaptedMap.entry) {
            map.put(entry.key, entry.value);
        }
        return map;
    }

    @Override
    public AdaptedMap marshal(final Map<String, PluginState> map) throws Exception {
        AdaptedMap adaptedMap = new AdaptedMap();
        for (Map.Entry<String, PluginState> mapEntry : map.entrySet()) {
            Entry entry = new Entry();
            entry.key = mapEntry.getKey();
            entry.value = mapEntry.getValue();
            adaptedMap.entry.add(entry);
        }
        return adaptedMap;
    }

    public static class AdaptedMap {

        @XmlElement(name = "plugin")
        public List<Entry> entry = new ArrayList<>();

    }

    public static class Entry {

        @XmlElement(name = "plugin-id")
        public String key;

        @XmlElement(name = "plugin-state")
        public PluginState value;

    }
}
