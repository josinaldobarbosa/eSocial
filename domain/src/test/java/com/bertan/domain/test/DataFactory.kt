package com.bertan.domain.test

import com.bertan.domain.model.*
import java.util.*

abstract class DataFactory<T> {
    abstract fun get(): T
    fun get(count: Int): List<T> = List(count) { get() }

    fun randomString(): String = UUID.randomUUID().toString()
    fun randomBoolean(): Boolean = Math.random() < 0.5
    fun randomInt(bound: Int = Int.MAX_VALUE): Int = Random().nextInt(bound)
    fun randomLong(): Long = randomInt().toLong()
}

object SourceDataFactory : DataFactory<Source>() {
    override fun get(): Source =
        Source(
            randomString(),
            randomString(),
            randomString(),
            randomState(),
            randomColour()
        )

    private fun randomState() =
        listOf(
            Source.State.Enabled,
            Source.State.Disabled
        ).random()

    private fun randomColour() =
        listOf(
            Source.Colour.RGB(randomInt(255), randomInt(255), randomInt(255)),
            Source.Colour.Hex(randomString())
        ).random()
}

object AccountDataFactory : DataFactory<Account>() {
    override fun get(): Account =
        Account(
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomLong(),
            randomLong()
        )
}

object PostDataFactory : DataFactory<Post>() {
    override fun get(): Post =
        Post(
            randomString(),
            randomString(),
            BodyDataFactory.get(),
            randomString(),
            randomLong()
        )
}

object CommentDataFactory : DataFactory<Comment>() {
    override fun get(): Comment =
        Comment(
            randomString(),
            randomString(),
            BodyDataFactory.get(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomLong()
        )
}

object BodyDataFactory : DataFactory<Body>() {
    override fun get(): Body =
        Body(
            randomType(),
            randomString()
        )

    private fun randomType() =
        listOf(
            Body.Type.Image,
            Body.Type.Text,
            Body.Type.Video
        ).random()
}