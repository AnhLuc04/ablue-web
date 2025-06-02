//package com.ablueit.ecommerce.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "product_variants")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProductVariant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String sku;
//    private double price;
//    private double salePrice;
//
//    @ManyToOne
//    @JoinColumn(name = "parent_id")
//    private Product parent;
//
//    @ElementCollection
//    @CollectionTable(name = "variant_attributes", joinColumns = @JoinColumn(name = "variant_id"))
//    private List<VariantAttribute> attributes = new ArrayList<>();
//
//    // Getters & Setters
//}
