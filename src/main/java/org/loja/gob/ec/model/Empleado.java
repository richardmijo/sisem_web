package org.loja.gob.ec.model;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@TableGenerator(name = "EmpleadoGenerator", table = "IdentityGenerator", pkColumnName = "name", valueColumnName = "value", pkColumnValue = "Empleado", initialValue = 1, allocationSize = 1)
public class Empleado implements Serializable {

    @Id
    @GeneratedValue(generator = "EmpleadoGenerator", strategy = GenerationType.TABLE)
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column(length = 15, nullable = false)
    private String cedula;

    @Column(length = 200)
    private String unidad;

    @Column(length = 200)
    private String departamento;

    @Column(nullable = false)
    private String nombres;

    @Column(length = 200)
    private String correo;
    
    @Column(length = 100)
    private String cargo;
    
    @Column(length = 5)
    private String estado_sistema;
    
    @Column(length = 30)
    private String actual_decimos;
    
    @Column(length = 20)
    private String telefonoContato;

    @Column
    private Boolean apply;

    @Temporal(TemporalType.DATE)
    private Date registerDate;

    @Temporal(TemporalType.TIME)
    private Date registerTime;

    private Integer numberSolicitud;

    public Empleado() {
            //registerDate = new Date();
        //registerTime = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Empleado)) {
            return false;
        }
        Empleado other = (Empleado) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getApply() {
        return apply;
    }

    public void setApply(Boolean apply) {
        this.apply = apply;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getNumberSolicitud() {
        return numberSolicitud;
    }

    public void setNumberSolicitud(Integer numberSolicitud) {
        this.numberSolicitud = numberSolicitud;
    }
    
    public String getNumeroFormato(){
        if (this.numberSolicitud != null) {
            return String.format("%06d", this.numberSolicitud);
        }
        return "";
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (cedula != null && !cedula.trim().isEmpty()) {
            result += "cedula: " + cedula;
        }
        if (unidad != null && !unidad.trim().isEmpty()) {
            result += ", unidad: " + unidad;
        }
        if (departamento != null && !departamento.trim().isEmpty()) {
            result += ", departamento: " + departamento;
        }
        if (nombres != null && !nombres.trim().isEmpty()) {
            result += ", nombres: " + nombres;
        }
        if (correo != null && !correo.trim().isEmpty()) {
            result += ", correo: " + correo;
        }
        if (apply != null) {
            result += ", apply: " + apply;
        }
        return result;
    }

    public String getTelefonoContato() {
        return telefonoContato;
    }

    public void setTelefonoContato(String telefonoContato) {
        this.telefonoContato = telefonoContato;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEstado_sistema() {
        return estado_sistema;
    }

    public void setEstado_sistema(String estado_sistema) {
        this.estado_sistema = estado_sistema;
    }

    public String getActual_decimos() {
        return actual_decimos;
    }

    public void setActual_decimos(String actual_decimos) {
        this.actual_decimos = actual_decimos;
    }
    
}
