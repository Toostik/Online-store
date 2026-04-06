package com.example.productservice.entity;

import com.example.productservice.dto.CategoryDto;
import com.example.productservice.dto.CategoryTreeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public CategoryTreeDto toTreeDto() {
        return new CategoryTreeDto(
                id,
                name,
                imagePath,
                children.stream().map(Category::toTreeDto).toList()
        );
    }

    public CategoryDto toDto() {
        return new CategoryDto(
                id,
                name,
                imagePath,
                children.stream().map(Category::toDto).toList(),
                products.stream().map(Product::toDto).toList()
        );
    }
}
