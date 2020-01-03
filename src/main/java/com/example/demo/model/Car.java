package com.example.demo.model;

import com.example.demo.model.base.AbstractEntity;
import com.example.demo.utils.JSONChange;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tb_car")
public class Car extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -8786497774689346549L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer id;

    @Column(name = "car_vin")
    private String vin;

    @Column(name = "car_plate", length = 64)
    private String licensePlate;

    //多对一
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_car_id", referencedColumnName = "id")
    private User user;

    public Car() {

    }

    public Car(String vin, String licensePlate) {
        this.vin = vin;
        this.licensePlate = licensePlate;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return JSONChange.objToJson(this);
    }
}
