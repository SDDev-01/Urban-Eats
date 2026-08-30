package com.urbaneats.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Gerente")
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gerente_id")
    private Integer gerenteId;

    @Column(name = "departamento", nullable = false, length = 100)
    private String departamento;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    public Gerente() {
    }

    public Gerente(Integer gerenteId, String departamento, Usuario usuario) {
        this.gerenteId = gerenteId;
        this.departamento = departamento;
        this.usuario = usuario;
    }

    public Integer getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}