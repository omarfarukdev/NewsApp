package com.example.newsapp.di

import android.app.Application
import androidx.room.Room
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.local.NewsTypeConvertor
import com.example.newsapp.data.manger.LocalUserMangerImpl
import com.example.newsapp.data.remote.NewsApi
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.manger.LocalUserManger
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.usecases.app_entry.AppEntryUseCase
import com.example.newsapp.domain.usecases.app_entry.ReadAppEntry
import com.example.newsapp.domain.usecases.app_entry.SaveAppEntry
import com.example.newsapp.domain.usecases.news.DeleteArticle
import com.example.newsapp.domain.usecases.news.GetArticle
import com.example.newsapp.domain.usecases.news.GetArticles
import com.example.newsapp.domain.usecases.news.GetNews
import com.example.newsapp.domain.usecases.news.NewsUseCases
import com.example.newsapp.domain.usecases.news.SearchNews
import com.example.newsapp.domain.usecases.news.SelectArticle
import com.example.newsapp.domain.usecases.news.UpsertArticle
import com.example.newsapp.util.Constants.BASE_URL
import com.example.newsapp.util.Constants.NEWS_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides
    @Singleton
    fun provideLocalUserManger(
        application: Application
    ): LocalUserManger = LocalUserMangerImpl(context = application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManger: LocalUserManger
    ): AppEntryUseCase = AppEntryUseCase(
        readAppEntry = ReadAppEntry(localUserManger),
        saveAppEntry = SaveAppEntry(localUserManger)
    )

    @Provides
    @Singleton
    fun provideApiInstance(): NewsApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun providerNewsRepository(
        newsApi: NewsApi,
        newsDao: NewsDao
    ): NewsRepository= NewsRepositoryImpl(newsApi,newsDao)

    @Provides
    @Singleton
    fun providerNewsUseCases(
        newsRepository: NewsRepository,
        newsDao: NewsDao
    ): NewsUseCases {
        return NewsUseCases(
            getNews = GetNews(newsRepository),
            searchNews = SearchNews(newsRepository),
            upsertArticle = UpsertArticle(newsDao),
            deleteArticle = DeleteArticle(newsDao),
            getArticles = GetArticles(newsDao),
            getArticle = GetArticle(newsDao)
        )
    }
@Provides
@Singleton
fun provideNewsDatabase(application: Application): NewsDatabase {
    return Room.databaseBuilder(
        context = application,
        klass = NewsDatabase::class.java,
        name = NEWS_DATABASE_NAME    ).addTypeConverter(NewsTypeConvertor())
        .fallbackToDestructiveMigration()
        .build()
}
    @Provides
    @Singleton
    fun provideNewsDao(
        newsDatabase: NewsDatabase
    ): NewsDao = newsDatabase.dao

}
