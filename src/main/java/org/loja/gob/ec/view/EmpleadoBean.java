package org.loja.gob.ec.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.loja.gob.ec.model.DataMemo;

import org.loja.gob.ec.model.Empleado;

/**
 * Backing bean for Empleado entities.
 * <p/>
 * This class provides CRUD functionality for all Empleado entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */
@Named
@Stateful
@ConversationScoped
public class EmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Support creating and retrieving Empleado entities
     */
    private Long id;
    DataMemo data=new DataMemo();
    private String cedulaBusqueda;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Empleado empleado;

    public Empleado getEmpleado() {
        return this.empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Inject
    private Conversation conversation;

    @PersistenceContext(unitName = "sisem-persistence-unit", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String create() {

        this.conversation.begin();
        this.conversation.setTimeout(1800000L);
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
            this.conversation.setTimeout(1800000L);
        }

        if (this.id == null) {
            this.empleado = this.example;
        } else {
            this.empleado = findById(getId());
        }
    }

    public Empleado findById(Long id) {

        return this.entityManager.find(Empleado.class, id);
    }

    /*
     * Support updating and deleting Empleado entities
     */
    public String update() {
        this.conversation.end();
        try {
            if (this.id == null) {
                this.entityManager.persist(this.empleado);
                return "search?faces-redirect=true";
            } else {
                this.empleado.setRegisterDate(new Date());
                this.empleado.setRegisterTime(new Date());
                this.entityManager.merge(this.empleado);
                String summary = "Se ha guardado correctamente";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
                return "view?faces-redirect=true&id=" + this.empleado.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }
    
    
    public void updateEmpleado(){
        System.out.println("esta entrando aaaaaaaaaaaaa a modificarssssssssssssssss");
        if (this.getEmpleado().getNumberSolicitud() ==  null) {
            this.empleado.setNumberSolicitud(findCurrenRequestNumber());
            this.empleado.setRegisterDate(new Date());
            this.empleado.setRegisterTime(new Date());
        }
        //this.conversation.end();
        try {
            this.empleado.setRegisterDate(new Date());
            this.empleado.setRegisterTime(new Date());
            switch (this.empleado.getEstado_sistema()) {
                case "N":
                    this.empleado.setEstado_sistema("Y");
                    this.empleado.setActual_decimos("MENSUALIZA DECIMO");
                    this.entityManager.merge(this.empleado);
                    estadoDecimo = "MENSUALIZACIÓN";
                    String summary = "Se ha guardado correctamente";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
                    //return "view?faces-redirect=true&id=" + this.empleado.getId();\
                    break;
                case "Y":
                    this.empleado.setEstado_sistema("N");
                    this.empleado.setActual_decimos("ACUMULA DECIMO");
                    this.entityManager.merge(this.empleado);
                    estadoDecimo = "ACUMULACIÓN";
                    String summary2 = "Se ha guardado correctamente";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary2));
                    break;
                default:
                    estadoDecimo = "";
                    String error= "El empleado no tiene Estado_sistema";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
                    break;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            //return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Empleado deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    /*
     * Support searching Empleado entities with pagination
     */
    private int page;
    private long count;
    private List<Empleado> pageItems;

    private Empleado example = new Empleado();

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 25;
    }

    public Empleado getExample() {
        return this.example;
    }

    public void setExample(Empleado example) {
        this.example = example;
    }

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Empleado> root = countCriteria.from(Empleado.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

		// Populate this.pageItems
        CriteriaQuery<Empleado> criteria = builder.createQuery(Empleado.class);
        root = criteria.from(Empleado.class);
        TypedQuery<Empleado> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Empleado> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String cedula = this.example.getCedula();
        if (cedula != null && !"".equals(cedula)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("cedula")),
                    '%' + cedula.toLowerCase() + '%'));
        }
        String unidad = this.example.getUnidad();
        if (unidad != null && !"".equals(unidad)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("unidad")),
                    '%' + unidad.toLowerCase() + '%'));
        }
        String departamento = this.example.getDepartamento();
        if (departamento != null && !"".equals(departamento)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("departamento")),
                    '%' + departamento.toLowerCase() + '%'));
        }
        String nombres = this.example.getNombres();
        if (nombres != null && !"".equals(nombres)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("nombres")),
                    '%' + nombres.toLowerCase() + '%'));
        }
        String correo = this.example.getCorreo();
        if (correo != null && !"".equals(correo)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("correo")),
                    '%' + correo.toLowerCase() + '%'));
        }
        
        Integer numberSolicitud = this.example.getNumberSolicitud();
        if (numberSolicitud != null && numberSolicitud.intValue() != 0) {
            predicatesList.add(builder.equal(root.get("numberSolicitud"),
                    numberSolicitud));
        }
        
        Boolean apply = this.example.getApply();
        if (apply != null) {
                predicatesList.add(builder.equal(root.get("apply"), apply));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<Empleado> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    /*
     * Support listing and POSTing back Empleado entities (e.g. from inside an
     * HtmlSelectOneMenu)
     */
    public List<Empleado> getAll() {

        CriteriaQuery<Empleado> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Empleado.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Empleado.class))).getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

        final EmpleadoBean ejbProxy = this.sessionContext
                .getBusinessObject(EmpleadoBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                    UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                    UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((Empleado) value).getId());
            }
        };
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */
    private Empleado add = new Empleado();

    public Empleado getAdd() {
        return this.add;
    }

    public Empleado getAdded() {
        Empleado added = this.add;
        this.add = new Empleado();
        return added;
    }

    public void addMessage() {
        String summary = empleado.getApply() ? "Aplica" : "No aplica";
        String detail = empleado.getApply() ? "Aplica acumulación decimos" : "No aplica acumulación decimos";
        Severity ss = empleado.getApply() ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_WARN;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ss,summary,detail));
    }
    
    public void setMessage(String text,Severity s) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s,text,""));
    }
    
    private String estadoDecimo;
    public void findEmpleado(){
        estadoDecimo = "";
        if (this.cedulaBusqueda != null) {
            System.out.println("el valor es: "+this.cedulaBusqueda);
            Query q = this.entityManager.createQuery("select e from Empleado e where e.cedula like :cedula");
            q.setParameter("cedula", this.cedulaBusqueda);
            List <Empleado> empleadosEncontrados = new ArrayList<>();
            empleadosEncontrados = q.getResultList();
            if (empleadosEncontrados.size() > 0) {
                this.empleado = empleadosEncontrados.get(0);
                if(this.empleado.getEstado_sistema().equals("N")){
                    estadoDecimo = "ACUMULACIÓN";
                }else{
                    estadoDecimo = "MENSUALIZACIÓN";
                }
            }else{
                Severity ss = FacesMessage.SEVERITY_ERROR;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ss,"Datos no existentes","Digite su cédula"));
            }
        }
    }
    
    public void cancel(){
        this.conversation.end();
        setMessage("Accion Cancelada", FacesMessage.SEVERITY_INFO);
        this.empleado=new Empleado();
    }
    
        
    public void executePDF() {
        
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            // setting some response headers
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            //response.setHeader("Content-disposition","inline; filename=kiran.pdf");
            response.setHeader("Pragma", "public");
            response.setContentType("application/pdf");
            //response.setHeader("Content-Disposition", "attachment;filename=\"ContactList.pdf\"");
            response.addHeader("Content-disposition","attachment;filename=\"memo_sol_acum.pdf\"");
            //step 1: creation of a document-object
            Document document = new Document(PageSize.A4, 80, 59, 30, 30);
            //step 2: we create a writer that listens to the document
            // and directs a PDF-stream to a temporary buffer
            ByteArrayOutputStream baos = new ByteArrayOutputStream(3024);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            //step 3: we open the document
            document.open();
            
            
            URL url = FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/logo_ok.png");
            
            Image image = Image.getInstance(url);
            image.scaleAbsolute(330, 79);
            image.setAlignment(Element.ALIGN_LEFT);
            //image.setAbsolutePosition(500f, 650f);
            document.add(image);
            
            Paragraph p_titulo = new Paragraph(data.getTitulo(estadoDecimo), new Font(FontFamily.HELVETICA, 12));
            p_titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(p_titulo);
                        
            Paragraph m_fecha = new Paragraph(data.getFechaMemorando(this.empleado.getRegisterDate()), new Font(FontFamily.HELVETICA, 11));
            m_fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(m_fecha);
            
            Paragraph m_doctor = new Paragraph(data.getDoctor(), new Font(FontFamily.HELVETICA, 11));
             m_doctor.setAlignment(Element.ALIGN_LEFT);
            document.add( m_doctor);
                                                            
            Paragraph m_canton = new Paragraph(data.getDoctorDesignacion(), new Font(FontFamily.HELVETICA, 11,Font.BOLD));
            m_canton.setAlignment(Element.ALIGN_LEFT);
            document.add(m_canton);
            
            
            Paragraph m_despacho = new Paragraph(data.getDespacho(), new Font(FontFamily.HELVETICA, 11));
            m_despacho.setAlignment(Element.ALIGN_LEFT);
            document.add( m_despacho);
            
            
            /*Paragraph m_doctorDesignacion = new Paragraph(data.getDoctorDesignacion(), new Font(FontFamily.HELVETICA, 11));
            m_doctorDesignacion.setAlignment(Element.ALIGN_LEFT);
            document.add( m_doctorDesignacion);*/                        
            
            Paragraph p = new Paragraph(data.getData(this.getEmpleado().getCedula(), this.getEmpleado().getNombres(),estadoDecimo), new Font(FontFamily.HELVETICA, 12));
            p.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(p);
            
            Paragraph m_firma = new Paragraph(data.getFirma(this.getEmpleado().getCedula(), this.getEmpleado().getNombres()), new Font(FontFamily.HELVETICA, 11));
            m_firma.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(m_firma);
            
            Paragraph m_numero = new Paragraph(data.getNumero(this.getEmpleado().getNumberSolicitud()), new Font(FontFamily.HELVETICA, 9));
            m_numero.setAlignment(Element.ALIGN_RIGHT);
            document.add(m_numero);
            
            Paragraph m_info = new Paragraph(data.info(), new Font(FontFamily.COURIER, 8));
            m_info.setAlignment(Element.ALIGN_CENTER);
            document.add(m_info);
                        
            document.addAuthor(this.getEmpleado().getNombres());
            document.addCreationDate();
            document.addCreator("di_iml");
            document.addTitle("acumulacion_decimo");
            
            //step 5: we close the document
            document.close();
            //step 6: we output the writer as bytes to the response output
            // the contentlength is needed for MSIE!!!
            response.setContentLength(baos.size());
            // write ByteArrayOutputStream to the ServletOutputStream
            ServletOutputStream out = response.getOutputStream();
            baos.writeTo(out);
            baos.flush();
            faces.responseComplete();                        

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public Integer findCurrenRequestNumber(){
        Query q = this.entityManager.createQuery("select max(e.numberSolicitud) from Empleado e ");
        Integer number = new Integer("0");
        try {
            Object ob = q.getSingleResult();
            number = Integer.parseInt(ob.toString()) + 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            number = new Integer("1");
        }
        System.out.println("el valoeeeeeeeeeeeeeeeee es "+number);
        
        return number;
    }

    public String getCedulaBusqueda() {
        return cedulaBusqueda;
    }

    public void setCedulaBusqueda(String cedulaBusqueda) {
        this.cedulaBusqueda = cedulaBusqueda;
    }

        
}
