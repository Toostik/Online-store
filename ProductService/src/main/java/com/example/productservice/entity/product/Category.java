package com.example.productservice.entity.product;

import com.example.productservice.dto.category.CategoryDto;
import com.example.productservice.dto.category.CategoryTreeDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
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

    public Category(Long id, String name, String imagePath, Category parent) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.parent = parent;
    }

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
                parent != null ? parent.getId() : null
        );
    }
}
