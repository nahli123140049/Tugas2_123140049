package com.example.newsreaderapp.data.repository

import com.example.newsreaderapp.data.local.ArticleDao
import com.example.newsreaderapp.data.local.ArticleEntity
import com.example.newsreaderapp.domain.Article
import com.example.newsreaderapp.domain.repository.NewsRepository
import com.example.newsreaderapp.domain.repository.Resource
import io.ktor.client.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(
    private val client: HttpClient,
    private val dao: ArticleDao
) : NewsRepository {

    // Kumpulan Berita Indonesia (Mock Pool) buat simulasi
    private val beritaPool = listOf(
        ArticleEntity(1, "Timnas Indonesia Masuk Putaran 3 Kualifikasi Piala Dunia!", "Sejarah tercipta! Skuad Garuda asuhan Shin Tae-yong berhasil melaju ke putaran ketiga.", "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?q=80&w=600&auto=format&fit=crop", "https://www.pssi.org"),
        ArticleEntity(2, "Progres IKN Capai 80%, Siap Dipakai Upacara 17 Agustus", "Pembangunan Istana Negara di Ibu Kota Nusantara (IKN) terus dikebut.", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=600&auto=format&fit=crop", "https://ikn.go.id"),
        ArticleEntity(3, "Harga Cabai di Pasar Tradisional Mulai Turun", "Kabar baik! Harga cabai rawit merah mulai stabil di angka 45rb per kilo.", "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?q=80&w=600&auto=format&fit=crop", "https://www.kompas.com"),
        ArticleEntity(4, "Konser Bruno Mars Jakarta: Tiket Presale Ludes dalam 10 Menit", "Antusiasme luar biasa, ribuan fans war tiket sejak pagi tadi.", "https://images.unsplash.com/photo-1501281668745-f7f57925c3b4?q=80&w=600&auto=format&fit=crop", "https://www.detik.com"),
        ArticleEntity(5, "Wisata Kuliner: Viral Mie Gacoan Buka Cabang Baru di Pelosok", "Fenomena kuliner pedas dengan harga terjangkau masih sangat diminati.", "https://images.unsplash.com/photo-1552611052-33e04de081de?q=80&w=600&auto=format&fit=crop", "https://www.tribunnews.com"),
        ArticleEntity(6, "Cuaca Ekstrem: BMKG Ingatkan Potensi Hujan Lebat di Jawa", "Waspada banjir rob dan angin kencang di wilayah pesisir utara.", "https://images.unsplash.com/photo-1516912481808-34061f8bc634?q=80&w=600&auto=format&fit=crop", "https://www.bmkg.go.id"),
        ArticleEntity(7, "Rupiah Menguat Hari Ini Terhadap Dollar AS", "Sentimen positif pasar global membawa angin segar bagi nilai tukar rupiah.", "https://images.unsplash.com/photo-1580519542036-c47de6196ba5?q=80&w=600&auto=format&fit=crop", "https://www.bi.go.id"),
        ArticleEntity(8, "Teknologi: Startup AI Indonesia Raih Pendanaan Seri A", "Inovasi kecerdasan buatan anak bangsa mulai dilirik investor mancanegara.", "https://images.unsplash.com/photo-1677442136019-21780ecad995?q=80&w=600&auto=format&fit=crop", "https://www.techinasia.com")
    )

    override fun getArticles(): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        val localArticles = dao.getAllArticles().map { it.toArticle() }
        emit(Resource.Loading(data = localArticles))

        try {
            delay(1000)
            
            val randomArticles = beritaPool.shuffled().take(5)

            dao.clearArticles()
            dao.insertArticles(randomArticles)

            val updatedArticles = dao.getAllArticles().map { it.toArticle() }
            emit(Resource.Success(updatedArticles))

        } catch (e: Exception) {
            emit(Resource.Error(
                message = "Gagal update berita terbaru. Menampilkan berita offline.",
                data = localArticles
            ))
        }
    }

    override suspend fun getArticleById(id: Int): Article? {
        return dao.getArticleById(id)?.toArticle()
    }

    private fun ArticleEntity.toArticle() = Article(
        id = id,
        title = title,
        description = content,
        imageUrl = imageUrl,
        url = url
    )
}
