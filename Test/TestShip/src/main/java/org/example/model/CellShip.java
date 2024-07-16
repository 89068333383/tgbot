package org.example.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "cell_ships")
public class CellShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('cell_ships_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ship", nullable = false)
    private Ship idShip;

    @Column(name = "cell_ship", nullable = false)
    private Integer cellShip;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ship getIdShip() {
        return idShip;
    }

    public void setIdShip(Ship idShip) {
        this.idShip = idShip;
    }

    public Integer getCellShip() {
        return cellShip;
    }

    public void setCellShip(Integer cellShip) {
        this.cellShip = cellShip;
    }

}