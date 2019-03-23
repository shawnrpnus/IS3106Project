/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author shawn
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class DiscountCodeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long discountCodeId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    protected Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    protected Date endDate; 
    
    @Min(1)
    @NotNull
    protected Integer numAvailable;
    
    @Column(unique = true, nullable = false, length = 6)
    @Size(max = 6)
    protected String discountCode;
    
    //RELATIONSHIPS
    
    @ManyToMany(mappedBy = "discountCodeEntities")
    private List<CustomerEntity> customerEntities;
    
    @ManyToMany(mappedBy = "discountCodeEntities")
    private List<ProductEntity> productEntities;
   
    public DiscountCodeEntity() {
        this.customerEntities = new ArrayList<>();
        this.productEntities = new ArrayList<>();
    }

    public DiscountCodeEntity(Date startDate, Date endDate, Integer numAvailable, String discountCode) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.numAvailable = numAvailable;
        this.discountCode = discountCode;
    }
    
    public Long getDiscountCodeId() {
        return discountCodeId;
    }

    public void setDiscountCodeId(Long discountCodeId) {
        this.discountCodeId = discountCodeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (discountCodeId != null ? discountCodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the discountCodeId fields are not set
        if (!(object instanceof DiscountCodeEntity)) {
            return false;
        }
        DiscountCodeEntity other = (DiscountCodeEntity) object;
        if ((this.discountCodeId == null && other.discountCodeId != null) || (this.discountCodeId != null && !this.discountCodeId.equals(other.discountCodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DiscountCodeEntity[ id=" + discountCodeId + " ]";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    public List<CustomerEntity> getCustomerEntities() {
        return customerEntities;
    }

    public void setCustomerEntities(List<CustomerEntity> customerEntities) {
        this.customerEntities = customerEntities;
    }

    public Integer getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(Integer numAvailable) {
        this.numAvailable = numAvailable;
    }

    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }
    
    public void addCustomer(CustomerEntity customerEntity)
    {
        if(customerEntity != null)
        {
            if(!this.customerEntities.contains(customerEntity))
            {
                this.customerEntities.add(customerEntity);
                
                if(!customerEntity.getDiscountCodeEntities().contains(this))
                {                    
                    customerEntity.getDiscountCodeEntities().add(this);
                }
            }
        }
    }
    
    public void addProduct(ProductEntity productEntity)
    {
        if(productEntity != null)
        {
            if(!this.productEntities.contains(productEntity))
            {
                this.productEntities.add(productEntity);
                
                if(!productEntity.getDiscountCodeEntities().contains(this))
                {                    
                    productEntity.getDiscountCodeEntities().add(this);
                }
            }
        }
    }
    
}
