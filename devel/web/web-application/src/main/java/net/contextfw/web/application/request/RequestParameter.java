package net.contextfw.web.application.request;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides functionality to access on GET/POST-parameter.
 * 
 * <h3>Parsing integers, floats and booleans</h3>
 * 
 * <p>
 * Integers, floats and booleans can be represented as a string in many ways.
 * For instance decimal point can be '.' or ','. Also other means of
 * representation are possible, i.e. scientific form
 * </p>
 * 
 * <p>
 * Therefore it is the implentors responsibility to decide what kind of
 * representations are accepted.
 * </p>
 * 
 * @author Marko Lavikainen
 * 
 */
public class RequestParameter {

    /**
     * GET/POST-parameter name
     */
    private String publicName;

    private String name;
    private Object modifier;

    private int status = 0;

    /**
     * Current HTTP-request is saved here for quicker access.
     */
    private HttpServletRequest request;

    /**
     * Externally assigned value(s)
     */
    private String[] values = null;

    /**
     * Assigned options
     */
    private Object options = null;

    /**
     * <p>
     * Constructs new HiveParameter
     * </p>
     * 
     * <p>
     * This constructor should not be called explicitly.
     * </p>
     * 
     * @param name
     *            Parameter name, which corresponds to the equal
     *            GET/POST-value(s).
     */
    public RequestParameter(String name, String publicName, HttpServletRequest request) {
        this.publicName = publicName;
        this.name = name;
        this.request = request;
    }

    public int valueCount() {

        if (values != null)
            return values.length;

        String[] values = request.getParameterValues(publicName);

        if (values == null)
            return 0;

        return values.length;
    }

    /**
     * <p>
     * Returns parameter value as a string.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @return Found parameter value.
     */
    public String getStringValue() {

        if (values != null)
            return values[0];

        String value = request.getParameter(publicName);

        return value;
    }

    /**
     * <p>
     * Returns parameter value as a string.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @param def
     *            Default value. Can be <code>null</code>.
     * @return Found parameter value. If value does not exists or request is
     *         ignored, then default value is returned.
     */
    public String getStringValue(String def) {
        String value = getStringValue();
        return value != null ? value : def;
    }

    /**
     * <p>
     * Returns all parameter values in a table of strings
     * </p>
     * 
     * @return Found parameter values.
     * @throws NoValueException
     *             Thrown if parameter has no value(s), or request is ignored.
     */
    public String[] getStringValues() {

        if (values != null)
            return values;

        String[] values = request.getParameterValues(publicName);
        return values;
    }

    /**
     * <p>
     * Returns all parameter values in a table of strings
     * </p>
     * 
     * @param def
     *            Default values
     * 
     * @return Found parameter values. If parameter does not have any values, or
     *         request is ignored, then default values are returned.
     */
    public String[] getStringValues(String[] def) {
        String[] values = getStringValues();
        return values != null ? values : def;
    }

    /**
     * <p>
     * Returns parameter value as an integer.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @return Found parameter value
     * @throws NumberFormatException
     *             Thrown if parameter cannot be parsed to integer.
     */
    public Integer getIntValue() throws NumberFormatException {
        String value = getStringValue();
        return value != null ? new Integer(value) : null;
    }

    /**
     * <p>
     * Returns parameter value as an integer.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @param def
     *            Default value
     * 
     * @return Found parameter value. If parameter does not have any values, or
     *         if request is ignored, then default value are returned.
     */
    public Integer getIntValue(Integer def) {
        try {
            Integer value = getIntValue();
            return value != null ? value : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns parameter value as a long integer.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @return Found parameter value
     * @throws NumberFormatException
     *             Thrown if parameter cannot be parsed to long integer.
     */
    public Long getLongValue() throws NumberFormatException {
        String value = getStringValue();
        return value != null ? new Long(value) : null;
    }

    /**
     * <p>
     * Returns parameter value as a long.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * @param def
     *            Default value
     * 
     * @return Found parameter value. If parameter does not have any values, or
     *         if request is ignored, then default value are returned.
     */
    public Long getLongValue(Long def) {

        try {
            String value = getStringValue();
            return value != null ? new Long(value) : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns all parameter values in a table of long integers
     * </p>
     * 
     * @return Found parameter values
     * @throws NoValueException
     *             Thrown if parameter has no value(s), or if request is
     *             ignored.
     * @throws NumberFormatException
     *             Thrown if any of the parameter values cannot be parsed to
     *             long integer
     */
    public Long[] getLongValues() throws NumberFormatException {

        Long[] values;
        String[] strValues = getStringValues();

        if (strValues == null)
            return null;

        values = new Long[strValues.length];

        for (int c = 0; c < strValues.length; c++) {
            values[c] = new Long(strValues[c]);
        }

        return values;
    }

    public Long[] getLongValues(Long[] def) {

        try {
            Long[] values = getLongValues();
            return values != null ? values : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns all parameter values in a table of integers
     * </p>
     * 
     * @return Found parameter values
     * @throws NumberFormatException
     *             Thrown if any of the parameter values cannot be parsed to int
     */
    public Integer[] getIntValues() throws NumberFormatException {

        Integer[] values;
        String[] strValues = getStringValues();

        if (strValues == null)
            return null;

        values = new Integer[strValues.length];

        for (int c = 0; c < strValues.length; c++) {
            values[c] = new Integer(strValues[c]);
        }

        return values;
    }

    public Integer[] getIntValues(Integer[] def) {
        try {
            Integer[] values = getIntValues();
            return values != null ? values : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns parameter value as a float.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * When floats are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @return Found parameter value
     * 
     * @throws NumberFormatException
     *             Thrown if parameter cannot be parsed to float.
     */
    public Float getFloatValue() throws NumberFormatException {
        String value = getStringValue();
        return value != null ? new Float(value.replace(",", ".")) : null;
    }

    /**
     * <p>
     * Returns parameter value as a long.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * When floats are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @param def
     *            Default value
     * 
     * @return Found parameter value. If parameter does not have any values, or
     *         if request is ignored, then default value are returned.
     */
    public Float getFloatValue(Float def) {
        try {
            Float value = getFloatValue();
            return value != null ? value : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns all parameter values in a table of floats
     * </p>
     * 
     * <p>
     * When floats are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @return Found parameter values
     * 
     * @throws NumberFormatException
     *             Thrown if any of the parameter values cannot be parsed to
     *             float.
     */
    public Float[] getFloatValues() throws NumberFormatException {

        Float[] values;
        String[] strValues = getStringValues();

        values = new Float[strValues.length];

        for (int c = 0; c < strValues.length; c++) {
            values[c] = new Float(strValues[c].replace(",", "."));
        }

        return values;
    }

    public Float[] getFloatValues(Float[] def) {
        try {
            Float[] values = getFloatValues();
            return values != null ? values : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns parameter value as a double.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * When doubles are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @return Found parameter value
     * 
     * @throws NumberFormatException
     *             Thrown if parameter cannot be parsed to double.
     */
    public Double getDoubleValue() throws NumberFormatException {
        String value = getStringValue();
        return value != null ? new Double(value.replace(",", ".")) : null;
    }

    /**
     * <p>
     * Returns parameter value as a double.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * When doubles are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @param def
     *            Default value
     * 
     * @return Found parameter value. If parameter does not have any values, or
     *         if request is ignored, then default value are returned.
     */
    public Double getDoubleValue(Double def) {
        try {
            Double value = getDoubleValue();
            return value != null ? value : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns all parameter values in a table of doubles
     * </p>
     * 
     * <p>
     * When doubles are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @return Found parameter values
     * @throws NumberFormatException
     *             Thrown if any of the parameter values cannot be parsed to
     *             doubles.
     */
    public Double[] getDoubleValues() throws NumberFormatException {

        Double[] values;
        String[] strValues = getStringValues();

        values = new Double[strValues.length];

        for (int c = 0; c < strValues.length; c++) {
            values[c] = new Double(strValues[c].replace(",", "."));
        }

        return values;
    }

    /**
     * <p>
     * Returns all parameter values in a table of doubles
     * </p>
     * 
     * <p>
     * When doubles are handled, system accepts both ways to represent decimal
     * points. This means, that decimal point can be represented with '.' or
     * ','. For example "123.435" or "123,345" are both accepted.
     * </p>
     * 
     * @return Found parameter values
     * 
     * @throws NumberFormatException
     *             Thrown if any of the parameter values cannot be parsed to
     *             doubles.
     */
    public Double[] getDoubleValues(Double[] def) {
        try {
            Double[] values = getDoubleValues();
            return values != null ? values : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns parameter value as a boolean.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * Booleans are parsed simply using <code>Boolean.parseBoolean()</code>.
     * Refer J2SE-documentation for more information.
     * </p>
     * 
     * @return Found parameter value
     * 
     * @throws NoValueException
     *             Thrown if parameter has no value, or if request is ignored.
     */
    public Boolean getBooleanValue() {
        String value = getStringValue();
        return value != null ? new Boolean(value) : null;
    }

    /**
     * <p>
     * Returns parameter value as a boolean.
     * </p>
     * 
     * <p>
     * If parameter has multiple values, then only the first value is returned.
     * </p>
     * 
     * <p>
     * Booleans are parsed simply using <code>Boolean.parseBoolean()</code>.
     * Refer J2SE-documentation for more information.
     * </p>
     * 
     * @param def
     *            Default value
     * 
     * @return Found parameter value. If parameter does not have any values, or
     *         if request is ignored, then default value are returned.
     */
    public Boolean getBooleanValue(Boolean def) {

        try {
            Boolean value = getBooleanValue();
            return value != null ? value : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * <p>
     * Returns all parameter values in a table of floats
     * </p>
     * 
     * <p>
     * Booleans are parsed simply using <code>Boolean.parseBoolean()</code>.
     * Refer J2SE-documentation for more information.
     * </p>
     * 
     * @return Found parameter values
     * 
     * @throws NoValueException
     *             Thrown if parameter has no value(s), or if request is
     *             ignored.
     */
    public Boolean[] getBooleanValues() {

        Boolean[] values;
        String[] strValues = getStringValues();

        values = new Boolean[strValues.length];

        for (int c = 0; c < strValues.length; c++) {
            values[c] = new Boolean(strValues[c]);
        }

        return values;
    }

    public Boolean[] getBooleanValues(Boolean[] def) {

        try {
            Boolean[] values = getBooleanValues();
            return values != null ? values : def;
        }
        catch (Exception e) {
            return def;
        }
    }

    public boolean hasValue(boolean strict) {

        // if (requestIgnored)
        // return false;

        if (values != null && values.length > 0) {
            return true;
        }

        String value = request.getParameter(publicName);

        if (value == null || (strict && value.length() == 0))
            return false;

        return true;
    }

    public boolean hasValue() {
        return hasValue(true);
    }

    public String getName() {
        return name;
    }

    public void setPublicName(String name) {
        this.publicName = name;
    }

    public void setValue(String value) {

        if (value == null) {
            values = null;
        }
        else {
            values = new String[1];
            values[0] = value;
        }
    }

    public void setValues(String[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = values[c];
            }
        }
    }

    public void setValue(int value) {
        values = new String[1];
        values[0] = Integer.toString(value);
    }

    public void setValues(int[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = Integer.toString(values[c]);
            }
        }
    }

    public void setValue(long value) {
        values = new String[1];
        values[0] = Long.toString(value);
    }

    public void setValues(long[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = Long.toString(values[c]);
            }
        }
    }

    public void setValue(float value) {
        values = new String[1];
        values[0] = Float.toString(value);
    }

    public void setValues(float[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = Float.toString(values[c]);
            }
        }
    }

    public void setValue(double value) {
        values = new String[1];
        values[0] = Double.toString(value);
    }

    public void setValues(double[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = Double.toString(values[c]);
            }
        }
    }

    public void setValue(boolean value) {
        values = new String[1];
        values[0] = Boolean.toString(value);
    }

    public void setValues(boolean[] values) {

        if (values.length > 0) {
            this.values = new String[values.length];

            for (int c = 0; c < values.length; c++) {
                this.values[c] = Boolean.toString(values[c]);
            }
        }
    }

    public void clearValues() {
        this.values = null;
    }

    public void clearOptions() {
        options = null;
    }

    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String publicName() {
        return publicName;
    }

    public Object getModifier() {
        return modifier;
    }

    public void setModifier(Object modifier) {
        this.modifier = modifier;
    }
}
