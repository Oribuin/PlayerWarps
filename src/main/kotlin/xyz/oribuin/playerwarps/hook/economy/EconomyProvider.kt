package xyz.oribuin.playerwarps.hook.economy

import org.bukkit.OfflinePlayer

interface EconomyProvider {
    /**
     * Formats a currency value to a user-displayable string.
     *
     * @param amount The currency amount to format
     * @return The formatted currency string
     */
    fun formatCurrency(amount: Double): String

    /**
     * Gets the balance of the specified player, or 0 if the balance was unable to be looked up.
     *
     * @param offlinePlayer The player to get the balance of
     * @return The balance of the player
     */
    fun checkBalance(offlinePlayer: OfflinePlayer): Double

    /**
     * Deposits the specified amount of currency into the specified player's account.
     *
     * @param offlinePlayer The player to deposit the currency into
     * @param amount        The amount of currency to deposit
     */
    fun deposit(offlinePlayer: OfflinePlayer, amount: Double)

    /**
     * Withdraws the specified amount of currency from the specified player's account.
     *
     * @param offlinePlayer The player to withdraw the currency from
     * @param amount        The amount of currency to withdraw
     */
    fun withdraw(offlinePlayer: OfflinePlayer, amount: Double)
}