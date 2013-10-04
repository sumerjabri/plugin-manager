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

import java.util.jar.Attributes;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Plugin Information.
 * Reflects Manifest.mf Information.
 */
@XmlRootElement(name = "plugin-info")
public class PluginInfo {
    private String id;
    private String name;
    private String version;
    private String[] developers;
    private String url;
    private String license;
    private String licenseUrl;
    private String cost;
    private String type;
    private String compatibility;
    private PluginManager.PluginState state;
    private Class clazz;
    private String folder;
    public PluginInfo() {
    }

//    public PluginInfo(Attributes attributes) {
//    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String[] getDevelopers() {
        return this.developers;
    }

    public void setDevelopers(final String[] developers) {
        this.developers = developers;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getLicense() {
        return this.license;
    }

    public void setLicense(final String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return this.licenseUrl;
    }

    public void setLicenseUrl(final String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getCost() {
        return this.cost;
    }

    public void setCost(final String cost) {
        this.cost = cost;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getCompatibility() {
        return this.compatibility;
    }

    public void setCompatibility(final String compatibility) {
        this.compatibility = compatibility;
    }

    public PluginManager.PluginState getState() {
        return this.state;
    }

    public void setState(final PluginManager.PluginState state) {
        this.state = state;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public void setClazz(final Class clazz) {
        this.clazz = clazz;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(final String folder) {
        this.folder = folder;
    }
}
