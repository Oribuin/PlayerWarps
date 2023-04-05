package xyz.oribuin.playerwarps.hook.economy

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class VaultEconomyProvider : EconomyProvider {

    private var enabled: Boolean = false
    private var economy: Economy? = null

    init {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Vault")

        if (enabled) {
            val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)
            if (rsp != null) {
                economy = rsp.provider
            }
        }
    }

    override fun formatCurrency(amount: Double): String {
        if (!enabled) return amount.toString()

        return this.economy?.format(amount) ?: amount.toString()
    }

    override fun checkBalance(offlinePlayer: OfflinePlayer): Double {
        if (!enabled) return 0.0

        return this.economy?.getBalance(offlinePlayer) ?: 0.0
    }

    override fun deposit(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        this.economy?.depositPlayer(offlinePlayer, amount)
    }

    override fun withdraw(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        this.economy?.withdrawPlayer(offlinePlayer, amount)
    }

}