package xyz.oribuin.playerwarps.hook.economy

import me.realized.tokenmanager.TokenManagerPlugin
import me.realized.tokenmanager.util.NumberUtil
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class TokenManagerEconomyProvider : EconomyProvider {

    private var enabled: Boolean = false

    init {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("TokenManager")
    }

    override fun formatCurrency(amount: Double): String {
        if (!enabled) return amount.toString()

        return NumberUtil.withCommas(amount.toLong())
    }

    override fun checkBalance(offlinePlayer: OfflinePlayer): Double {
        if (!enabled) return 0.0

        return TokenManagerPlugin.getInstance().getTokens(offlinePlayer.player).orElse(0L).toDouble()
    }

    override fun deposit(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        TokenManagerPlugin.getInstance().addTokens(offlinePlayer.player, amount.toLong())
    }

    override fun withdraw(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        TokenManagerPlugin.getInstance().removeTokens(offlinePlayer.player, amount.toLong())
    }

}
