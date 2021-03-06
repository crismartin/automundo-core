package es.upm.miw.tfm.automundo.infrastructure.mongodb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.tfm.automundo.domain.model.Customer;
import es.upm.miw.tfm.automundo.domain.model.Vehicle;
import es.upm.miw.tfm.automundo.domain.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class VehicleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String bin;
    @Indexed(unique = true)
    private String reference;
    private String plate;
    private String model;
    private Integer yearRelease;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime registerDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastViewDate;
    @DBRef(lazy = true)
    private VehicleTypeEntity vehicleType;
    private String typeNumber;
    @DBRef
    private CustomerEntity customer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime leaveDate;

    public VehicleEntity(Vehicle vehicle){
        BeanUtils.copyProperties(vehicle, this);

        if(vehicle.getVehicleType() != null){
            vehicleType = new VehicleTypeEntity(vehicle.getVehicleType());
        }

        if(vehicle.getCustomer() != null){
            customer = new CustomerEntity(vehicle.getCustomer());
        }
    }

    public Vehicle toVehicle(){
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(this, vehicle);

        vehicle.setCustomer(getCustomerFromEntity());
        vehicle.setVehicleType(getVehicleTypeFromEntity());

        return vehicle;
    }

    private Customer getCustomerFromEntity(){
        return (this.customer != null) ? this.customer.toCustomer() : null;
    }

    private VehicleType getVehicleTypeFromEntity(){
        return (this.vehicleType != null ) ? this.vehicleType.toVehicleType() : null;
    }

    public String getIdCustomer(){
        return this.customer != null ? this.customer.getId() : null;
    }

    public void setFieldsCreation(){
        setLastViewDate(LocalDateTime.now());
        setRegisterDate(LocalDateTime.now());
        setReference(UUID.randomUUID().toString());
    }
}
