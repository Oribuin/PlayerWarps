package xyz.oribuin.playerwarps.hook.economy

import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.black_ixx.playerpoints.manager.LocaleManager
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import kotlin.math.round

class PlayerPointsEconomyProvider : EconomyProvider {

    private var enabled: Boolean = false
    private var economy: PlayerPointsAPI? = null

    init {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")
        if (this.enabled)
            this.economy = PlayerPoints.getInstance().api
    }

    override fun formatCurrency(amount: Double): String {
        if (!enabled) return amount.toString()

        return PlayerPoints.getInstance().getManager(LocaleManager::class.java).getCurrencyName(round(amount).toInt())
    }

    override fun checkBalance(offlinePlayer: OfflinePlayer): Double {
        if (!enabled) return 0.0

        return this.economy?.look(offlinePlayer.uniqueId)?.toDouble() ?: 0.0
    }

    override fun deposit(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        this.economy?.give(offlinePlayer.uniqueId, round(amount).toInt())
    }

    override fun withdraw(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        this.economy?.take(offlinePlayer.uniqueId, round(amount).toInt())
    }

}