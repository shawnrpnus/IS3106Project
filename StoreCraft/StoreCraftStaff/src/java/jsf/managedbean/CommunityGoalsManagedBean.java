/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.stateless.CommunityGoalEntityControllerLocal;
import entity.CommunityGoalEntity;
import entity.StaffEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import util.exception.CommunityGoalNotFoundException;
import util.exception.CreateNewCommunityGoalException;
import util.exception.InputDataValidationException;
import util.exception.StaffNotFoundException;

/**
 *
 * @author bryan
 */
@Named(value = "communityGoalsManagedBean")
@ViewScoped
public class CommunityGoalsManagedBean implements Serializable{

    @EJB
    private CommunityGoalEntityControllerLocal communityGoalEntityControllerLocal;

    //For Initial Load
    private List<CommunityGoalEntity> communityGoals;
    //****************
    
    //For Search All Fields
    private List<CommunityGoalEntity> filteredCommunityGoals;
    //****************
    
    //For Creating
    private CommunityGoalEntity newCommunityGoal;
    //************
    
    //For Updating
    private boolean isUpdating;
    private CommunityGoalEntity selectedCommunityGoal;
    //***********
    
    
    private List<String> countries;
    private Date currentDate;
    private Date afterStartDate;
    
    public CommunityGoalsManagedBean() {
        countries= new ArrayList<>();
        countries.add("Singapore");
        countries.add("China");
        countries.add("India");
        countries.add("Malaysia");
        countries.add("Frozen Throne");
        currentDate = new Date();
        afterStartDate = new Date();
        newCommunityGoal = new CommunityGoalEntity();
    }
    
    @PostConstruct
    public void PostConstruct(){
        communityGoals = communityGoalEntityControllerLocal.retrieveAllCommunityGoals();
    }
    
    public void updating(ActionEvent event){
        setIsUpdating(true);
    }
    
    public void cancelUpdating(ActionEvent event){
        setIsUpdating(false);
        try { //to reset fields in the dialog
            selectedCommunityGoal = communityGoalEntityControllerLocal.retrieveCommunityGoalByCommunityGoalId(selectedCommunityGoal.getCommunityGoalId());
        } catch (CommunityGoalNotFoundException ex) {
            Logger.getLogger(FilterProductsByCategoryManagedBean.class.getName()).log(Level.SEVERE, null, ex); //exception should not occur
        }
    }
    
    
    public void closeViewDialog(CloseEvent event){
        selectedCommunityGoal = new CommunityGoalEntity();
        currentDate = new Date();
        afterStartDate = new Date();
    }
    
    public void closeCreateDialog(CloseEvent event){
        newCommunityGoal = new CommunityGoalEntity();
        currentDate = new Date();
        afterStartDate = new Date();
    }
    //for update
    public void afterDate(SelectEvent event){
        
        afterStartDate = selectedCommunityGoal.getStartDate();
        Calendar c = Calendar.getInstance();
        
        c.setTime(afterStartDate);


        c.add(Calendar.DAY_OF_YEAR, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);

        afterStartDate = c.getTime();

    }
    //for create
    public void afterCreateDate(SelectEvent event){
        afterStartDate = newCommunityGoal.getStartDate();
        Calendar c = Calendar.getInstance();

        c.setTime(afterStartDate);
       
        c.add(Calendar.DAY_OF_YEAR, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);

        afterStartDate = c.getTime();
    }
    
    public Date yesterday(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, -1);
        
        return c.getTime();
    }
    
    public void updateCommunityGoal(ActionEvent event){
        try {
            System.out.println("community goal " + selectedCommunityGoal.getCountry());
            communityGoalEntityControllerLocal.updateCommunityGoal(selectedCommunityGoal, selectedCommunityGoal.getCommunityGoalId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Community Goal updated successfully", null));
            cancelUpdating(null);
        } catch (InputDataValidationException | CommunityGoalNotFoundException ex) {
              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating community goal: " + ex.getMessage(), null));
        }  
    }
    
    public void createCommunityGoal(ActionEvent event){
        
        StaffEntity staff = (StaffEntity)  FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentStaffEntity");
        try {
            communityGoalEntityControllerLocal.createNewCommunityGoal(newCommunityGoal, staff.getStaffId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Community Goal Successfully Created", null));
            this.communityGoals = communityGoalEntityControllerLocal.retrieveAllCommunityGoals();
        } catch (InputDataValidationException | StaffNotFoundException | CreateNewCommunityGoalException ex) {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating community goal: " + ex.getMessage(), null));
        }
    }
    
    public void deleteCommunityGoal(ActionEvent event){
        try {
            communityGoalEntityControllerLocal.deleteCommunityGoal(selectedCommunityGoal.getCommunityGoalId());
            communityGoals.remove(selectedCommunityGoal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Community Goal Successfully Deleted", null));
        } catch (CommunityGoalNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting community goal: " + ex.getMessage(), null));
        }
    }
    
    public void creating(ActionEvent event){    
        this.newCommunityGoal = new CommunityGoalEntity();
    }

    public List<CommunityGoalEntity> getCommunityGoals() {
        return communityGoals;
    }

    public void setCommunityGoals(List<CommunityGoalEntity> communityGoals) {
        this.communityGoals = communityGoals;
    }

    public CommunityGoalEntity getNewCommunityGoal() {
        return newCommunityGoal;
    }

    public void setNewCommunityGoal(CommunityGoalEntity newCommunityGoal) {
        this.newCommunityGoal = newCommunityGoal;
    }

    public CommunityGoalEntity getSelectedCommunityGoal() {
        return selectedCommunityGoal;
    }

    public void setSelectedCommunityGoal(CommunityGoalEntity selectedCommunityGoal) {
        this.selectedCommunityGoal = selectedCommunityGoal;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getAfterStartDate() {
        return afterStartDate;
    }

    public void setAfterStartDate(Date afterStartDate) {
        this.afterStartDate = afterStartDate;
    }

    public List<CommunityGoalEntity> getFilteredCommunityGoals() {
        return filteredCommunityGoals;
    }

    public void setFilteredCommunityGoals(List<CommunityGoalEntity> filteredCommunityGoals) {
        this.filteredCommunityGoals = filteredCommunityGoals;
    }

    public boolean isIsUpdating() {
        return isUpdating;
    }

    public void setIsUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }
}
