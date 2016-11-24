/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.loja.gob.ec.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author richard
 */
public class DataMemo {
    
    public String getTitulo(String estado){
            return "\nSOLICITUD DE "+estado+" DE LA DÉCIMA CUARTA Y TERCERA REMUNERACIÓN\n\n\n";
    }
    
    public String getFechaMemorando(Date fecha){
        SimpleDateFormat sdf_dia=new SimpleDateFormat("dd");
        SimpleDateFormat sdf_mes=new SimpleDateFormat("MMMM");
        SimpleDateFormat sdf_anio=new SimpleDateFormat("yyyy");
        return "Loja, "+sdf_dia.format(fecha)+" de "+sdf_mes.format(fecha)+" de "+sdf_anio.format(fecha)+"\n\n";
    }
    
    public String getDoctor(){
        return "Doctor\nJosé Bolívar Castillo Vivanco";
    }
    
    public String getDoctorDesignacion(){
        return "ALCALDE DEL CANTÓN LOJA\n";
    }
    
    public String getDespacho(){
        return "En su despacho.-\n\nDe mi consideración:\n\n";
    }        
    
    public String getData(String cedula, String nombres,String estado){
        String data="Yo, "+nombres+", portad@r de la C.I.: "+cedula+", me dirijo a usted para solicitarle se proceda con la "+estado+" DE MI DÉCIMA CUARTA Y TERCERA REMUNERACIÓN, de acuerdo con la Ley Orgánica de Justicia Laboral y Reconocimiento del Trabajo en el Hogar, emitido por la Asamblea Nacional del Ecuador, publicado en el Registro Oficial Nro. 483 de fecha 20 de abril de 2015, petición que la realizo de manera libre y voluntaria.\n\n" +
                "Por la atención que se digne dispensar a este pedido y en pleno uso de mis facultades, me suscribo.\n\n" +
                "Atentamente,";
        return data;
    }
    
    public String getFirma(String cedula, String nombres){
        return "\n\n\n______________________________________\n"
                + nombres+"\n"
                + cedula;
    }
    
    public String getNumero(Integer numero){
        if (numero != null) {
            return "\n\n_______\nNro. "+String.format("%06d", numero);
        }
        return null;
    }
    
    public String info(){
        return "\n\n\nPresentar este documento firmado en la Dirección de Recursos Humanos";
    }
    
}
