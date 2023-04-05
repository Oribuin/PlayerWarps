package xyz.oribuin.playerwarps.hook.economy

import org.bukkit.OfflinePlayer
import xyz.oribuin.playerwarps.util.Lazy
import java.util.function.Supplier

enum class EconomyPlugin(lazyLoader: Supplier<EconomyProvider>) : EconomyProvider {

    VAULT(Supplier<EconomyProvider> { VaultEconomyProvider() }),
    PLAYERPOINTS(Supplier<EconomyProvider> { PlayerPointsEconomyProvider() }),
    TOKENMANAGER(Supplier<EconomyProvider> { TokenManagerEconomyProvider() }),
    TREASURY(Supplier<EconomyProvider> { TreasuryEconomyProvider() })
    ;

    private var economyProvider: Lazy<EconomyProvider>

    init {
        economyProvider = Lazy(lazyLoader)
    }

    override fun formatCurrency(amount: Double): String = economyProvider.get().formatCurrency(amount)

    override fun checkBalance(offlinePlayer: OfflinePlayer): Double = economyProvider.get().checkBalance(offlinePlayer)

    override fun deposit(offlinePlayer: OfflinePlayer, amount: Double) = economyProvider.get().deposit(offlinePlayer, amount)

    override fun withdraw(offlinePlayer: OfflinePlayer, amount: Double) = economyProvider.get().withdraw(offlinePlayer, amount)

}