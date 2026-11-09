package com.invenza.service.impl;

import com.invenza.dto.product.ExcelImportResponse;
import com.invenza.entity.Category;
import com.invenza.entity.Location;
import com.invenza.entity.Product;
import com.invenza.entity.Stock;
import com.invenza.exception.BadRequestException;
import com.invenza.repository.CategoryRepository;
import com.invenza.repository.LocationRepository;
import com.invenza.repository.ProductRepository;
import com.invenza.repository.StockRepository;
import com.invenza.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;

    public ExcelServiceImpl(ProductRepository productRepository,
                            CategoryRepository categoryRepository,
                            LocationRepository locationRepository,
                            StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    public ByteArrayInputStream generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"SKU", "Product Name", "Category", "Unit of Measure", "Current Stock", "Unit Price", "Reorder Threshold", "Image URL"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Instructions Sheet
            Sheet instSheet = workbook.createSheet("Instructions");
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            Row instHeader = instSheet.createRow(0);
            String[] instHeaders = {"Column", "Required", "Description"};
            for (int i = 0; i < instHeaders.length; i++) {
                Cell cell = instHeader.createCell(i);
                cell.setCellValue(instHeaders[i]);
                cell.setCellStyle(boldStyle);
            }

            String[][] instructions = {
                    {"SKU", "Yes", "Unique identifier for the product"},
                    {"Product Name", "Yes", "Name of the product"},
                    {"Category", "No", "Product category (will be created if missing)"},
                    {"Unit of Measure", "No", "e.g., pcs, kg, ml"},
                    {"Current Stock", "No", "Initial stock quantity (numeric)"},
                    {"Unit Price", "No", "Price per unit (numeric)"},
                    {"Reorder Threshold", "No", "Minimum stock level for alerts (numeric)"},
                    {"Image URL", "No", "Public URL to product image"}
            };

            for (int i = 0; i < instructions.length; i++) {
                Row row = instSheet.createRow(i + 1);
                for (int j = 0; j < instructions[i].length; j++) {
                    row.createCell(j).setCellValue(instructions[i][j]);
                }
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            for (int i = 0; i < instHeaders.length; i++) instSheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel template: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportProducts(String search, UUID categoryId) {
        String query = (search != null && !search.isBlank()) ? search.trim() : null;
        List<Product> products = productRepository.searchProducts(query, categoryId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"SKU", "Product Name", "Category", "Current Stock", "Unit Price", "Total Value", "Reorder Threshold", "Image URL", "Created At"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            BigDecimal totalQty = BigDecimal.ZERO;
            BigDecimal totalVal = BigDecimal.ZERO;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault());

            int rowIndex = 1;
            for (Product p : products) {
                BigDecimal qty = p.calculateCurrentStock();
                BigDecimal price = (p.getStockEntries() != null && !p.getStockEntries().isEmpty())
                        ? p.getStockEntries().get(0).getPerUnitCost()
                        : BigDecimal.ZERO;
                BigDecimal val = qty.multiply(price);

                totalQty = totalQty.add(qty);
                totalVal = totalVal.add(val);

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(p.getSku());
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getCategory() != null ? p.getCategory().getName() : "");
                row.createCell(3).setCellValue(qty.doubleValue());
                row.createCell(4).setCellValue(price.doubleValue());
                row.createCell(5).setCellValue(val.doubleValue());
                row.createCell(6).setCellValue(p.getReorderThreshold() != null ? p.getReorderThreshold().doubleValue() : 0.0);
                row.createCell(7).setCellValue(p.getImageData() != null && p.getImageData().startsWith("http") ? p.getImageData() : "");
                row.createCell(8).setCellValue(p.getCreatedAt() != null ? formatter.format(p.getCreatedAt()) : "");
            }

            // Summary Sheet
            Sheet summarySheet = workbook.createSheet("Summary");
            Row summaryHeader = summarySheet.createRow(0);
            summaryHeader.createCell(0).setCellValue("Metric");
            summaryHeader.createCell(1).setCellValue("Value");
            summaryHeader.getCell(0).setCellStyle(headerStyle);
            summaryHeader.getCell(1).setCellStyle(headerStyle);

            summarySheet.createRow(1).createCell(0).setCellValue("Total Products");
            summarySheet.getRow(1).createCell(1).setCellValue(products.size());

            summarySheet.createRow(2).createCell(0).setCellValue("Total Quantity");
            summarySheet.getRow(2).createCell(1).setCellValue(totalQty.doubleValue());

            summarySheet.createRow(3).createCell(0).setCellValue("Total Inventory Value");
            summarySheet.getRow(3).createCell(1).setCellValue(totalVal.doubleValue());

            summarySheet.createRow(4).createCell(0).setCellValue("Export Date");
            summarySheet.getRow(4).createCell(1).setCellValue(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(java.time.LocalDateTime.now()));

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to export products to Excel: " + e.getMessage(), e);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num) && !Double.isInfinite(num)) {
                    yield String.valueOf((long) num);
                } else {
                    yield String.valueOf(num);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    @Override
    @Transactional
    public Object importProducts(MultipartFile file, boolean confirm, boolean updateExisting) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please upload an Excel file.");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BadRequestException("Invalid file type. Please upload an Excel file.");
        }

        List<Map<String, Object>> rowsData = new ArrayList<>();
        List<String> headers = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                if (headers.isEmpty()) {
                    for (int c = 0; c < row.getLastCellNum(); c++) {
                        String h = getCellValueAsString(row.getCell(c));
                        headers.add(h != null ? h.trim().toLowerCase() : "");
                    }
                    continue;
                }

                Map<String, Object> rowMap = new HashMap<>();
                boolean hasContent = false;
                for (int c = 0; c < headers.size(); c++) {
                    String h = headers.get(c);
                    if (h.isEmpty()) continue;
                    String val = getCellValueAsString(row.getCell(c));
                    if (val != null && !val.isBlank()) {
                        hasContent = true;
                        rowMap.put(h, val);
                    }
                }
                if (hasContent) {
                    rowsData.add(rowMap);
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Could not parse Excel file: " + e.getMessage());
        }

        // Validation phase
        List<Map<String, Object>> results = new ArrayList<>();
        int validCount = 0;
        int warningCount = 0;
        int errorCount = 0;

        Map<String, Product> existingProducts = new HashMap<>();
        productRepository.findAll().forEach(p -> existingProducts.put(p.getSku().toUpperCase(), p));

        Map<String, Category> existingCategories = new HashMap<>();
        categoryRepository.findAll().forEach(c -> existingCategories.put(c.getName().toLowerCase(), c));

        Location defaultLocation = locationRepository.findFirstByOrderByIdAsc().orElse(null);

        List<Map<String, Object>> newProductsToCreate = new ArrayList<>();
        List<Map<String, Object>> productsToUpdate = new ArrayList<>();
        Set<String> categoriesToCreate = new HashSet<>();
        Set<String> seenSkus = new HashSet<>();

        int rowNum = 2;
        for (Map<String, Object> row : rowsData) {
            String status = "Valid";
            List<String> issues = new ArrayList<>();
            boolean isError = false;

            String sku = (String) row.get("sku");
            String name = (String) row.get("product name");
            String catName = (String) row.get("category");
            String qtyStr = (String) (row.containsKey("current stock") ? row.get("current stock") : row.get("quantity"));
            String priceStr = (String) row.get("unit price");

            if (sku == null || sku.isBlank()) {
                issues.add("Missing SKU");
                isError = true;
            } else {
                String cleanSku = sku.trim().toUpperCase();
                if (seenSkus.contains(cleanSku)) {
                    issues.add("Duplicate SKU in file");
                    isError = true;
                } else {
                    seenSkus.add(cleanSku);
                }
            }

            if (name == null || name.isBlank()) {
                issues.add("Missing Product Name");
                isError = true;
            }

            if (qtyStr != null) {
                try {
                    double q = Double.parseDouble(qtyStr);
                    if (q < 0) {
                        issues.add("Invalid quantity (negative)");
                        isError = true;
                    }
                } catch (NumberFormatException e) {
                    issues.add("Quantity must be numeric");
                    isError = true;
                }
            }

            if (priceStr != null) {
                try {
                    double p = Double.parseDouble(priceStr);
                    if (p < 0) {
                        issues.add("Invalid price (negative)");
                        isError = true;
                    }
                } catch (NumberFormatException e) {
                    issues.add("Unit Price must be numeric");
                    isError = true;
                }
            }

            if (!isError) {
                String cleanSku = sku.trim().toUpperCase();
                if (existingProducts.containsKey(cleanSku)) {
                    if (!updateExisting) {
                        issues.add("SKU exists (Skipping)");
                        status = "Warning";
                        warningCount++;
                    } else {
                        issues.add("SKU exists (Will Update)");
                        status = "Warning";
                        warningCount++;
                        productsToUpdate.add(row);
                    }
                } else {
                    validCount++;
                    newProductsToCreate.add(row);
                }

                if (catName != null && !catName.isBlank() && !existingCategories.containsKey(catName.trim().toLowerCase())) {
                    categoriesToCreate.add(catName.trim());
                }
            } else {
                errorCount++;
                status = "Error";
            }

            Map<String, Object> resItem = new HashMap<>();
            resItem.put("row_num", rowNum++);
            resItem.put("sku", sku != null ? sku : "");
            resItem.put("name", name != null ? name : "");
            resItem.put("status", status);
            resItem.put("issues", issues);
            results.add(resItem);
        }

        if (!confirm) {
            return new ExcelImportResponse(validCount, warningCount, errorCount, results);
        }

        if (errorCount > 0) {
            throw new BadRequestException("Cannot import file with critical errors. Please fix and re-upload.");
        }

        // Transactional import
        // 1. Create missing categories
        for (String cName : categoriesToCreate) {
            if (!existingCategories.containsKey(cName.toLowerCase())) {
                Category cat = new Category(cName);
                Category saved = categoryRepository.save(cat);
                existingCategories.put(cName.toLowerCase(), saved);
            }
        }

        // 2. Update existing
        if (updateExisting) {
            for (Map<String, Object> row : productsToUpdate) {
                String cleanSku = ((String) row.get("sku")).trim().toUpperCase();
                Product p = existingProducts.get(cleanSku);
                if (p != null) {
                    if (row.get("product name") != null) p.setName((String) row.get("product name"));
                    if (row.get("unit of measure") != null) p.setUom((String) row.get("unit of measure"));
                    if (row.get("reorder threshold") != null) {
                        try {
                            p.setReorderThreshold(new BigDecimal((String) row.get("reorder threshold")));
                        } catch (Exception ignored) {}
                    }
                    if (row.get("category") != null) {
                        Category cat = existingCategories.get(((String) row.get("category")).trim().toLowerCase());
                        if (cat != null) p.setCategory(cat);
                    }
                    if (row.get("image url") != null) {
                        p.setImageData((String) row.get("image url"));
                    }
                    productRepository.save(p);
                }
            }
        }

        // 3. Create new products
        for (Map<String, Object> row : newProductsToCreate) {
            String cleanSku = ((String) row.get("sku")).trim().toUpperCase();
            Category cat = null;
            if (row.get("category") != null) {
                cat = existingCategories.get(((String) row.get("category")).trim().toLowerCase());
            }

            Product p = new Product();
            p.setSku(cleanSku);
            p.setName((String) row.get("product name"));
            p.setUom(row.get("unit of measure") != null ? (String) row.get("unit of measure") : "pcs");
            p.setCategory(cat);

            if (row.get("reorder threshold") != null) {
                try {
                    p.setReorderThreshold(new BigDecimal((String) row.get("reorder threshold")));
                } catch (Exception ignored) {}
            }
            if (row.get("image url") != null) {
                p.setImageData((String) row.get("image url"));
            }

            Product savedProduct = productRepository.save(p);

            String qtyStr = (String) (row.containsKey("current stock") ? row.get("current stock") : row.get("quantity"));
            String priceStr = (String) row.get("unit price");
            if (qtyStr != null && defaultLocation != null) {
                try {
                    BigDecimal qty = new BigDecimal(qtyStr);
                    if (qty.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal price = priceStr != null ? new BigDecimal(priceStr) : BigDecimal.ZERO;
                        Stock stock = new Stock(savedProduct, defaultLocation, qty, price);
                        stockRepository.save(stock);
                    }
                } catch (Exception ignored) {}
            }
        }

        return Map.of("detail", String.format("Successfully imported %d new products and updated %d.",
                newProductsToCreate.size(), productsToUpdate.size()));
    }
}

