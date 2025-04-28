package com.kibber.cryptoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name="criptomonedas")

public class Criptomoneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    private String simbolo;

    private double precioActual;

    private double cambio24h;

    private Long volumenMercado;

    private LocalDate fechaUltimaActualizacion;

    public Criptomoneda() {
    }

    public Criptomoneda(int id, String nombre, String simbolo, double precioActual, double cambio24h, Long volumenMercado, LocalDate fechaUltimaActualizacion) {
        this.id = id;
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.precioActual = precioActual;
        this.cambio24h = cambio24h;
        this.volumenMercado = volumenMercado;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}
