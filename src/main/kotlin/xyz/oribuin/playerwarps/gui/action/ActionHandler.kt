package xyz.oribuin.playerwarps.gui.action

import java.util.function.Function
import java.util.function.Supplier
import java.util.regex.Pattern

/**
 * @author HSGamer
 */
object ActionHandler {

    private val ACTION_PATTERN = Pattern.compile("\\[(\\w+)]\\W?(.*)")
    private val ACTIONS = mutableMapOf<String, Function<String, ActionProvider>>()

    init {
         registerAction("broadcast") { message -> BroadcastAction(message) }
        registerAction("close") { message -> CloseAction(message) }
        registerAction("console") { message -> ConsoleAction(message) }
        registerAction("message") { message -> MessageAction(message) }
        registerAction("player") { message -> PlayerAction(message) }
        registerAction("sound") { message -> SoundAction(message) }
    }

    /**
     * Register an action
     *
     * @param name           Name of the action
     * @param actionFunction Function to create the action, with the message as a parameter
     */
    private fun registerAction(name: String, actionFunction: Function<String, ActionProvider>) {
        // toLowerCase to avoid case-sensitive issues
        ACTIONS[name.lowercase()] = actionFunction
    }

    /**
     * Register an action
     *
     * @param name           Name of the action
     * @param actionSupplier Supplier to create the action
     */
    fun registerAction(name: String, actionSupplier: Supplier<ActionProvider>) {
        registerAction(name) { text ->
            val action = actionSupplier.get()
            action.message = text
            action
        }
    }

    /**
     * Parse the action from text
     *
     * @param text Text to parse
     * @return Action associated with the text
     */
    fun parse(text: String): ActionProvider? {
        // Check if the text matches the pattern ("[<action>] <message>") and get the action and message
        val matcher = ACTION_PATTERN.matcher(text)
        if (!matcher.find()) {
            return null
        }
        val actionName = matcher.group(1).lowercase() // toLowerCase to avoid case-sensitive issues
        val actionText = matcher.group(2)
        val action = ACTIONS[actionName] ?: return null
        return action.apply(actionText)
    }

}