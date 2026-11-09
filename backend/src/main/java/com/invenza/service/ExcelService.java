package com.invenza.service;

import com.invenza.dto.product.ExcelImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;

public interface ExcelService {
    ByteArrayInputStream generateTemplate();
    ByteArrayInputStream exportProducts(String search, UUID categoryId);
    Object importProducts(MultipartFile file, boolean confirm, boolean updateExisting);
}

