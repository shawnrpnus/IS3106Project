/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.ws.rest;

import entity.CustomerEntity;

/**
 *
 * @author Win Phong
 */
public class CustomerRegisterRsp {
    
    private CustomerEntity customerEntity;
    
    public CustomerRegisterRsp() {
    }
    
    public CustomerRegisterRsp(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    } 
}
