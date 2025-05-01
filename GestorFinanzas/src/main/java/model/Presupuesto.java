package model;

public class Presupuesto {
    private String mes;
    private String categoria;
    private double cantidad;

    public Presupuesto(String mes, String categoria, double cantidad) {
        this.mes = mes;
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public String getMes() {
        return mes;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
