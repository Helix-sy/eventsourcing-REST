package de.eventsourcingbook.cart.cosmetics.importer

import de.eventsourcingbook.cart.cosmetics.domain.CreateInventoryCommand
import de.eventsourcingbook.cart.cosmetics.domain.CreateProductCommand
import de.eventsourcingbook.cart.cosmetics.services.CosmeticsDataImportService
import de.eventsourcingbook.cart.cosmetics.services.ImportResult
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Service
class CosmeticsDataImportServiceImpl(private val commandGateway: CommandGateway) : CosmeticsDataImportService {
    
    private val logger = LoggerFactory.getLogger(CosmeticsDataImportServiceImpl::class.java)
    private val processedProductIds = ConcurrentHashMap<String, Boolean>()
    
    override fun importProducts(filePath: String): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync {
            try {
                val result = processFile(filePath, Int.MAX_VALUE)
                logger.info("Import completed: ${result.productsCreated} products created")
                result.productsCreated
            } catch (e: Exception) {
                logger.error("Error importing products", e)
                throw e
            }
        }
    }
    
    override fun importData(filePath: String, limit: Int): CompletableFuture<ImportResult> {
        return CompletableFuture.supplyAsync {
            try {
                val result = processFile(filePath, limit)
                logger.info("Import completed: processed ${result.linesProcessed} lines")
                result
            } catch (e: Exception) {
                logger.error("Error importing data", e)
                throw e
            }
        }
    }
    
    private fun processFile(filePath: String, limit: Int): ImportResult {
        val path = Paths.get(filePath)
        if (!Files.exists(path)) {
            throw IllegalArgumentException("File not found: $filePath")
        }
        
        var linesProcessed = 0
        var productsCreated = 0
        var inventoriesCreated = 0
        var cartEvents = 0
        
        BufferedReader(FileReader(filePath)).use { reader ->
            // Skip header line
            val header = reader.readLine()
            val headers = header.split(",")
            
            // Find indices of relevant columns
            val eventTimeIdx = headers.indexOf("event_time")
            val eventTypeIdx = headers.indexOf("event_type")
            val productIdIdx = headers.indexOf("product_id")
            val categoryIdIdx = headers.indexOf("category_id")
            val categoryCodeIdx = headers.indexOf("category_code")
            val brandIdx = headers.indexOf("brand")
            val priceIdx = headers.indexOf("price")
            
            if (productIdIdx == -1 || categoryIdIdx == -1 || brandIdx == -1 || priceIdx == -1) {
                throw IllegalArgumentException("CSV file missing required columns")
            }
            
            var line: String?
            while (reader.readLine().also { line = it } != null && linesProcessed < limit) {
                val values = line!!.split(",")
                
                if (values.size <= productIdIdx || values.size <= priceIdx) {
                    logger.warn("Skipping invalid line: $line")
                    continue
                }
                
                val productId = values[productIdIdx].trim()
                if (productId.isEmpty()) {
                    continue
                }
                
                val eventType = if (eventTypeIdx >= 0 && values.size > eventTypeIdx) values[eventTypeIdx].trim() else ""
                
                // Process product data if we haven't seen this product ID before
                if (!processedProductIds.containsKey(productId)) {
                    val brand = if (values.size > brandIdx) values[brandIdx].trim() else "Unknown"
                    val categoryId = if (values.size > categoryIdIdx) values[categoryIdIdx].trim() else "Unknown"
                    val categoryCode = if (categoryCodeIdx >= 0 && values.size > categoryCodeIdx) values[categoryCodeIdx].trim() else "Unknown"
                    
                    // Parse price, default to 0.0 if invalid
                    val price = if (values.size > priceIdx) {
                        try {
                            values[priceIdx].trim().toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }
                    } else {
                        0.0
                    }
                    
                    // Send command to create product
                    commandGateway.sendAndWait<String>(
                        CreateProductCommand(
                            aggregateId = UUID.randomUUID(),
                            productId = productId,
                            brand = brand,
                            categoryId = categoryId,
                            categoryCode = categoryCode,
                            price = price
                        )
                    )
                    productsCreated++
                    
                    // Send command to create inventory with default values
                    commandGateway.sendAndWait<String>(
                        CreateInventoryCommand(
                            aggregateId = UUID.randomUUID(),
                            productId = productId,
                            initialStock = 100,
                            reorderThreshold = 20,
                            reorderAmount = 50
                        )
                    )
                    inventoriesCreated++
                    
                    // Mark this product as processed
                    processedProductIds[productId] = true
                }
                
                // Count cart events
                if (eventType == "cart" || eventType == "purchase") {
                    cartEvents++
                }
                
                linesProcessed++
                if (linesProcessed % 1000 == 0) {
                    logger.info("Processed $linesProcessed lines")
                }
            }
        }
        
        return ImportResult(
            linesProcessed = linesProcessed,
            productsCreated = productsCreated,
            inventoriesCreated = inventoriesCreated,
            cartEvents = cartEvents
        )
    }
}