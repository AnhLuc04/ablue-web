//package com.ablueit.ecommerce.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "variants")
//public class Variant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String type;  // Loại biến thể (Màu sắc, Kích thước)
//    private String value; // Giá trị biến thể (Red, Blue, Large)
//    private Double price;
//    private Double listPrice;
//    private Integer quantity;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    // Getters và Setters
//}
