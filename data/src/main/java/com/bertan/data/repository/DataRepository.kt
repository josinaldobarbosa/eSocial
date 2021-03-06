package com.bertan.data.repository

import com.bertan.data.mapper.AccountEntityMapper.asAccount
import com.bertan.data.mapper.AccountMapper.asAccountEntity
import com.bertan.data.mapper.CommentEntityMapper.asComment
import com.bertan.data.mapper.CommentMapper.asCommentEntity
import com.bertan.data.mapper.PostEntityMapper.asPost
import com.bertan.data.mapper.PostMapper.asPostEntity
import com.bertan.data.mapper.SourceEntityMapper.asSource
import com.bertan.data.store.DataStore
import com.bertan.domain.model.Account
import com.bertan.domain.model.Comment
import com.bertan.domain.model.Post
import com.bertan.domain.model.Source
import com.bertan.domain.repository.Repository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableDefer
import java.util.*

class DataRepository(
    private val localDataStore: DataStore,
    private val remoteDataStore: DataStore
) : Repository {
    override fun getSources(): Observable<List<Source>> =
        localDataStore.getSources()
            .map { result -> result.map { it.asSource } }

    override fun getAccounts(): Observable<List<Account>> =
        localDataStore.getAccounts()
            .map { result -> result.map { it.asAccount } }

    override fun getAccount(accountId: String): Observable<Optional<Account>> =
        localDataStore.getAccount(accountId)
            .map { result -> result.map { it.asAccount } }

    override fun addAccount(account: Account): Completable =
        localDataStore.addAccount(account.asAccountEntity)

    override fun getPosts(): Observable<List<Post>> =
        remoteDataStore.getPosts()
            .flatMapCompletable { posts ->
                Completable.merge(posts.map { localDataStore.addPost(it) })
            }
            .onErrorComplete()
            .andThen(ObservableDefer.defer { localDataStore.getPosts() })
            .map { result -> result.map { it.asPost } }

    override fun getPost(postId: String): Observable<Optional<Post>> =
        remoteDataStore.getPost(postId)
            .flatMapCompletable { post ->
                post.map { localDataStore.addPost(it) }.orElse(Completable.complete())
            }
            .onErrorComplete()
            .andThen(ObservableDefer.defer { localDataStore.getPost(postId) })
            .map { result -> result.map { it.asPost } }

    override fun addPost(post: Post): Completable =
        localDataStore.addPost(post.asPostEntity)

    override fun getCommentsByPost(postId: String): Observable<List<Comment>> =
        remoteDataStore.getCommentsByPost(postId)
            .flatMapCompletable { comments ->
                Completable.merge(comments.map { localDataStore.addComment(it) })
            }
            .onErrorComplete()
            .andThen(ObservableDefer.defer { localDataStore.getCommentsByPost(postId) })
            .map { result -> result.map { it.asComment } }

    override fun getComment(postId: String, commentId: String): Observable<Optional<Comment>> =
        remoteDataStore.getComment(postId, commentId)
            .flatMapCompletable { comment ->
                comment.map { localDataStore.addComment(it) }.orElse(Completable.complete())
            }
            .onErrorComplete()
            .andThen(ObservableDefer.defer { localDataStore.getComment(postId, commentId) })
            .map { result -> result.map { it.asComment } }

    override fun addComment(comment: Comment): Completable =
        localDataStore.addComment(comment.asCommentEntity)

}