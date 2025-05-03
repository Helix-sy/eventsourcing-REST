package de.eventsourcingbook.cart.cosmetics.domain

import de.eventsourcingbook.cart.common.Command
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreateProductCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val brand: String,
    val categoryId: String?,
    val categoryCode: String?,
    val price: Double
) : Command

data class UpdateProductCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val brand: String,
    val categoryId: String?,
    val categoryCode: String?,
    val price: Double
) : Command

data class ArchiveProductCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String
) : Command

data class ChangeInventoryCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val quantityChange: Int
) : Command

data class SetReorderThresholdCommand(
    @TargetAggregateIdentifier
    override var aggregateId: UUID,
    val productId: String,
    val threshold: Int
) : Command