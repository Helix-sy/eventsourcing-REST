package de.eventsourcingbook.cart.cosmetics.services

import java.util.concurrent.CompletableFuture

/**
 * Service interface for importing cosmetics data from CSV files
 */
interface CosmeticsDataImportService {
    /**
     * Import products from a CSV file
     * @param filePath The path to the CSV file
     * @return A CompletableFuture that resolves to the number of products imported
     */
    fun importProducts(filePath: String): CompletableFuture<Int>
    
    /**
     * Import data with a limit on the number of records processed
     * @param filePath The path to the CSV file
     * @param limit The maximum number of records to process
     * @return A CompletableFuture that resolves to an ImportResult containing statistics about the import
     */
    fun importData(filePath: String, limit: Int): CompletableFuture<ImportResult>
}

/**
 * Result of an import operation
 */
data class ImportResult(
    val linesProcessed: Int,
    val productsCreated: Int,
    val inventoriesCreated: Int,
    val cartEvents: Int
)