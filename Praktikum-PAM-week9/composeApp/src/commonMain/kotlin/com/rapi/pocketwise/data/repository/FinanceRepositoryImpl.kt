package com.rapi.pocketwise.data.repository

import com.rapi.pocketwise.data.model.Expense
import com.rapi.pocketwise.data.model.FinanceSummary
import com.rapi.pocketwise.data.remote.GeminiService

class FinanceRepositoryImpl(
    private val geminiService: GeminiService
) : FinanceRepository {

    override fun calculateSummary(expenses: List<Expense>): FinanceSummary {
        if (expenses.isEmpty()) {
            return FinanceSummary(
                totalExpense = 0,
                highestCategory = "-",
                categoryBreakdown = "-",
                expenseCount = 0
            )
        }

        val totalExpense = expenses.sumOf { expense ->
            expense.amount
        }

        val groupedByCategory = expenses
            .groupBy { expense ->
                expense.category.label
            }
            .mapValues { entry ->
                entry.value.sumOf { expense ->
                    expense.amount
                }
            }

        val highestCategory = groupedByCategory
            .maxByOrNull { entry ->
                entry.value
            }
            ?.key ?: "-"

        val categoryBreakdown = groupedByCategory.entries.joinToString(
            separator = "\n"
        ) { entry ->
            "- ${entry.key}: Rp${entry.value}"
        }

        return FinanceSummary(
            totalExpense = totalExpense,
            highestCategory = highestCategory,
            categoryBreakdown = categoryBreakdown,
            expenseCount = expenses.size
        )
    }

    override suspend fun analyzeExpenses(expenses: List<Expense>): Result<String> {
        if (expenses.isEmpty()) {
            return Result.failure(
                Exception("Belum ada data pengeluaran untuk dianalisis.")
            )
        }

        val summary = calculateSummary(expenses)

        val prompt = buildFinancePrompt(
            summary = summary,
            expenses = expenses
        )

        val geminiResult = geminiService.generateContent(prompt)

        return geminiResult.fold(
            onSuccess = { analysis ->
                Result.success(analysis)
            },
            onFailure = { error ->
                val message = error.message.orEmpty()

                val isQuotaOrRateLimitError =
                    message.contains("429") ||
                            message.contains("quota", ignoreCase = true) ||
                            message.contains("rate limit", ignoreCase = true) ||
                            message.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                            message.contains("free tier", ignoreCase = true)

                if (isQuotaOrRateLimitError) {
                    Result.success(
                        generateFallbackAnalysis(
                            summary = summary,
                            expenses = expenses,
                            reason = message
                        )
                    )
                } else {
                    Result.failure(error)
                }
            }
        )
    }

    private fun buildFinancePrompt(
        summary: FinanceSummary,
        expenses: List<Expense>
    ): String {
        val expenseDetails = expenses.joinToString(separator = "\n") { expense ->
            "- ${expense.category.label}: Rp${expense.amount}, catatan: ${expense.note.ifBlank { "-" }}"
        }

        return """
            Kamu adalah asisten keuangan pribadi untuk mahasiswa.

            Tugasmu adalah menganalisis data pengeluaran user dan memberikan saran hemat yang realistis.

            Data ringkasan:
            - Total pengeluaran: Rp${summary.totalExpense}
            - Jumlah transaksi: ${summary.expenseCount}
            - Kategori pengeluaran terbesar: ${summary.highestCategory}

            Breakdown kategori:
            ${summary.categoryBreakdown}

            Detail transaksi:
            $expenseDetails

            Aturan:
            - Gunakan Bahasa Indonesia
            - Jangan mengarang angka baru di luar data yang diberikan
            - Jangan memberikan rekomendasi produk keuangan tertentu
            - Fokus hanya pada pengelolaan pengeluaran harian
            - Fokus pada pola pengeluaran dan kebiasaan belanja
            - Jika data masih sedikit, jelaskan bahwa analisis masih terbatas
            - Berikan saran yang praktis untuk mahasiswa
            - Jawaban harus tegas, jelas, dan mudah dipahami

            Format jawaban:
            1. Ringkasan Kondisi Keuangan
            2. Pengeluaran Terbesar
            3. Pola yang Terlihat
            4. Risiko Jika Dibiarkan
            5. Saran Penghematan
            6. Target Minggu Depan
        """.trimIndent()
    }

    private fun generateFallbackAnalysis(
        summary: FinanceSummary,
        expenses: List<Expense>,
        reason: String
    ): String {
        val groupedByCategory = expenses
            .groupBy { expense -> expense.category.label }
            .mapValues { entry ->
                entry.value.sumOf { expense -> expense.amount }
            }

        val highestCategoryTotal = groupedByCategory[summary.highestCategory] ?: 0

        val highestCategoryPercentage = if (summary.totalExpense > 0) {
            (highestCategoryTotal * 100) / summary.totalExpense
        } else {
            0
        }

        val averageTransaction = if (summary.expenseCount > 0) {
            summary.totalExpense / summary.expenseCount
        } else {
            0
        }

        val categoryList = groupedByCategory.entries.joinToString(separator = "\n") { entry ->
            "- ${entry.key}: Rp${entry.value}"
        }

        val suggestion = when (summary.highestCategory) {
            "Makanan" -> "Kategori makanan paling dominan. Tetapkan batas makan harian dan kurangi pembelian makanan impulsif."
            "Kopi/Jajan" -> "Pengeluaran jajan terlihat dominan. Batasi pembelian kecil yang berulang karena jumlahnya cepat menumpuk."
            "Transportasi" -> "Transportasi menjadi pengeluaran utama. Evaluasi rute, frekuensi perjalanan, atau opsi transportasi yang lebih hemat."
            "Belanja" -> "Belanja menjadi kategori terbesar. Bedakan kebutuhan dan keinginan sebelum membeli barang."
            "Pendidikan" -> "Pengeluaran pendidikan cukup besar. Ini bisa bernilai positif, tapi tetap perlu dikontrol agar tidak mengganggu kebutuhan lain."
            else -> "Tetapkan batas pengeluaran mingguan agar keuangan lebih terkendali."
        }

        return """
        Mode Fallback Aktif

        Gemini API sedang tidak tersedia atau terkena batas penggunaan.
        PocketWise menampilkan analisis lokal sementara berdasarkan data pengeluaran yang dihitung oleh aplikasi.

        1. Ringkasan Kondisi Keuangan
        Total pengeluaran kamu saat ini adalah Rp${summary.totalExpense} dari ${summary.expenseCount} transaksi.
        Rata-rata pengeluaran per transaksi adalah sekitar Rp$averageTransaction.

        2. Pengeluaran Terbesar
        Kategori terbesar adalah ${summary.highestCategory} dengan total Rp$highestCategoryTotal.
        Kategori ini mengambil sekitar $highestCategoryPercentage% dari total pengeluaran.

        3. Breakdown Kategori
        $categoryList

        4. Pola yang Terlihat
        Data menunjukkan bahwa pengeluaran paling banyak berada pada kategori ${summary.highestCategory}.
        Semakin besar persentasenya, semakin besar pengaruh kategori ini terhadap kondisi pengeluaranmu.

        5. Risiko Jika Dibiarkan
        Jika pola ini terus berulang, kategori ${summary.highestCategory} bisa menghabiskan sebagian besar budget harian atau mingguan.

        6. Saran Penghematan
        $suggestion

        7. Target Minggu Depan
        Coba turunkan pengeluaran kategori ${summary.highestCategory} sekitar 10% dan catat semua transaksi agar pola pengeluaran lebih mudah dievaluasi.

        Catatan teknis:
        Analisis ini adalah fallback lokal, bukan respons langsung dari Gemini.
        Penyebab fallback: ${reason.take(180)}
    """.trimIndent()
    }
}