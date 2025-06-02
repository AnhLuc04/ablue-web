package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "variation_attribute")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariationAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private Variation variation;

    @ManyToOne
    @JoinColumn(name = "attribute_term_id")
    private AttributeTerm attributeTerm;
}
