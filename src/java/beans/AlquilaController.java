package beans;

import entidades.Alquila;
import beans.util.JsfUtil;
import beans.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("alquilaController")
@SessionScoped
public class AlquilaController implements Serializable {

    @EJB
    private beans.AlquilaFacade ejbFacade;
    private List<Alquila> items = null;
    private Alquila selected;

    public AlquilaController() {
    }

    public Alquila getSelected() {
        return selected;
    }

    public void setSelected(Alquila selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getAlquilaPK().setDniSocio(selected.getSocio().getDniSocio());
        selected.getAlquilaPK().setPelicula(selected.getEjemplar().getEjemplarPK().getPelTitulo());
        selected.getAlquilaPK().setEjeNumEjemplar(selected.getEjemplar().getEjemplarPK().getEjeNumEjemplar());
    }

    protected void initializeEmbeddableKey() {
        selected.setAlquilaPK(new entidades.AlquilaPK());
    }

    private AlquilaFacade getFacade() {
        return ejbFacade;
    }

    public Alquila prepareCreate() {
        selected = new Alquila();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AlquilaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AlquilaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AlquilaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Alquila> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Alquila getAlquila(entidades.AlquilaPK id) {
        return getFacade().find(id);
    }

    public List<Alquila> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Alquila> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Alquila.class)
    public static class AlquilaControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AlquilaController controller = (AlquilaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "alquilaController");
            return controller.getAlquila(getKey(value));
        }

        entidades.AlquilaPK getKey(String value) {
            entidades.AlquilaPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entidades.AlquilaPK();
            key.setDniSocio(values[0]);
            key.setFechaAlquiler(java.sql.Date.valueOf(values[1]));
            key.setEjeNumEjemplar(Integer.parseInt(values[2]));
            key.setPelicula(values[3]);
            return key;
        }

        String getStringKey(entidades.AlquilaPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getDniSocio());
            sb.append(SEPARATOR);
            sb.append(value.getFechaAlquiler());
            sb.append(SEPARATOR);
            sb.append(value.getEjeNumEjemplar());
            sb.append(SEPARATOR);
            sb.append(value.getPelicula());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Alquila) {
                Alquila o = (Alquila) object;
                return getStringKey(o.getAlquilaPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Alquila.class.getName()});
                return null;
            }
        }

    }

}
