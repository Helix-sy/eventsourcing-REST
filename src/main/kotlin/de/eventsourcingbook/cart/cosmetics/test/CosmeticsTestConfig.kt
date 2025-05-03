package de.eventsourcingbook.cart.cosmetics.test

import de.eventsourcingbook.cart.cosmetics.api.DataImportController
import de.eventsourcingbook.cart.cosmetics.api.InventoryController
import de.eventsourcingbook.cart.cosmetics.api.ProductController
import de.eventsourcingbook.cart.cosmetics.domain.ProductAggregate
import de.eventsourcingbook.cart.cosmetics.domain.CosmeticsInventoryAggregate
import de.eventsourcingbook.cart.cosmetics.importer.CosmeticsDataImportServiceImpl
import de.eventsourcingbook.cart.cosmetics.services.CosmeticsDataImportService
import de.eventsourcingbook.cart.cosmetics.readmodel.InventoryProjection
import de.eventsourcingbook.cart.cosmetics.readmodel.ProductProjection
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ConditionalOnProperty(name = ["application.cosmetics.enabled"], havingValue = "true", matchIfMissing = false)
@ComponentScan(
    basePackageClasses = [
        // Domain
        ProductAggregate::class,
        CosmeticsInventoryAggregate::class,
        
        // API
        ProductController::class,
        InventoryController::class,
        DataImportController::class,
        
        // Importer
        CosmeticsDataImportServiceImpl::class,
        
        // Read models
        ProductProjection::class,
        InventoryProjection::class
    ],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [
                ProductAggregate::class,
                CosmeticsInventoryAggregate::class,
                ProductController::class,
                InventoryController::class,
                DataImportController::class,
                CosmeticsDataImportService::class,
                ProductProjection::class,
                InventoryProjection::class
            ]
        )
    ]
)
class CosmeticsTestConfig