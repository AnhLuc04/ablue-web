package com.ablueit.ecommerce.service.impl;

import com.ablueit.ecommerce.payload.request.AttributeRequest;
import com.ablueit.ecommerce.payload.response.ProductCardResponse;
import com.ablueit.ecommerce.service.FileService;
import lombok.experimental.NonFinal;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    FileService fileService;

    @Override
    public ProductResponse addVariationProduct(ProductRequest request) throws IOException {
        Store store = getStoreById(request.getStoreId());

        Product product = Product.builder()
                .name(request.getName())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getRegularPrice())
                .store(store)
                .status(ProductStatus.PUBLISHED)
                .stockQuantity(request.getStockQuantity())
//                .stockStatus(StockStatus.valueOf(request.getStockStatus()))
                .regularPrice(request.getRegularPrice())
                .build();

        List<VariationRequest> variationFromRequest = request.getVariationsData();

        List<Variation> variations = variationFromRequest.stream().map(variationRequest -> {
            Variation variation = Variation.builder()
                    .stockQuantity(variationRequest.getStock())
                    .price(variationRequest.getPrice())
                    .build();

            List<AttributeRequest> attributeRequests = variationRequest.getAttributes();

            List<VariationAttribute> variationAttributes = attributeRequests.stream().map(attributeRequest -> {
                Attribute attribute = getAttributeByNameOrElseCreateNew(attributeRequest.getName());
                attributeRepository.save(attribute);

                AttributeTerm attributeTerm = getAttributeTermByNameOrElseCreateNew(attributeRequest.getTerm(), attribute);
                attributeTermRepository.save(attributeTerm);

                return VariationAttribute.builder()
                        .variation(variation)
                        .attributeTerm(attributeTerm)
                        .build();
            }).collect(Collectors.toCollection(ArrayList::new));

            variation.setAttributes(variationAttributes);
            variation.setProduct(product);
            return variation;
        }).collect(Collectors.toCollection(ArrayList::new));

        product.setUpVariations(variations);

        fileService.upload(request.getPrimaryImage(), product, ImageType.PRIMARY);

        if (!request.getSizeGuideImage().isEmpty()) {
            fileService.upload(request.getSizeGuideImage(), product, ImageType.SIZE_GUIDE);
        }

        if (!request.getGalleryImages().isEmpty()) {
            request.getGalleryImages().forEach(x -> {
                try {
                    fileService.upload(x, product, ImageType.DEFAULT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Categories categories = categoriesRepository
                .findById(request.getCategoryId()).orElseThrow();


        product.setCategories(new ArrayList<>(List.of(categories)));

        productRepository.save(product);

        log.info("product={}", product.getName());

        return ProductResponse.builder()
                .productName(product.getName())
                .build();
    }


    @Override
    public ProductRequest mapToRequest(Product product) {
        return null;
    }

    @Override
    public String updateVariationProduct(ProductRequest request) throws IOException {
        return null;
    }

    @Override
    public ProductResponse getProduct(Long id) {
        log.info("getProduct={}", id);

        Product product = getProductById(id);

        List<VariationResponse> variationResponses = product.getVariations().stream().map(x -> {
            List<AttributeResponse> attributeResponses = x.getAttributes().stream()
                    .map(variationAttribute -> AttributeResponse.builder()
                            .name(variationAttribute.getAttributeTerm().getAttribute().getName())
                            .term(variationAttribute.getAttributeTerm().getName())
                            .build()).toList();
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
                .category(product.getCategories().stream().findFirst().get().getName())
                .sku(product.getSku())
                .variationsData(variationResponses)
//                .stockQuantity(product.getStockQuantity().longValue())
//                .stockStatus(Objects.isNull(product.getStockStatus().name()) ? null : product.getStockStatus().name())
//                .backorders(product.getBackOrderAllowed() ? "yes" : "no")
                .build();
    }

    @Override
    public List<ProductCardResponse> getProductCardByCategory(Long categoryId, Long currentProductId) {
        Categories categories = getCategoryById(categoryId);

        List<Product> products = productRepository.findRelatedProductsByCategoryId(categoryId, currentProductId, 10);


        return products.stream().map(x -> {

           ProductImage primaryImage = productImageRepository.findByImageTypeAndProduct(ImageType.PRIMARY, x);

            return ProductCardResponse
                    .builder()
                    .id(x.getId())
                    .name(x.getName())
                    .price(x.getPrice())
                    .primaryImage(primaryImage.getUrl())
                    .totalSold(100L)
                    .rating(4.5)
                    .build();
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Page<Product> searchProducts(String keyword, String category, Double maxPrice, String sort, Pageable pageable) {
        return productRepository.searchProducts(keyword, category, maxPrice, pageable);
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

    Categories getCategoryById(Long id) {
        log.info("getCategoryById={}", id);

        return categoriesRepository.findById(id)
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