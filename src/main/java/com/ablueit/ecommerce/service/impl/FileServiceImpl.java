package com.ablueit.ecommerce.service.impl;


import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.model.ProductImage;
import com.ablueit.ecommerce.payload.response.FileInfo;
import com.ablueit.ecommerce.payload.response.FileResponse;
import com.ablueit.ecommerce.repository.FileRepository;
import com.ablueit.ecommerce.repository.ProductImageRepository;
import com.ablueit.ecommerce.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceImpl implements FileService {

    FileRepository fileRepository;
    ProductImageRepository productImageRepository;

    @Override
    public FileResponse upload(MultipartFile file, Product product, ImageType imageType) throws IOException {
        // Store file
        var fileInfo = fileRepository.store(file);

        // Create file management info
        var fileMgmt = ProductImage.builder()
                .imageType(imageType)
                .fileName(fileInfo.getName())
                .product(product)
                .url(fileInfo.getUrl())
                .build();

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        productImageRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

}

