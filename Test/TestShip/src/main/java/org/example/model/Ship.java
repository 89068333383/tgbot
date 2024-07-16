package org.example.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ships")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('ship_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name_ship", nullable = false, length = Integer.MAX_VALUE)
    private String nameShip;

    @Column(name = "cell_ship", nullable = false)
    private Integer cellShip;

    @Column(name = "count_ship", nullable = false)
    private Integer countShip;

    @OneToMany(mappedBy = "idShip")
    private List<CellShip> cellShips = new ArrayList<>();

    public Ship(){}

    public Ship(Integer id, String nameShip, Integer cellShip, Integer countShip) {
        this.id = id;
        this.nameShip = nameShip;
        this.cellShip = cellShip;
        this.countShip = countShip;
        cellShips = new ArrayList<>();
    }

    public void addCellShip(CellShip cellShip){
        cellShip.setShip(this);
        cellShips.add(cellShip);
    }

    public void addCellShip(CellShip cellShip) {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameShip() {
        return nameShip;
    }

    public void setNameShip(String nameShip) {
        this.nameShip = nameShip;
    }

    public Integer getCellShip() {
        return cellShip;
    }

    public void setCellShip(Integer cellShip) {
        this.cellShip = cellShip;
    }

    public Integer getCountShip() {
        return countShip;
    }

    public void setCountShip(Integer countShip) {
        this.countShip = countShip;
    }

    public Set<CellShip> getCellShips() {
        return cellShips;
    }

    public void setCellShips(Set<CellShip> cellShips) {
        this.cellShips = cellShips;
    }

}