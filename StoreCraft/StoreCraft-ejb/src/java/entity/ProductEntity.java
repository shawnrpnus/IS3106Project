/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author shawn
 */
@Entity
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    
    @Column(nullable = false, unique = true, length = 7)
    @NotNull
    @Size(min = 7, max = 7)
    private String skuCode;
    
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String name;
    
   
    @Column(columnDefinition = "VARCHAR(1337)")
    @Size(max = 1337)
    private String description;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer quantityOnHand;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer reorderQuantity;
    
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal unitPrice;
    
    @NotNull
    private Boolean isScavengerHuntPrize;
    
    private String productImageUrl;
    
    //RELATIONSHIPS
    
    @OneToMany(mappedBy = "productEntity")
    private List<ReviewEntity> reviewEntities;
    
    @ManyToMany
    private List<TagEntity> tagEntities;
    
    @ManyToOne (optional = false)
    @JoinColumn (nullable = false)
    private CategoryEntity categoryEntity;
    
    @ManyToMany
    private List<DiscountCodeEntity> discountCodeEntities;

    public ProductEntity() {
        this.isScavengerHuntPrize = false;
        this.tagEntities = new ArrayList<>();
        this.discountCodeEntities = new ArrayList<>();
        this.reviewEntities = new ArrayList<>();
    }

    public ProductEntity(String skuCode, String name, String description, Integer quantityOnHand, Integer reorderQuantity, BigDecimal unitPrice, String productImageUrl) {
        this();
        this.skuCode = skuCode;
        this.name = name;
        this.description = description;
        this.quantityOnHand = quantityOnHand;
        this.reorderQuantity = reorderQuantity;
        this.unitPrice = unitPrice;
        this.productImageUrl = productImageUrl;
    }
    
    

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the productId fields are not set
        if (!(object instanceof ProductEntity)) {
            return false;
        }
        ProductEntity other = (ProductEntity) object;
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProductEntity[ id=" + productId + " ]";
    }

    public Boolean getIsScavengerHuntPrize() {
        return isScavengerHuntPrize;
    }

    public void setIsScavengerHuntPrize(Boolean isScavengerHuntPrize) {
        this.isScavengerHuntPrize = isScavengerHuntPrize;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        System.out.println("Set Description");
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<ReviewEntity> getReviewEntities() {
        return reviewEntities;
    }

    public void setReviewEntities(List<ReviewEntity> reviewEntities) {
        this.reviewEntities = reviewEntities;
    }

    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    public void setTagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        if(this.categoryEntity != null)
        {
            if(this.categoryEntity.getProductEntities().contains(this))
            {
                this.categoryEntity.getProductEntities().remove(this);
            }
        }
        
        this.categoryEntity = categoryEntity;
        
        if(this.categoryEntity != null)
        {
            if(!this.categoryEntity.getProductEntities().contains(this))
            {
                this.categoryEntity.getProductEntities().add(this);
            }
        }
    }
    
    public void addTag(TagEntity tagEntity)
    {
        if(tagEntity != null)
        {
            if(!this.tagEntities.contains(tagEntity))
            {
                this.tagEntities.add(tagEntity);
                
                if(!tagEntity.getProductEntities().contains(this))
                {                    
                    tagEntity.getProductEntities().add(this);
                }
            }
        }
    }
    
    public void removeTag(TagEntity tagEntity)
    {
        if(tagEntity != null)
        {
            if(this.tagEntities.contains(tagEntity))
            {
                this.tagEntities.remove(tagEntity);
                
                if(tagEntity.getProductEntities().contains(this))
                {
                    tagEntity.getProductEntities().remove(this);
                }
            }
        }
    }

    /**
     * @return the discountCodeEntities
     */
    public List<DiscountCodeEntity> getDiscountCodeEntities() {
        return discountCodeEntities;
    }

    /**
     * @param discountCodeEntities the discountCodeEntities to set
     */
    public void setDiscountCodeEntities(List<DiscountCodeEntity> discountCodeEntities) {
        this.discountCodeEntities = discountCodeEntities;
    }

    /**
     * @return the productImageUrl
     */
    public String getProductImageUrl() {
        return productImageUrl;
    }

    /**
     * @param productImageUrl the productImageUrl to set
     */
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
}
