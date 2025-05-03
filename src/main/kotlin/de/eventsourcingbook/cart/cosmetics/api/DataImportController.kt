package de.eventsourcingbook.cart.cosmetics.api

import de.eventsourcingbook.cart.cosmetics.services.CosmeticsDataImportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/import")
class DataImportController(private val importService: CosmeticsDataImportService) {

    @PostMapping("/cosmetics")
    fun importCosmeticsData(
        @RequestParam("filePath", required = false) filePath: String = "c:\\Users\\Helix\\Desktop\\SS25\\SE2 Eng\\cosmetic_archive\\cosmetics_data.csv"
    ): CompletableFuture<ResponseEntity<Map<String, Any>>> {
        return importService.importProducts(filePath)
            .thenApply { count ->
                ResponseEntity.ok(mapOf<String, Any>(
                    "status" to "success",
                    "message" to "Import completed",
                    "productsImported" to count
                ))
            }
            .exceptionally { ex ->
                ResponseEntity.badRequest().body(mapOf<String, Any>(
                    "status" to "error",
                    "message" to (ex.message ?: "Unknown error")
                ))
            }
    }
    
    @PostMapping("/cosmetics-with-limit")
    fun importCosmeticsDataWithLimit(
        @RequestParam("filePath") filePath: String,
        @RequestParam("limit", defaultValue = "1000") limit: Int
    ): CompletableFuture<ResponseEntity<Map<String, Any>>> {
        return importService.importData(filePath, limit)
            .thenApply { result ->
                ResponseEntity.ok(mapOf<String, Any>(
                    "status" to "success",
                    "message" to "Import completed",
                    "linesProcessed" to result.linesProcessed,
                    "productsCreated" to result.productsCreated,
                    "inventoriesCreated" to result.inventoriesCreated,
                    "cartEvents" to result.cartEvents
                ))
            }
            .exceptionally { ex ->
                ResponseEntity.badRequest().body(mapOf<String, Any>(
                    "status" to "error",
                    "message" to (ex.message ?: "Unknown error")
                ))
            }
    }
}