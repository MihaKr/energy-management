package com.example.energy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Table(name = "measurement")
@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name="timestamp", nullable = false)
    private Instant timestamp;

    @NotNull
    @Column(name="energy_kwh", nullable = false)
    private Float energyKwh;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    public Measurement(Integer id, Building building, Device device, Instant timestamp, Float energyKwh) {
        this.id = id;
        this.building = building;
        this.device = device;
        this.timestamp = timestamp;
        this.energyKwh = energyKwh;
    }

    public Measurement(Building building, Device device, Instant timestamp, Float energyKwh) {
        this.building = building;
        this.device = device;
        this.timestamp = timestamp;
        this.energyKwh = energyKwh;
    }

    public Measurement() {
    }

    public Float getEnergyKwh() {
        return energyKwh;
    }

    public void setEnergyKwh(Float energyKwh) {
        this.energyKwh = energyKwh;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}
