package beans;

import entidades.Participa;
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

@Named("participaController")
@SessionScoped
public class ParticipaController implements Serializable {

    @EJB
    private beans.ParticipaFacade ejbFacade;
    private List<Participa> items = null;
    private Participa selected;

    public ParticipaController() {
    }

    public Participa getSelected() {
        return selected;
    }

    public void setSelected(Participa selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getParticipaPK().setPelTitulo(selected.getPelicula().getPelTitulo());
        selected.getParticipaPK().setActNombre(selected.getActor().getActNombre());
    }

    protected void initializeEmbeddableKey() {
        selected.setParticipaPK(new entidades.ParticipaPK());
    }

    private ParticipaFacade getFacade() {
        return ejbFacade;
    }

    public Participa prepareCreate() {
        selected = new Participa();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ParticipaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ParticipaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ParticipaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Participa> getItems() {
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

    public Participa getParticipa(entidades.ParticipaPK id) {
        return getFacade().find(id);
    }

    public List<Participa> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Participa> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Participa.class)
    public static class ParticipaControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ParticipaController controller = (ParticipaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "participaController");
            return controller.getParticipa(getKey(value));
        }

        entidades.ParticipaPK getKey(String value) {
            entidades.ParticipaPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entidades.ParticipaPK();
            key.setActNombre(values[0]);
            key.setPelTitulo(values[1]);
            return key;
        }

        String getStringKey(entidades.ParticipaPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getActNombre());
            sb.append(SEPARATOR);
            sb.append(value.getPelTitulo());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Participa) {
                Participa o = (Participa) object;
                return getStringKey(o.getParticipaPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Participa.class.getName()});
                return null;
            }
        }

    }

}
