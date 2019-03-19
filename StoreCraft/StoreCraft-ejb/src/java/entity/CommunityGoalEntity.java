/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 *
 * @author shawn
 */
@Entity
public class CommunityGoalEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityGoalId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date endDate; 
    
    @NotNull
    @DecimalMin("1000.00")
    private BigDecimal targetPoints;
    
    @NotNull
    private String country;
    
    //RELATIONSHIPS

    @ManyToOne (optional = false)
    @JoinColumn (nullable = false)
    private StaffEntity staffEntity;
    
    
    public CommunityGoalEntity() {
    }

    public CommunityGoalEntity(Date startDate, Date endDate, BigDecimal targetPoints, String country) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetPoints = targetPoints;
        this.country = country;
    }

    public Long getCommunityGoalId() {
        return communityGoalId;
    }

    public void setCommunityGoalId(Long communityGoalId) {
        this.communityGoalId = communityGoalId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (communityGoalId != null ? communityGoalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the communityGoalId fields are not set
        if (!(object instanceof CommunityGoalEntity)) {
            return false;
        }
        CommunityGoalEntity other = (CommunityGoalEntity) object;
        if ((this.communityGoalId == null && other.communityGoalId != null) || (this.communityGoalId != null && !this.communityGoalId.equals(other.communityGoalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CommunityGoalEntity[ id=" + communityGoalId + " ]";
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

    public BigDecimal getTargetPoints() {
        return targetPoints;
    }

    public void setTargetPoints(BigDecimal targetPoints) {
        this.targetPoints = targetPoints;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public StaffEntity getStaffEntity() {
        return staffEntity;
    }
    
    public void setStaffEntity(StaffEntity staffEntity) {
        this.staffEntity = staffEntity;
    }
    
}
