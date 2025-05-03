package de.eventsourcingbook.cart.cosmetics.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class ProductAggregate {

    @AggregateIdentifier
    lateinit var productId: String
    private var brand: String = ""
    private var categoryId: String? = null
    private var categoryCode: String? = null
    private var price: Double = 0.0
    private var archived: Boolean = false
    private var inventory: Int = 0
    private var reorderThreshold: Int = 10

    constructor()

    @CommandHandler
    constructor(command: CreateProductCommand) {
        if (command.price < 0) {
            throw IllegalArgumentException("Price cannot be negative")
        }
        
        AggregateLifecycle.apply(
            ProductCreatedEvent(
                productId = command.productId,
                brand = command.brand,
                categoryId = command.categoryId,
                categoryCode = command.categoryCode,
                price = command.price
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateProductCommand) {
        if (archived) {
            throw IllegalStateException("Cannot update archived product")
        }
        
        if (command.price < 0) {
            throw IllegalArgumentException("Price cannot be negative")
        }
        
        AggregateLifecycle.apply(
            ProductUpdatedEvent(
                productId = command.productId,
                brand = command.brand,
                categoryId = command.categoryId,
                categoryCode = command.categoryCode,
                price = command.price
            )
        )
    }

    @CommandHandler
    fun handle(command: ArchiveProductCommand) {
        if (archived) {
            return
        }
        
        AggregateLifecycle.apply(
            ProductArchivedEvent(productId = command.productId)
        )
    }

    @CommandHandler
    fun handle(command: ChangeInventoryCommand) {
        if (archived) {
            throw IllegalStateException("Cannot change inventory for archived product")
        }
        
        val newInventory = inventory + command.quantityChange
        if (newInventory < 0) {
            throw IllegalArgumentException("Inventory cannot be negative")
        }
        
        AggregateLifecycle.apply(
            InventoryChangedEvent(
                productId = command.productId,
                newInventory = newInventory,
                change = command.quantityChange
            )
        )
        
        // Check if we need to reorder
        if (newInventory <= reorderThreshold) {
            AggregateLifecycle.apply(
                ReorderTriggeredEvent(
                    productId = command.productId,
                    currentInventory = newInventory,
                    reorderThreshold = reorderThreshold
                )
            )
        }
    }

    @CommandHandler
    fun handle(command: SetReorderThresholdCommand) {
        if (archived) {
            throw IllegalStateException("Cannot set reorder threshold for archived product")
        }
        
        if (command.threshold < 0) {
            throw IllegalArgumentException("Reorder threshold cannot be negative")
        }
        
        AggregateLifecycle.apply(
            ReorderThresholdSetEvent(
                productId = command.productId,
                threshold = command.threshold
            )
        )
    }

    @EventSourcingHandler
    fun on(event: ProductCreatedEvent) {
        this.productId = event.productId
        this.brand = event.brand
        this.categoryId = event.categoryId
        this.categoryCode = event.categoryCode
        this.price = event.price
        this.inventory = 0
        this.archived = false
    }

    @EventSourcingHandler
    fun on(event: ProductUpdatedEvent) {
        this.brand = event.brand
        this.categoryId = event.categoryId
        this.categoryCode = event.categoryCode
        this.price = event.price
    }

    @EventSourcingHandler
    fun on(event: ProductArchivedEvent) {
        this.archived = true
    }

    @EventSourcingHandler
    fun on(event: InventoryChangedEvent) {
        this.inventory = event.newInventory
    }

    @EventSourcingHandler
    fun on(event: ReorderThresholdSetEvent) {
        this.reorderThreshold = event.threshold
    }
}