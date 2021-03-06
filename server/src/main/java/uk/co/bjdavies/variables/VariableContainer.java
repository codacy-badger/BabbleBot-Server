package uk.co.bjdavies.variables;

import lombok.extern.log4j.Log4j2;
import uk.co.bjdavies.api.variables.IVariableContainer;
import uk.co.bjdavies.api.variables.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * BabbleBot, open-source Discord Bot
 * Licence: GPL V3
 * Author: Ben Davies
 * Class Name: VariableContainer.java
 * Compiled Class Name: VariableContainer.class
 * Date Created: 30/01/2018
 */
@Log4j2
public class VariableContainer implements IVariableContainer {
    /**
     * This is the Map for all the field variables.
     */
    private final Map<String, Field> variableFields;


    /**
     * This is the Map for all the method variables.
     */
    private final Map<String, Method> variableMethods;


    /**
     * This is where the Maps get initialized with the HashMap implementation of Map.
     */
    public VariableContainer() {
        variableFields = new HashMap<>();
        variableMethods = new HashMap<>();
    }


    /**
     * This method will add all the variables (@Variable.class) that are in that class to the 2 Maps.
     *
     * @param clazz - The class you want to insert them from.
     */
    public void addAllFrom(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Variable.class)) addMethod(method.getName(), method);
        }

        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Variable.class)) variableFields.put(field.getName(), field);
        }
    }

    /**
     * This will add a method to the container.
     *
     * @param name   - the name of the variable.
     * @param method - the method for the variable.
     */
    public void addMethod(String name, Method method) {
        if (variableMethods.containsKey(name) || variableMethods.containsValue(method)) {
            log.error("The key or method is already in the container.");
        } else {
            variableMethods.put(name, method);
        }
    }


    /**
     * This will add a field to the container.
     *
     * @param name  - the name of the variable.
     * @param field - the field for the variable.
     */
    public void addField(String name, Field field) {
        if (variableFields.containsKey(name) || variableFields.containsValue(field)) {
            log.error("The key or field is already in the container.");
        } else {
            variableFields.put(name, field);
        }
    }

    /**
     * This will remove one from the container based on the name.
     *
     * @param name - The name of the variable.
     */
    public void remove(String name) {
        if (variableFields.containsKey(name)) {
            variableFields.remove(name);
        } else if (variableMethods.containsKey(name)) {
            variableMethods.remove(name);
        } else {
            log.error("The name specified cannot be found inside this container.");
        }
    }

    /**
     * This will return a field based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Field
     */
    public Field getFieldVariable(String name) {
        if (!variableFields.containsKey(name)) {
            log.error("Field cannot be found with the name specified in this container.");
        } else {
            return variableFields.get(name);
        }
        return null;
    }

    /**
     * This will return a method based on the name specified.
     *
     * @param name - the name of the Field you want to receive.
     * @return Method
     */
    public Method getMethodVariable(String name) {
        if (!variableMethods.containsKey(name)) {
            log.error("Method cannot be found with the name specified in this container.");
        } else {
            return variableMethods.get(name);
        }
        return null;
    }

    /**
     * This checks whether the variable exists in this container.
     *
     * @param name - the name of the variable.
     * @return Boolean
     */
    public boolean exists(String name) {
        if (variableFields.containsKey(name)) {
            return true;
        } else return variableMethods.containsKey(name);
    }

}
