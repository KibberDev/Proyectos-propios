package model;

public class Movimiento {

    private String tipo;
    private double cantidad;
    private String fecha;
    private String categoria;
    private String descripcion;


    public Movimiento(String tipo, double cantidad, String fecha, String categoria, String descripcion) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
