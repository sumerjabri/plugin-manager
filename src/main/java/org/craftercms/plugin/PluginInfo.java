package org.craftercms.plugin;

/**
 * Plugin Information.
 * Reflects Manifest.mf Information.
 */
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

    public PluginInfo() {
    }

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
}
