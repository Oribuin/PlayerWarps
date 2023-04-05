package xyz.oribuin.playerwarps.hook.economy

import me.lokka30.treasury.api.common.service.ServiceRegistry
import me.lokka30.treasury.api.economy.response.EconomySubscriber
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.concurrent.TimeUnit

class TreasuryEconomyProvider : EconomyProvider {

    private var enabled: Boolean = false
    private var economy: me.lokka30.treasury.api.economy.EconomyProvider? = null

    init {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("Treasury")
        if (this.enabled) {
            this.economy = ServiceRegistry.INSTANCE.serviceFor(me.lokka30.treasury.api.economy.EconomyProvider::class.java)
                .map { it.get() }
                .orElse(null)

        }

    }

    override fun formatCurrency(amount: Double): String {
        if (!enabled) return amount.toString()

        return this.economy?.primaryCurrency?.format(amount.toBigDecimal(), null, 2) ?: amount.toString()
    }

    override fun checkBalance(offlinePlayer: OfflinePlayer): Double {
        if (!enabled || economy == null) return 0.0

        // We're only using !! here because we've already checked if the economy is null.
        return try {
            this.economy?.let { provider ->
                EconomySubscriber.asFuture { provider.retrievePlayerAccount(offlinePlayer.uniqueId, it) }
                    .thenCompose { account -> EconomySubscriber.asFuture { account.retrieveBalance(provider.primaryCurrency, it) } }
                    .get(3, TimeUnit.SECONDS)
                    .toDouble()
            } ?: 0.0

        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    override fun deposit(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled || economy == null) return

        this.economy?.let { provider ->
            EconomySubscriber.asFuture { provider.retrievePlayerAccount(offlinePlayer.uniqueId, it) }
                .thenCompose { account ->
                    EconomySubscriber.asFuture {
                        account.depositBalance(amount.toBigDecimal(), EconomyTransactionInitiator.SERVER, provider.primaryCurrency, it)
                    }
                }.whenComplete { _, _ ->  /* Do nothing */ }
        }
    }

    override fun withdraw(offlinePlayer: OfflinePlayer, amount: Double) {
        if (!enabled) return

        this.economy?.let { provider ->
            EconomySubscriber.asFuture { provider.retrievePlayerAccount(offlinePlayer.uniqueId, it) }
                .thenCompose { account ->
                    EconomySubscriber.asFuture {
                        account.withdrawBalance(amount.toBigDecimal(), EconomyTransactionInitiator.SERVER, provider.primaryCurrency, it)
                    }
                }.whenComplete { _, _ ->  /* Do nothing */ }
        }
    }


}
