package de.eventsourcingbook.cart.cosmetics.domain

import de.eventsourcingbook.cart.common.Event

data class InventoryCreatedEvent(
    val productId: String,
    val stock: Int,
    val reorderThreshold: Int,
    val reorderAmount: Int
) : Event

data class InventoryReducedEvent(
    val productId: String,
    val quantity: Int
) : Event

data class InventoryIncreasedEvent(
    val productId: String,
    val quantity: Int
) : Event

data class ReorderTriggeredEvent(
    val productId: String,
    val currentInventory: Int,
    val reorderThreshold: Int
) : Event