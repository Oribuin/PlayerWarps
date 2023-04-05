package xyz.oribuin.playerwarps.util

import org.jetbrains.annotations.NotNull
import java.util.function.Supplier

class Lazy<T>(private var lazyLoader: Supplier<T>) : Supplier<T> {

    private var value: T? = null

    @NotNull
    override fun get(): T = value ?: lazyLoader.get().also { value = it }

}