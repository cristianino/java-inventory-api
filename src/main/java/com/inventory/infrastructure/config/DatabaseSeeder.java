package com.inventory.infrastructure.config;

import com.inventory.domain.port.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;  
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;

/**
 * Database seeder component that runs on application startup.
 * This component verifies that the database has been properly seeded with initial data.
 * 
 * IMPORTANT: The database must be created manually before application startup.
 * See DATABASE_SETUP.md for detailed instructions.
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
        
        log.info("🌱 Checking database seeding status...");
        
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
            
        } catch (DataAccessException e) {
            log.warn("⚠️  Could not check inventory count, database may be initializing: {}", e.getMessage());
            log.info("🔄 Flyway migrations will handle database setup");
            log.warn("⚠️  Application will continue startup despite seeding check failure");
        } catch (Exception e) {
            log.error("❌ Error during database seeding check: {}", e.getMessage(), e);
            log.warn("⚠️  Application will continue startup despite seeding check failure");
        }
    }
}