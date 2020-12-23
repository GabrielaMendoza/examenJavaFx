/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mysqlc;

/**
 *
 * @author gabriela
 */
public class Producto {

    private Integer codigo;
    private String descripcion;
    private Integer can;
    private Integer iva;
    private Integer precio;
    private String marca;

    public Producto(Integer codigo, String descripcion, Integer can, Integer iva, Integer precio, String marca) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.can = can;
        this.iva = iva;
        this.precio = precio;
        this.marca = marca;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCan() {
        return can;
    }

    public void setCan(Integer can) {
        this.can = can;
    }

    public Integer getIva() {
        return iva;
    }

    public void setIva(Integer iva) {
        this.iva = iva;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

}
