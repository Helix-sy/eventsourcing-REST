package de.eventsourcingbook.cart.cosmetics.api

import de.eventsourcingbook.cart.cosmetics.domain.ArchiveProductCommand
import de.eventsourcingbook.cart.cosmetics.domain.CreateProductCommand
import de.eventsourcingbook.cart.cosmetics.domain.UpdateProductCommand
import de.eventsourcingbook.cart.cosmetics.readmodel.ProductView
import de.eventsourcingbook.cart.cosmetics.readmodel.ProductViewRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/products")
class ProductController(
    private val commandGateway: CommandGateway,
    private val productRepository: ProductViewRepository
) {
    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): CompletableFuture<ResponseEntity<String>> {
        val productId = request.productId ?: UUID.randomUUID().toString()

        val command = CreateProductCommand(
            aggregateId = UUID.randomUUID(),
            productId = productId,
            brand = request.brand,
            categoryId = request.categoryId,
            categoryCode = request.categoryCode,
            price = request.price
        )

        return commandGateway.send<String>(command)
            .thenApply { ResponseEntity.status(HttpStatus.CREATED).body(productId) }
            .exceptionally { ResponseEntity.status(HttpStatus.BAD_REQUEST).body(it.message) }
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: String,
        @RequestBody request: UpdateProductRequest
    ): CompletableFuture<ResponseEntity<String>> {
        val command = UpdateProductCommand(
            aggregateId = UUID.randomUUID(),
            productId = productId,
            brand = request.brand,
            categoryId = request.categoryId,
            categoryCode = request.categoryCode,
            price = request.price
        )

        return commandGateway.send<String>(command)
            .thenApply { ResponseEntity.ok(productId) }
            .exceptionally { ResponseEntity.status(HttpStatus.BAD_REQUEST).body(it.message) }
    }

    @DeleteMapping("/{productId}")
    fun archiveProduct(@PathVariable productId: String): CompletableFuture<ResponseEntity<String>> {
        val command = ArchiveProductCommand(
            aggregateId = UUID.randomUUID(),
            productId = productId
        )

        return commandGateway.send<String>(command)
            .thenApply { ResponseEntity.ok(productId) }
            .exceptionally { ResponseEntity.status(HttpStatus.BAD_REQUEST).body(it.message) }
    }

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: String): ResponseEntity<ProductView> {
        return productRepository.findById(productId)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductView>> {
        return ResponseEntity.ok(productRepository.findAll())
    }

    @GetMapping("/brand/{brand}")
    fun getProductsByBrand(@PathVariable brand: String): ResponseEntity<List<ProductView>> {
        return ResponseEntity.ok(productRepository.findByBrand(brand))
    }

    @GetMapping("/category/{categoryId}")
    fun getProductsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProductView>> {
        return ResponseEntity.ok(productRepository.findByCategoryId(categoryId))
    }
}

data class CreateProductRequest(
    val productId: String? = null,
    val brand: String,
    val categoryId: String? = null,
    val categoryCode: String? = null,
    val price: Double
)

data class UpdateProductRequest(
    val brand: String,
    val categoryId: String? = null,
    val categoryCode: String? = null,
    val price: Double
)