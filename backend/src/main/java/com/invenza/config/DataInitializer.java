package com.invenza.config;

import com.invenza.entity.Category;
import com.invenza.entity.Location;
import com.invenza.entity.Warehouse;
import com.invenza.repository.CategoryRepository;
import com.invenza.repository.LocationRepository;
import com.invenza.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Initializes seed reference data if database is empty.
 * Matches default seed data from Python backend reset_data.py.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           WarehouseRepository warehouseRepository,
                           LocationRepository locationRepository) {
        this.categoryRepository = categoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Seeding initial categories...");
            categoryRepository.saveAll(List.of(
                    new Category("Electronics"),
                    new Category("Furniture"),
                    new Category("Supplies")
            ));
        }

        if (warehouseRepository.count() == 0) {
            log.info("Seeding initial warehouses and locations...");

            Warehouse mainWh = new Warehouse("Main Warehouse", "MAIN", "123 Industrial Ave");
            warehouseRepository.save(mainWh);

            Location mainStock = new Location("Stock", "MAIN-STK", mainWh);
            Location mainIn = new Location("Receiving", "MAIN-REC", mainWh);
            Location mainOut = new Location("Shipping", "MAIN-SHP", mainWh);
            locationRepository.saveAll(List.of(mainStock, mainIn, mainOut));

            Warehouse secWh = new Warehouse("Secondary Warehouse", "SEC", "456 Logistics Blvd");
            warehouseRepository.save(secWh);

            Location secStock = new Location("Stock", "SEC-STK", secWh);
            locationRepository.save(secStock);

            log.info("Default seed data initialized successfully.");
        }
    }
}

