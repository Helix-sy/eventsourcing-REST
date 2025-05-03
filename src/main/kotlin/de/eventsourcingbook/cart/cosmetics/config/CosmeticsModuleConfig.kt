package de.eventsourcingbook.cart.cosmetics.config

import de.eventsourcingbook.cart.cosmetics.api.DataImportController
import de.eventsourcingbook.cart.cosmetics.api.InventoryController
import de.eventsourcingbook.cart.cosmetics.api.ProductController
import de.eventsourcingbook.cart.cosmetics.domain.CosmeticsInventoryAggregate
import de.eventsourcingbook.cart.cosmetics.domain.ProductAggregate
import de.eventsourcingbook.cart.cosmetics.services.CosmeticsDataImportService
import de.eventsourcingbook.cart.cosmetics.readmodel.InventoryProjection
import de.eventsourcingbook.cart.cosmetics.readmodel.ProductProjection
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ConditionalOnProperty(name = ["application.cosmetics.enabled"], havingValue = "true", matchIfMissing = false)
class CosmeticsModuleConfig {
    // This class enables the cosmetics module when application.cosmetics.enabled=true
}