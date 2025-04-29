package com.kibber.cryptoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//Getters y Setter a menos
@Entity
@Table(name="criptomonedas")

public class Criptomoneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "simbolo")
    private String simbolo;

    @Column(name = "precio_actual")
    private double precioActual;

    @Column (name = "cambio_24h")
    private double cambio24h;

    @Column (name = "volumen_mercado")
    private Long volumenMercado;

    @Column(name = "fecha_ultima_actualizacion")
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public double getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(double precioActual) {
        this.precioActual = precioActual;
    }

    public double getCambio24h() {
        return cambio24h;
    }

    public void setCambio24h(double cambio24h) {
        this.cambio24h = cambio24h;
    }

    public Long getVolumenMercado() {
        return volumenMercado;
    }

    public void setVolumenMercado(Long volumenMercado) {
        this.volumenMercado = volumenMercado;
    }

    public LocalDate getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDate fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}
