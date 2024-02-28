package xyz.oribuin.playerwarps.manager;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.playerwarps.PlayerWarpsPlugin;

import java.util.List;

public class ConfigurationManager extends AbstractConfigurationManager {

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[]{};
    }

    public enum Setting implements RoseSetting {
        DISABLED_WORLDS("disabled-worlds", List.of("disabled-world-1"), "Here you can configure the worlds that warps are disabled in."),

        WARP_SETTINGS("warp-settings", null, "Here you can configure the settings for warps."),
        WARP_CREATE_COST("warp-settings.create-cost", 500.0, "The cost to create a warp.", "Uses vault as the economy provider."),
        WARP_DELETE_COST("warp-settings.delete-cost", 500.0, "The cost to delete a warp.", "Uses vault as the economy provider."),
        WARP_DESC_FILTERS("warp-settings.desc-filters", List.of("\\b(f+(\\W|\\d|_)*(a|@)+(\\W|\\d|_)*g+(\\W|\\d|_)*)\\b"), "If any of these patterns are matched, the warp description will be invalid."),
        WARP_NAME_FILTERS("warp-settings.name-filters", List.of("\\b(f+(\\W|\\d|_)*(a|@)+(\\W|\\d|_)*g+(\\W|\\d|_)*)\\b"), "If any of these patterns are matched, the warp name will be invalid."),

        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return PlayerWarpsPlugin.get().getManager(ConfigurationManager.class).getConfig();
        }
    }
}
