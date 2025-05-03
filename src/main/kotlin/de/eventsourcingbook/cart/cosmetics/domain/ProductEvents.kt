package de.eventsourcingbook.cart.cosmetics.domain

import de.eventsourcingbook.cart.common.Event

/**
 * Event emitted when a product is created
 */
data class ProductCreatedEvent(
    val productId: String,
    val brand: String,
    val categoryId: String?,
    val categoryCode: String?,
    val price: Double
) : Event

/**
 * Event emitted when a product is updated
 */
data class ProductUpdatedEvent(
    val productId: String,
    val brand: String,
    val categoryId: String?,
    val categoryCode: String?,
    val price: Double
) : Event

/**
 * Event emitted when a product is archived
 */
data class ProductArchivedEvent(
    val productId: String
) : Event

/**
 * Event emitted when inventory levels change
 */
data class InventoryChangedEvent(
    val productId: String,
    val newInventory: Int,
    val change: Int
) : Event

/**
 * Event emitted when a reorder threshold is set
 */
data class ReorderThresholdSetEvent(
    val productId: String,
    val threshold: Int
) : Event