package xyz.oribuin.playerwarps.util.nms

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.rosewood.rosegarden.utils.NMSUtil
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

object SkullUtils {

    private var method_SkullMeta_setProfile: Method? = null
    private var field_SkullMeta_profile: Field? = null

    /**
     * Applies a base64 encoded texture to an item's SkullMeta
     *
     * @param skullMeta The ItemMeta for the Skull
     * @param texture The texture to apply to the skull
     */
    fun setSkullTexture(skullMeta: SkullMeta, texture: String?) {
        if (texture.isNullOrEmpty()) return

        var newTexture = texture
        if (newTexture.startsWith("hdb:") && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            newTexture = HeadDatabaseAPI().getBase64(texture.substring(4))
            if (newTexture == null) return
        }

        val profile = GameProfile(UUID.nameUUIDFromBytes(texture.toByteArray()), null)
        profile.properties.put("textures", Property("textures", texture))

        try {
            if (NMSUtil.getVersionNumber() > 15) {
                if (method_SkullMeta_setProfile == null) {
                    method_SkullMeta_setProfile = skullMeta.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
                    method_SkullMeta_setProfile?.isAccessible = true
                }

                method_SkullMeta_setProfile?.invoke(skullMeta, profile)
            } else {
                if (field_SkullMeta_profile == null) {
                    field_SkullMeta_profile = skullMeta.javaClass.getDeclaredField("profile")
                    field_SkullMeta_profile?.isAccessible = true
                }

                field_SkullMeta_profile?.set(skullMeta, profile)
            }
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
        }
    }

}