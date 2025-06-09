package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    FileResponse upload(MultipartFile file, Product product, ImageType imageType) throws IOException;

}
