package de.eventsourcingbook.cart.cosmetics.domain

import de.eventsourcingbook.cart.common.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateInventoryCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val initialStock: Int,
    val reorderThreshold: Int = 20,
    val reorderAmount: Int = 50
) : Command

data class ReduceInventoryCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val quantity: Int
) : Command

data class IncreaseInventoryCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val quantity: Int
) : Command