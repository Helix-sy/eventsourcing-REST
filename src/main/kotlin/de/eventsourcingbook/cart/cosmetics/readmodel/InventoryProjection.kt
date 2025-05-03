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
@Table(name = "inventory_view")
class InventoryView {
    @Id
    lateinit var productId: String
    var stock: Int = 0
    var reorderThreshold: Int = 0
    var reorderAmount: Int = 0
    var reorderInProgress: Boolean = false
    var lastUpdated: Instant? = null
}

@Repository
interface InventoryViewRepository : JpaRepository<InventoryView, String> {
    fun findByStockLessThanEqual(threshold: Int): List<InventoryView>
}

@Component
class InventoryProjection(private val repository: InventoryViewRepository) {
    
    @EventHandler
    fun on(event: InventoryCreatedEvent, @Timestamp timestamp: Instant) {
        val inventory = InventoryView().apply {
            productId = event.productId
            stock = event.stock
            reorderThreshold = event.reorderThreshold
            reorderAmount = event.reorderAmount
            reorderInProgress = false
            lastUpdated = timestamp
        }
        repository.save(inventory)
    }
    
    @EventHandler
    fun on(event: InventoryReducedEvent, @Timestamp timestamp: Instant) {
        repository.findById(event.productId).ifPresent { inventory ->
            inventory.stock -= event.quantity
            inventory.lastUpdated = timestamp
            repository.save(inventory)
        }
    }
    
    @EventHandler
    fun on(event: InventoryIncreasedEvent, @Timestamp timestamp: Instant) {
        repository.findById(event.productId).ifPresent { inventory ->
            inventory.stock += event.quantity
            inventory.reorderInProgress = false
            inventory.lastUpdated = timestamp
            repository.save(inventory)
        }
    }
    
    @EventHandler
    fun on(event: ReorderTriggeredEvent, @Timestamp timestamp: Instant) {
        repository.findById(event.productId).ifPresent { inventory ->
            inventory.reorderInProgress = true
            inventory.lastUpdated = timestamp
            repository.save(inventory)
        }
    }
}