package xyz.oribuin.playerwarps.gui.enum

import xyz.oribuin.playerwarps.warp.Warp

enum class SortType(val displayName: String) {
    NONE("None"), // Default

    // Sort by warp names.
    NAME_ASCENDING("Names ↑ A-Z ↓"),
    NAME_DESCENDING("Names ↓ Z-A ↑"),

    // Sort by creation date
    CREATION_HIGH_LOW("Creation ↑ High - Low ↓"),
    CREATION_LOW_HIGH("Creation ↓ Low - High ↑"),

    // Sort by visits
    VISITS_LOW_HIGH("Visits ↓ Low - High ↑"),
    VISITS_HIGH_LOW("Visits ↑ High - Low ↓"),

    // Sort by likes
    LIKES_HIGH_LOW("Likes ↑ High - Low ↓"),
    LIKES_LOW_HIGH("Likes ↓ Low - High ↑"),

    ;

    fun sort(warps: MutableList<Warp>) {
        when (this) {
            // Warp Names
            NAME_ASCENDING -> warps.sortBy { it.name } // A-Z
            NAME_DESCENDING -> warps.sortByDescending { it.name } // Z-A

            // Warp Creation
            CREATION_LOW_HIGH -> warps.sortByDescending { it.creationTime } // Oldest first, since its time it goes backwards
            CREATION_HIGH_LOW -> warps.sortBy { it.creationTime }  // Newest first

            // Warp Visits
            VISITS_HIGH_LOW -> warps.sortBy { it.visitors.size } // Least visited first
            VISITS_LOW_HIGH -> warps.sortByDescending { it.visitors.size } // Most visited first

            // Warp Likes
            LIKES_HIGH_LOW -> warps.sortBy { it.likes.size } // Least liked first
            LIKES_LOW_HIGH -> warps.sortByDescending { it.likes.size } // Most liked first
            else -> {}
        }
    }
}