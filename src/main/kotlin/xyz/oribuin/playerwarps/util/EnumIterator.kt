package xyz.oribuin.playerwarps.util

import kotlin.reflect.KClass

class EnumIterator<T : Enum<T>>(var sort: T, var iterator: Iterator<T>) {

    constructor(enumClass: Class<T>) : this(enumClass.enumConstants.first(), enumClass.enumConstants.iterator()) {
        iterator.next()
    }

    constructor(enumClass: Class<T>, sort: T) : this(sort, enumClass.enumConstants.iterator()) {
        while (iterator.hasNext()) {
            if (iterator.next() == sort) {
                iterator = enumClass.enumConstants.iterator()
                iterator.next()
                break
            }
        }
    }
}