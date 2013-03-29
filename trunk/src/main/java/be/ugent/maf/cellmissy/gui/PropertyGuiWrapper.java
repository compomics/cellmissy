/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui;

import be.ugent.maf.cellmissy.config.PropertiesConfigurationHolder;
import java.beans.PropertyChangeListener;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * GUI wrapper to modify CellMissy properties
 *
 * @author Paola Masuzzo
 */
public class PropertyGuiWrapper {

    private static boolean transactionPropertyChanged = false;
    /**
     * The property key
     */
    private String key;
    /**
     * The property value
     */
    private Object value;
    private final SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);

    public PropertyGuiWrapper(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static boolean isTransactionPropertyChanged() {
        return transactionPropertyChanged;
    }

    public static void setTransactionPropertyChanged(boolean transactionPropertyChanged) {
        PropertyGuiWrapper.transactionPropertyChanged = transactionPropertyChanged;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the property. Sets the new value in the properties configuration holder.
     *
     * @param value the new property value
     */
    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        propertyChangeSupport.firePropertyChange("value", oldValue, value);
        PropertiesConfigurationHolder.getInstance().setProperty(key, value);
        if (key.startsWith("db") && !transactionPropertyChanged) {
            transactionPropertyChanged = true;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
