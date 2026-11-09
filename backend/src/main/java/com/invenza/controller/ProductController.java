package com.invenza.controller;

import com.invenza.dto.product.*;
import com.invenza.service.ExcelService;
import com.invenza.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {

    private final ProductService productService;
    private final ExcelService excelService;

    public ProductController(ProductService productService, ExcelService excelService) {
        this.productService = productService;
        this.excelService = excelService;
    }

    // ── Categories ─────────────────────────────────────────────────────────────

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        return ResponseEntity.ok(productService.listCategories());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse created = productService.createCategory(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ── Products ───────────────────────────────────────────────────────────────

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(name = "category_id", required = false) UUID categoryId) {
        return ResponseEntity.ok(productService.listProducts(search, categoryId));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse created = productService.createProduct(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/products/template")
    public ResponseEntity<InputStreamResource> getExcelTemplate() {
        ByteArrayInputStream in = excelService.generateTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"invenza_inventory_template.xlsx\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/products/export/excel")
    public ResponseEntity<InputStreamResource> exportProductsExcel(
            @RequestParam(required = false) String search,
            @RequestParam(name = "category_id", required = false) UUID categoryId) {
        ByteArrayInputStream in = excelService.exportProducts(search, categoryId);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"warehouse_inventory_%s.xlsx\"", dateStr));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PostMapping(value = "/products/import/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<Object> importProductsExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "confirm", defaultValue = "false") boolean confirm,
            @RequestParam(value = "update_existing", defaultValue = "true") boolean updateExisting) {
        Object result = excelService.importProducts(file, confirm, updateExisting);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id,
                                                        @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

