package xyz.oribuin.playerwarps.manager


import dev.rosewood.rosegarden.RosePlugin
import dev.rosewood.rosegarden.config.CommentedFileConfiguration
import dev.rosewood.rosegarden.config.RoseSetting
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager
import org.bukkit.Material
import xyz.oribuin.playerwarps.PlayerWarpsPlugin
import xyz.oribuin.playerwarps.util.WarpUtils.getManager

class ConfigurationManager(rosePlugin: RosePlugin) : AbstractConfigurationManager(rosePlugin, Setting::class.java) {

    enum class Setting(private val key: String?, private val defaultValue: Any?, private vararg val comments: String?) :
        RoseSetting {
        // General Warp Settings
        DISABLED_WORLDS("disabled-worlds", emptyList<String>(), "A list of worlds where warps are disabled."),
        DISABLED_ICONS("disabled-icons", listOf(Material.BEDROCK.name), "A list of icons that are disabled."),
        MIN_WARP_LIMIT("min-warp-limit", 1, "The minimum amount of warps a player can have."),
        DATE_FORMAT("date-format", "dd/MMMM", "What format the date should be displayed in."),

        // Command Cooldown Settings
        COOLDOWNS("cooldowns", null, "The cooldowns for each command.", "Set each option as 0 to disable."),
        COOLDOWNS_CREATE("cooldowns.create", 60, "The cooldown (in seconds) for creating a warp."),
        COOLDOWNS_DELETE("cooldowns.delete", 120, "The cooldown (in seconds) for deleting a warp."),
        COOLDOWNS_TELEPORT("cooldowns.teleport", 10, "The cooldown (in seconds) for teleporting to a warp."),
        COOLDOWNS_RENAME("cooldowns.rename", 60, "The cooldown (in seconds) for renaming a warp."),

        // Warp Pricing Settings
        ECONOMY_PLUGIN("economy.provider", "VAULT", "The primary economy plugin to use for all economy related features.", "Options: VAULT, PLAYERPOINTS, TOKENMANAGER, TREASURY"),
        CREATE_WARP_PRICE("economy.create", 1000.0, "The cost for creating a warp."),
        DELETE_WARP_PRICE("economy.delete", 1000.0, "The cost for deleting a warp."),
        RENAME_WARP_PRICE("economy.rename", 1000.0, "The cost for renaming a warp."),
        MIN_TELEPORT_PRICE("economy.min-teleport", 5.0, "The minimum cost a player can set for teleporting to their warp."),
        MAX_TELEPORT_PRICE("economy.max-teleport", 100.0, "The maximum cost a player can set for teleporting to their warp."),

        //
        ; // TODO: Add settings

        private var value: Any? = null

        override fun getKey() = this.key

        override fun getDefaultValue() = this.defaultValue

        override fun getComments() = this.comments

        override fun getCachedValue() = this.value

        override fun setCachedValue(value: Any?) {
            this.value = value
        }

        override fun getBaseConfig(): CommentedFileConfiguration =
            PlayerWarpsPlugin.instance.getManager<ConfigurationManager>().config

    }

    //__________.__                             __      __
    //\______   \  | _____  ___.__. ___________/  \    /  \_____ _____________  ______
    // |     ___/  | \__  \<   |  |/ __ \_  __ \   \/\/   /\__  \\_  __ \____ \/  ___/
    // |    |   |  |__/ __ \\___  \  ___/|  | \/\        /  / __ \|  | \/  |_> >___ \
    // |____|   |____(____  / ____|\___  >__|    \__/\  /  (____  /__|  |   __/____  >
    //                    \/\/         \/             \/        \/      |__|       \/
    override fun getHeader(): Array<String> {
        return arrayOf(
            "__________.__                             __      __                            ",
            "\\______   \\  | _____  ___.__. ___________/  \\    /  \\_____ _____________  ______",
            " |     ___/  | \\__  \\<   |  |/ __ \\_  __ \\   \\/\\/   /\\__  \\\\_  __ \\____ \\/  ___/",
            " |    |   |  |__/ __ \\\\___  \\  ___/|  | \\/\\        /  / __ \\|  | \\/  |_> >___ \\ ",
            " |____|   |____(____  / ____|\\___  >__|    \\__/\\  /  (____  /__|  |   __/____  >",
            "                    \\/\\/         \\/             \\/        \\/      |__|       \\/ "
        )
    }
}
