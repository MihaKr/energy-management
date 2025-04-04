package com.example.energy.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Table(name = "building")
@Entity
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull //java validation
    @Size(max=255)
    @Column(name = "name", unique = true, nullable = false, length = 255) //not needed but ok for validiaton
    private String name;

    @NotNull
    @Size(max=500)
    @Column(name = "location", nullable = false, length = 500)
    private String location;

    @OneToMany(mappedBy = "building")
    private List<Measurement> measurements = new ArrayList<>();

    @OneToMany(mappedBy = "building")
    private List<DeviceBuilding> deviceBuildings = new ArrayList<>();


    public Building(Integer id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Building() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<DeviceBuilding> getDeviceBuildings() {
        return deviceBuildings;
    }

    public void setDeviceBuildings(List<DeviceBuilding> deviceBuildings) {
        this.deviceBuildings = deviceBuildings;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public void addDeviceBuilding(DeviceBuilding deviceBuilding) {
        deviceBuildings.add(deviceBuilding);
        deviceBuilding.setBuilding(this);
    }

    public void removeDeviceBuilding(DeviceBuilding deviceBuilding) {
        deviceBuildings.remove(deviceBuilding);
        deviceBuilding.setBuilding(null);
    }

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
        measurement.setBuilding(this);
    }

    public void removeMeasurement(Measurement measurement) {
        measurements.remove(measurement);
        measurement.setBuilding(null);
    }

}
