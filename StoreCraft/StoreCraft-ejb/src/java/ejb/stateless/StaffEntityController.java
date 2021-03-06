/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.stateless;

import entity.StaffEntity;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.StaffTypeEnum;
import util.exception.CreateNewProductException;
import util.exception.CreateNewStaffException;
import util.exception.DeleteStaffException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.UpdateStaffException;
import util.security.CryptographicHelper;

/**
 *
 * @author shawn
 */
@Stateless
@Local(StaffEntityControllerLocal.class)
public class StaffEntityController implements StaffEntityControllerLocal {

    @PersistenceContext(unitName = "StoreCraft-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public StaffEntityController() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public StaffEntity createNewStaff(StaffEntity newStaffEntity) throws InputDataValidationException, CreateNewStaffException {
        Set<ConstraintViolation<StaffEntity>> constraintViolations = validator.validate(newStaffEntity);

        if (newStaffEntity.getStaffTypeEnum() == null) {
            throw new CreateNewStaffException("The staff created must have a staff type!");
        }

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newStaffEntity);
                entityManager.flush();

                return newStaffEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewStaffException("Staff with same username already exist");
                } else {
                    throw new CreateNewStaffException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewStaffException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<StaffEntity> retrieveAllStaffs() {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s");

        return query.getResultList();
    }

    @Override
    public StaffEntity retrieveStaffByStaffId(Long staffId) throws StaffNotFoundException {
        if (staffId == null) {
            throw new StaffNotFoundException("Staff ID not provided");
        }

        StaffEntity staffEntity = entityManager.find(StaffEntity.class, staffId);

        if (staffEntity != null) {
            return staffEntity;
        } else {
            throw new StaffNotFoundException("Staff ID " + staffId + " does not exist!");
        }
    }

    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (StaffEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }

    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            StaffEntity staffEntity = retrieveStaffByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + staffEntity.getSalt()));

            if (staffEntity.getPassword().equals(passwordHash)) {
                return staffEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (StaffNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void deleteStaff(Long staffId) throws StaffNotFoundException, DeleteStaffException {
        StaffEntity staffEntityToRemove = retrieveStaffByStaffId(staffId);
        System.err.println("staffId" + staffId);
        if (staffEntityToRemove.getCommunityGoalEntities().isEmpty()) {
            System.err.println("inside staffEntityToRemove");
            entityManager.remove(staffEntityToRemove);
        } else {
            throw new DeleteStaffException("Staff ID " + staffId + " is associated with existing community goal(s) and cannot be deleted!");
        }
    }

    @Override
    public void updateStaff(StaffEntity staffEntity) throws InputDataValidationException, StaffNotFoundException, UpdateStaffException {
        Set<ConstraintViolation<StaffEntity>> constraintViolations = validator.validate(staffEntity);

        List<StaffEntity> allStaffs = retrieveAllStaffs();
        for (StaffEntity se : allStaffs) {
            if (staffEntity.getUsername().equals(se.getUsername())) {
                throw new UpdateStaffException("Staff with same username already exist");
            }
        }

        if (constraintViolations.isEmpty()) {
            if (staffEntity.getStaffId() != null) {
                StaffEntity staffEntityToUpdate = retrieveStaffByStaffId(staffEntity.getStaffId());

                if (staffEntityToUpdate.getStaffId().equals(staffEntity.getStaffId())) {
                    staffEntityToUpdate.setFirstName(staffEntity.getFirstName());
                    staffEntityToUpdate.setLastName(staffEntity.getLastName());
                    staffEntityToUpdate.setUsername(staffEntity.getUsername());
                    staffEntityToUpdate.setProfilePicUrl(staffEntity.getProfilePicUrl());

                } else {
                    throw new UpdateStaffException("Username of staff record to be updated does not match the existing record");
                }
            } else {
                throw new StaffNotFoundException("Staff ID not provided for staff to be updated");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public void updatePassword(StaffEntity staffEntity, String oldPasword, String newPassword) throws StaffNotFoundException, InvalidLoginCredentialException {
        if (staffEntity.getStaffId() != null) {
            // Persistent context
            StaffEntity staffEntityToUpdate = retrieveStaffByStaffId(staffEntity.getStaffId());

            String oldPasswordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPasword + staffEntity.getSalt()));

            if (staffEntity.getPassword().equals(oldPasswordHash)) {
                staffEntityToUpdate.setSalt(CryptographicHelper.getInstance().generateRandomString(32));
                staffEntityToUpdate.setPassword(newPassword);
            } else {
                throw new InvalidLoginCredentialException("The old password is incorrect!");
            }
        } else {
            throw new StaffNotFoundException("Staff ID not provided for staff to update password");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<StaffEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
