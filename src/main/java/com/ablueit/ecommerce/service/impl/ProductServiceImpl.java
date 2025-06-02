package com.ablueit.ecommerce.service.impl;
import net.coobird.thumbnailator.Thumbnails;
import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.enums.ProductStatus;
import com.ablueit.ecommerce.enums.StockStatus;
import com.ablueit.ecommerce.exception.ResourceNotFoundException;
import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.request.ProductRequest;
import com.ablueit.ecommerce.payload.request.VariationRequest;
import com.ablueit.ecommerce.payload.response.AttributeResponse;
import com.ablueit.ecommerce.payload.response.ProductResponse;
import com.ablueit.ecommerce.payload.response.VariationResponse;
import com.ablueit.ecommerce.repository.*;
import com.ablueit.ecommerce.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    CategoriesRepository categoriesRepository;
    AttributeRepository attributeRepository;
    AttributeTermRepository attributeTermRepository;
    ProductRepository productRepository;
    StoreRepository storeRepository;
    VariationRepository variationRepository;
    ProductImageRepository productImageRepository;



    @Override
    public String addVariationProduct(ProductRequest request) throws IOException {
        Store store = getStoreById(request.storeId());

        Product product = Product.builder()
                .name(request.name())
                .shortDescription(request.shortDescription())
                .description(request.description())
                .sku(request.sku())
                .price(request.regularPrice())
                .store(store)
                .status(ProductStatus.PUBLISHED)
                .stockQuantity(request.stockQuantity())
                .stockStatus(StockStatus.valueOf(request.stockStatus()))
                .regularPrice(request.regularPrice())
                .build();

        List<VariationRequest> variationFromRequest = request.variationsData();

        List<Variation> variations = variationFromRequest.stream().map(variationRequest -> {
            Variation variation = Variation.builder()
                    .stockQuantity(variationRequest.stock())
                    .price(variationRequest.price())
                    .build();

            List<VariationRequest.AttributeRequest> attributeRequests = variationRequest.attributes();

            List<VariationAttribute> variationAttributes = attributeRequests.stream().map(attributeRequest -> {
                Attribute attribute = getAttributeByNameOrElseCreateNew(attributeRequest.name());
                attributeRepository.save(attribute);

                AttributeTerm attributeTerm = getAttributeTermByNameOrElseCreateNew(attributeRequest.term(), attribute);
                attributeTermRepository.save(attributeTerm);

                return VariationAttribute.builder()
                        .variation(variation)
                        .attributeTerm(attributeTerm)
                        .build();
            }).toList();

            variation.setAttributes(variationAttributes);
            variation.setProduct(product);
            return variation;
        }).toList();

        Categories categories = getCategoryByName(request.category());

        // ✅ Xử lý ảnh mới bằng MultipartFile
        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(request.primaryImage());
        allImages.add(request.sizeGuideImage());
        allImages.addAll(request.galleryImages());

        List<ImageType> imageTypes = new ArrayList<>();
        imageTypes.add(ImageType.PRIMARY);
        imageTypes.add(ImageType.SIZE_GUIDE);
        for (int i = 0; i < request.galleryImages().size(); i++) {
            imageTypes.add(ImageType.DEFAULT);
        }

        List<ProductImage> productImages = saveImageFiles(request.sku(), allImages, imageTypes, product);

        // ✅ Liên kết và lưu
        product.setCategories(List.of(categories));
        product.setProductImages(productImages);
        product.setVariations(variations);
        productRepository.save(product);

        log.info("for debug");
        return "ok";
    }
    public List<ProductImage> saveImageFiles(String sku, List<MultipartFile> files, List<ImageType> types, Product product) throws IOException {
        List<ProductImage> productImages = new ArrayList<>();
        Path uploadDir = Paths.get("src/main/resources/static/images/");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            ImageType type = types.get(i);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) continue;

            String baseFilename = sku + "_" + type.name().toLowerCase();
            String fileExtension = ".png";
            String uniqueFilename = baseFilename + fileExtension;

            File destinationFile = new File(uploadDir.toFile(), uniqueFilename);
            int counter = 0;

            while (destinationFile.exists()) {
                counter++;
                if (counter == 1) {
                    uniqueFilename = baseFilename + "+" + fileExtension;
                } else {
                    uniqueFilename = baseFilename + "+" + counter + fileExtension;
                }
                destinationFile = new File(uploadDir.toFile(), uniqueFilename);
            }

            Thumbnails.of(file.getInputStream())
                    .outputFormat("png")
                    .toFile(destinationFile);

            String imageUrl = "src/main/resources/static/images/" + uniqueFilename;

            ProductImage productImage = ProductImage.builder()
                    .url(imageUrl)
                    .imageType(type)
                    .product(product)
                    .build();

            productImages.add(productImage);
        }

        return productImages;
    }


    @Override
    public ProductResponse getProduct(Long id) {
        log.info("getProduct={}", id);

        Product product = getProductById(id);

        List<VariationResponse> variationResponses = product.getVariations().stream().map(x -> {
            List<AttributeResponse> attributeResponses = x.getAttributes().stream()
                    .map(variationAttribute -> {
                        return AttributeResponse.builder()
                                .name(variationAttribute.getAttributeTerm().getAttribute().getName())
                                .term(variationAttribute.getAttributeTerm().getName())
                                .build();
                    }).toList();
            return VariationResponse.builder()
                    .attributes(attributeResponses)
                    .price(x.getPrice())
                    .stock(Long.valueOf(x.getStockQuantity()))
                    .build();
        }).toList();

        ProductImage primaryImage = productImageRepository.findByProductAndImageType(product, ImageType.PRIMARY);
        ProductImage sizeGuide = productImageRepository.findByProductAndImageType(product, ImageType.SIZE_GUIDE);
        List<ProductImage> galleryImages = productImageRepository.findAllByProductAndImageType(product, ImageType.DEFAULT);

        return ProductResponse.builder()
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productShortDescription(product.getShortDescription())
                .regularPrice(product.getRegularPrice())
                .salePrice(product.getSalePrice())
                .primaryImage(primaryImage.getUrl())
                .sizeGuideImage(sizeGuide.getUrl())
                .galleryImages(galleryImages.stream().map(ProductImage::getUrl).toList())
//                .category(product.getCategories().)
                .sku(product.getSku())
                .variationsData(variationResponses)
//                .stockQuantity(product.getStockQuantity().longValue())
                .stockStatus(product.getStockStatus().name())
//                .backorders(product.getBackOrderAllowed() ? "yes" : "no")
                .build();
    }

    Product getProductById(Long id) {
        log.info("getProductById={}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found"));
    }


    Categories getCategoryByName(String name) {
        log.info("getCategoryByName={}", name);

        return categoriesRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("category not found"));
    }

    AttributeTerm getAttributeTermByNameOrElseCreateNew(String name, Attribute attribute) {
        log.info("getAttributeTermByNameOrElseCreateNew={}", name);

        return attributeTermRepository.findByNameAndAttribute(name, attribute).orElseGet(() -> AttributeTerm.builder()
                .name(name)
                .attribute(attribute)
                .build());
    }

    Attribute getAttributeByNameOrElseCreateNew(String name) {
        log.info("getAttributeByNameOrElseCreateNew={}", name);

        return attributeRepository.findByName(name).orElseGet(() -> Attribute.builder()
                .name(name)
                .build());
    }

    List<Attribute> getAllAttributeByListId(List<Long> id) {
        log.info("getAllAttributeByListId={}", id.toString());

        return attributeRepository.findAllById(id);
    }

    List<AttributeTerm> getAllAttributeTermByListId(List<Long> id) {
        log.info("getAllAttributeTermByListId={}", id.toString());

        return attributeTermRepository.findAllById(id);
    }

    Store getStoreById(Long id) {
        log.info("getStoreById={}", id);

        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("store not found"));
    }
}