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
package org.craftercms.plugin.service.api;

import java.util.List;

import org.craftercms.plugin.PluginManager.PluginState;

/**
 * @author David Escalante López
 */
public interface PluginStatusService {

    boolean addOne(String pluginId, PluginState state);

    boolean addList(List<String> pluginList, PluginState state);

    PluginState getStatusById(String pluginId);

    List<String> getAllByStatus(PluginState state);

    List<String> getAll();

    boolean updateOne(String pluginId, PluginState state);

    boolean updateList(List<String> pluginList, PluginState state);

    boolean updateAll(PluginState state);

    boolean delete(String pluginId);
}
