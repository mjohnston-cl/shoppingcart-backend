package com.corelogic.sc.db.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
@ToString(exclude = {"productCategory", "createdDate"})
public class Product implements Serializable {
    @Id
    @Column(name = "sku_number")
    private String skuNumber;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "inventory_count")
    private Integer inventoryCount;

    @Column(name = "price")
    private Double price;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_category_name", nullable = false)
    private ProductCategory productCategory;

    @JsonBackReference
    @OneToOne(mappedBy = "product",
            cascade = CascadeType.ALL)
    private Item item;
}