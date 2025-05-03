package de.eventsourcingbook.cart.cosmetics.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate(snapshotTriggerDefinition = "cosmeticsSnapshotTrigger")
class CosmeticsInventoryAggregate {
    @AggregateIdentifier
    private lateinit var productId: String
    private var stock: Int = 0
    private var reorderThreshold: Int = 10
    private var reorderAmount: Int = 50
    private var reorderInProgress: Boolean = false

    constructor()

    @CommandHandler
    constructor(command: CreateInventoryCommand) {
        AggregateLifecycle.apply(
            InventoryCreatedEvent(
                productId = command.productId,
                stock = command.initialStock,
                reorderThreshold = command.reorderThreshold,
                reorderAmount = command.reorderAmount
            )
        )
    }

    @CommandHandler
    fun handle(command: ReduceInventoryCommand) {
        if (stock < command.quantity) {
            throw InsufficientStockException("Insufficient stock for product $productId: required ${command.quantity}, available $stock")
        }

        AggregateLifecycle.apply(
            InventoryReducedEvent(
                productId = command.productId,
                quantity = command.quantity
            )
        )

        // Auto-reorder logic
        if (stock - command.quantity <= reorderThreshold && !reorderInProgress) {
            AggregateLifecycle.apply(
                ReorderTriggeredEvent(
                    productId = productId,
                    currentInventory = stock - command.quantity,
                    reorderThreshold = reorderThreshold
                )
            )
        }
    }

    @CommandHandler
    fun handle(command: IncreaseInventoryCommand) {
        AggregateLifecycle.apply(
            InventoryIncreasedEvent(
                productId = command.productId,
                quantity = command.quantity
            )
        )
    }

    @EventSourcingHandler
    fun on(event: InventoryCreatedEvent) {
        productId = event.productId
        stock = event.stock
        reorderThreshold = event.reorderThreshold
        reorderAmount = event.reorderAmount
        reorderInProgress = false
    }

    @EventSourcingHandler
    fun on(event: InventoryReducedEvent) {
        stock -= event.quantity
    }

    @EventSourcingHandler
    fun on(event: InventoryIncreasedEvent) {
        stock += event.quantity
        reorderInProgress = false
    }

    @EventSourcingHandler
    fun on(event: ReorderTriggeredEvent) {
        reorderInProgress = true
    }
}

class InsufficientStockException(message: String) : RuntimeException(message)