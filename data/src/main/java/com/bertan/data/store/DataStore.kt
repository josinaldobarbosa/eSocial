package com.bertan.data.store

import com.bertan.data.model.AccountEntity
import com.bertan.data.model.CommentEntity
import com.bertan.data.model.PostEntity
import com.bertan.data.model.SourceEntity
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface DataStore {
    fun getSources(): Observable<List<SourceEntity>>

    fun getAccounts(): Observable<List<AccountEntity>>
    fun getAccount(accountId: String): Observable<Optional<AccountEntity>>
    fun addAccount(account: AccountEntity): Completable

    fun getPosts(): Observable<List<PostEntity>>
    fun getPost(postId: String): Observable<Optional<PostEntity>>
    fun addPost(post: PostEntity): Completable

    fun getCommentsByPost(postId: String): Observable<List<CommentEntity>>
    fun getComment(postId: String, commentId: String): Observable<Optional<CommentEntity>>
    fun addComment(comment: CommentEntity): Completable
}