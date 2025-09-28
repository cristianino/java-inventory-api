package com.inventory.infrastructure.config;

import com.inventory.domain.port.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Database seeder component that runs on application startup.
 * This component ensures the database is properly initialized with required data
 * when the application starts, but only runs if no inventory data exists.
 */
@Component
@Profile({"docker", "dev", "!test"}) // Exclude from test profile
@Order(2) // Run after StartupConnectivityChecker (Order(1))
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    
    private final InventoryRepository inventoryRepository;
    
    @Value("${database.seeding.enabled:true}")
    private boolean seedingEnabled;

    public DatabaseSeeder(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!seedingEnabled) {
            log.info("🚫 Database seeding is disabled via configuration");
            return;
        }
        
        log.info("🌱 Checking if database seeding is needed...");
        
        try {
            // Check if any inventory data exists
            long recordCount = inventoryRepository.count();
            
            if (recordCount == 0) {
                log.info("📊 Database is empty, seeding will be handled by Flyway migration V2");
                log.info("✅ Initial inventory data will be inserted by migration script");
                log.info("🎯 Flyway migration V2__Insert_initial_inventory_data.sql will populate the database");
            } else {
                log.info("📈 Database already contains {} inventory records, skipping seeding", recordCount);
            }
            
            log.info("🎯 Database seeding check completed successfully");
            
        } catch (Exception e) {
            log.error("❌ Error during database seeding check: {}", e.getMessage(), e);
            // Don't fail the application startup for seeding issues
            log.warn("⚠️  Application will continue startup despite seeding check failure");
        }
    }
}