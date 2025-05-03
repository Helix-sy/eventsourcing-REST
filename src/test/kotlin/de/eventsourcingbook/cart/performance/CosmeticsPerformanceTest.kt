package de.eventsourcingbook.cart.performance

import de.eventsourcingbook.cart.ApplicationStarter
import de.eventsourcingbook.cart.cosmetics.domain.CreateProductCommand
import de.eventsourcingbook.cart.cosmetics.domain.CreateInventoryCommand
import de.eventsourcingbook.cart.cosmetics.services.CosmeticsDataImportService
import de.eventsourcingbook.cart.cosmetics.services.ImportResult
import de.eventsourcingbook.cart.cosmetics.readmodel.ProductViewRepository
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import de.eventsourcingbook.cart.domain.commands.removeitem.RemoveItemCommand
import de.eventsourcingbook.cart.domain.commands.submitcart.SubmitCartCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.concurrent.TimeUnit

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("cosmetics-test")
@TestPropertySource(properties = ["application.cosmetics.enabled=true"])
class CosmeticsPerformanceTest {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var productRepository: ProductViewRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dataImportService: CosmeticsDataImportService

    @BeforeEach
    fun setup() {
        // Any setup needed for tests
    }

    @Test
    fun testProductCreationPerformance() {
        val iterations = 100
        val times = mutableListOf<Long>()

        repeat(iterations) {
            val productId = UUID.randomUUID().toString()
            val brand = "TestBrand-$it"
            val price = (it % 10 + 1) * 9.99

            val startTime = System.nanoTime()
            
            commandGateway.sendAndWait<String>(
                CreateProductCommand(
                    aggregateId = UUID.randomUUID(),
                    productId = productId,
                    brand = brand,
                    categoryId = "category-1",
                    categoryCode = "test-category",
                    price = price
                )
            )
            
            val endTime = System.nanoTime()
            val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
            times.add(duration)
        }

        val avgTime = times.average()
        val minTime = times.minOrNull()
        val maxTime = times.maxOrNull()
        
        println("Product Creation Performance (ms):")
        println("Average time: $avgTime")
        println("Min time: $minTime")
        println("Max time: $maxTime")
    }

    @Test
    fun testProductQueryPerformance() {
        // First create some test products
        repeat(100) {
            val productId = UUID.randomUUID().toString()
            commandGateway.sendAndWait<String>(
                CreateProductCommand(
                    aggregateId = UUID.randomUUID(),
                    productId = productId,
                    brand = "QueryTestBrand-${it % 10}",
                    categoryId = "category-${it % 5}",
                    categoryCode = "test-category",
                    price = (it % 10 + 1) * 9.99
                )
            )
        }

        // Test query performance
        val iterations = 100
        val times = mutableListOf<Long>()

        repeat(iterations) {
            val brand = "QueryTestBrand-${it % 10}"
            
            val startTime = System.nanoTime()
            
            val products = productRepository.findByBrand(brand)
            
            val endTime = System.nanoTime()
            val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
            times.add(duration)
        }

        val avgTime = times.average()
        val minTime = times.minOrNull()
        val maxTime = times.maxOrNull()
        
        println("Product Query Performance (ms):")
        println("Average time: $avgTime")
        println("Min time: $minTime")
        println("Max time: $maxTime")
    }

    @Test
    fun testBulkDataImportPerformance() {
        val filePath = "c:\\Users\\Helix\\Desktop\\SS25\\SE2 Eng\\cosmetic_archive\\2019-Oct.csv"
        val importLimit = 1000 // Only import a subset for testing
        
        val startTime = System.nanoTime()
        
        val result = dataImportService.importData(filePath, importLimit).get()
        
        val endTime = System.nanoTime()
        val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
        
        println("Bulk Data Import Performance (ms):")
        println("Total time for $importLimit records: $duration")
        println("Time per record: ${duration.toDouble() / importLimit}")
        println("Records processed: ${result.linesProcessed}")
        println("Products created: ${result.productsCreated}")
        println("Inventories created: ${result.inventoriesCreated}")
        println("Cart events: ${result.cartEvents}")
    }

    @Test
    fun testCartOperationsPerformance() {
        // First create some test products and inventory
        val products = mutableListOf<String>()
        repeat(10) {
            val productId = UUID.randomUUID().toString()
            commandGateway.sendAndWait<String>(
                CreateProductCommand(
                    aggregateId = UUID.randomUUID(),
                    productId = productId,
                    brand = "CartTestBrand-$it",
                    categoryId = "category-1",
                    categoryCode = "test-category",
                    price = (it + 1) * 9.99
                )
            )
            
            commandGateway.sendAndWait<String>(
                CreateInventoryCommand(
                    aggregateId = UUID.randomUUID(),
                    productId = productId,
                    initialStock = 100
                )
            )
            
            products.add(productId)
        }

        val cartId = UUID.randomUUID()
        val iterations = 50
        val times = mutableListOf<Long>()

        // Test adding items to cart
        repeat(iterations) {
            val productId = products[it % products.size]
            val itemId = UUID.randomUUID()
            val price = (it % 10 + 1) * 9.99
            
            val startTime = System.nanoTime()
            
            // Send actual AddItemCommand
            commandGateway.sendAndWait<String>(
                AddItemCommand(
                    aggregateId = cartId,
                    description = "Test product ${it}",
                    image = "test-image.jpg",
                    price = price,
                    totalPrice = price,
                    itemId = itemId,
                    productId = UUID.fromString(productId)
                )
            )
            
            val endTime = System.nanoTime()
            val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
            times.add(duration)
        }

        // Test removing some items
        repeat(10) {
            val startTime = System.nanoTime()
            
            // For simplicity, we're not tracking the actual itemIds that were added,
            // but in a real scenario you would remove specific items
            // This is just to demonstrate the performance measurement
            commandGateway.sendAndWait<String>(
                RemoveItemCommand(
                    aggregateId = cartId,
                    itemId = UUID.randomUUID() // This would be an actual itemId in a real test
                )
            )
            
            val endTime = System.nanoTime()
            val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
            times.add(duration)
        }
        
        // Finally submit the cart
        val submitStartTime = System.nanoTime()
        commandGateway.sendAndWait<String>(
            SubmitCartCommand(
                aggregateId = cartId
            )
        )
        val submitEndTime = System.nanoTime()
        val submitDuration = TimeUnit.NANOSECONDS.toMillis(submitEndTime - submitStartTime)
        times.add(submitDuration)

        val avgTime = times.average()
        val minTime = times.minOrNull()
        val maxTime = times.maxOrNull()
        
        println("Cart Operation Performance (ms):")
        println("Average time: $avgTime")
        println("Min time: $minTime")
        println("Max time: $maxTime")
        println("Submit cart time: $submitDuration")
    }
}