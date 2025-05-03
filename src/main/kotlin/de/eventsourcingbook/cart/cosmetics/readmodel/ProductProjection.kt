package de.eventsourcingbook.cart.cosmetics.readmodel

import de.eventsourcingbook.cart.cosmetics.domain.*
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "product_view")
class ProductView {
    @Id
    lateinit var productId: String
    lateinit var brand: String
    var categoryId: String? = null
    var categoryCode: String? = null
    var price: Double = 0.0
    var views: Long = 0
    var cartAdditions: Long = 0
    var purchases: Long = 0
    var isArchived: Boolean = false
    var lastUpdated: Instant? = null
}

@Repository
interface ProductViewRepository : JpaRepository<ProductView, String> {
    fun findByBrand(brand: String): List<ProductView>
    fun findByCategoryId(categoryId: String): List<ProductView>
}

@Component
class ProductProjection(private val repository: ProductViewRepository) {
    
    @EventHandler
    fun on(event: ProductCreatedEvent, @Timestamp timestamp: Instant) {
        val product = ProductView().apply {
            productId = event.productId
            brand = event.brand
            categoryId = event.categoryId
            categoryCode = event.categoryCode
            price = event.price
            views = 0
            cartAdditions = 0
            purchases = 0
            isArchived = false
            lastUpdated = timestamp
        }
        repository.save(product)
    }
    
    @EventHandler
    fun on(event: ProductUpdatedEvent, @Timestamp timestamp: Instant) {
        repository.findById(event.productId).ifPresent { product ->
            product.brand = event.brand
            product.categoryId = event.categoryId
            product.categoryCode = event.categoryCode
            product.price = event.price
            product.lastUpdated = timestamp
            repository.save(product)
        }
    }
    
    @EventHandler
    fun on(event: ProductArchivedEvent, @Timestamp timestamp: Instant) {
        repository.findById(event.productId).ifPresent { product ->
            product.isArchived = true
            product.lastUpdated = timestamp
            repository.save(product)
        }
    }
}