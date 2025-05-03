package de.eventsourcingbook.cart.cosmetics.api

import de.eventsourcingbook.cart.cosmetics.domain.CreateInventoryCommand
import de.eventsourcingbook.cart.cosmetics.domain.IncreaseInventoryCommand
import de.eventsourcingbook.cart.cosmetics.domain.ReduceInventoryCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/inventory")
class InventoryController(private val commandGateway: CommandGateway) {

    @PostMapping
    fun createInventory(
        @RequestParam("productId") productId: String,
        @RequestParam("initialStock", defaultValue = "100") initialStock: Int,
        @RequestParam("reorderThreshold", defaultValue = "20") reorderThreshold: Int,
        @RequestParam("reorderAmount", defaultValue = "50") reorderAmount: Int
    ): CompletableFuture<ResponseEntity<Map<String, Any>>> {
        return commandGateway.send<String>(
            CreateInventoryCommand(
                aggregateId = UUID.randomUUID(),
                productId = productId,
                initialStock = initialStock,
                reorderThreshold = reorderThreshold,
                reorderAmount = reorderAmount
            )
        ).thenApply {
            ResponseEntity.ok(mapOf<String, Any>(
                "status" to "success",
                "message" to "Inventory created for product $productId"
            ))
        }.exceptionally { ex ->
            ResponseEntity.badRequest().body(mapOf<String, Any>(
                "status" to "error",
                "message" to (ex.message ?: "Unknown error")
            ))
        }
    }

    @PostMapping("/{productId}/reduce")
    fun reduceInventory(
        @PathVariable("productId") productId: String,
        @RequestParam("quantity") quantity: Int
    ): CompletableFuture<ResponseEntity<Map<String, Any>>> {
        return commandGateway.send<String>(
            ReduceInventoryCommand(
                aggregateId = UUID.randomUUID(),
                productId = productId,
                quantity = quantity
            )
        ).thenApply {
            ResponseEntity.ok(mapOf<String, Any>(
                "status" to "success",
                "message" to "Inventory reduced for product $productId by $quantity"
            ))
        }.exceptionally { ex ->
            ResponseEntity.badRequest().body(mapOf<String, Any>(
                "status" to "error",
                "message" to (ex.message ?: "Unknown error")
            ))
        }
    }

    @PostMapping("/{productId}/increase")
    fun increaseInventory(
        @PathVariable("productId") productId: String,
        @RequestParam("quantity") quantity: Int
    ): CompletableFuture<ResponseEntity<Map<String, Any>>> {
        return commandGateway.send<String>(
            IncreaseInventoryCommand(
                aggregateId = UUID.randomUUID(),
                productId = productId,
                quantity = quantity
            )
        ).thenApply {
            ResponseEntity.ok(mapOf<String, Any>(
                "status" to "success",
                "message" to "Inventory increased for product $productId by $quantity"
            ))
        }.exceptionally { ex ->
            ResponseEntity.badRequest().body(mapOf<String, Any>(
                "status" to "error",
                "message" to (ex.message ?: "Unknown error")
            ))
        }
    }
}