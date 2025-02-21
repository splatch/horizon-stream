/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2020 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2020 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.events.xml;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * The event correlation information
 * 
 * @version $Revision$ $Date$
 */

@XmlRootElement(name="correlation")
@XmlAccessorType(XmlAccessType.FIELD)
//@ValidateUsing("event.xsd")
public class Correlation implements Serializable {
	private static final long serialVersionUID = 7883869597194555535L;

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

	/**
     * The state determines if event is
     *  correlated
     */
	@XmlAttribute(name="state")
	@Pattern(regexp="(on|off)")
    private String _state = "off";

    /**
     * Field _path.
     */
	@XmlAttribute(name="path")
	@Pattern(regexp="(suppressDuplicates|cancellingEvent|suppressAndCancel|pathOutage)")
    private String _path = "suppressDuplicates".intern();

    /**
     * A cancelling UEI for this event
     */
	@XmlElement(name="cuei")
    private java.util.List<String> _cueiList;

    /**
     * The minimum count for this event
     */
	@XmlElement(name="cmin")
    private String _cmin;

    /**
     * The maximum count for this event
     */
	@XmlElement(name="cmax")
    private String _cmax;

    /**
     * The correlation time for this event
     */
	@XmlElement(name="ctime")
    private String _ctime;


      //----------------/
     //- Constructors -/
    //----------------/

    public Correlation() {
        super();
        setState("off");
        setPath("suppressDuplicates".intern());
        this._cueiList = new java.util.ArrayList<>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vCuei
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCuei(
            final String vCuei)
    throws IndexOutOfBoundsException {
        this._cueiList.add(vCuei);
    }

    /**
     * 
     * 
     * @param index
     * @param vCuei
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCuei(
            final int index,
            final String vCuei)
    throws IndexOutOfBoundsException {
        this._cueiList.add(index, vCuei);
    }

    /**
     * Method enumerateCuei.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<String> enumerateCuei(
    ) {
        return java.util.Collections.enumeration(this._cueiList);
    }

    /**
     * Returns the value of field 'cmax'. The field 'cmax' has the
     * following description: The maximum count for this event
     * 
     * @return the value of field 'Cmax'.
     */
    public String getCmax(
    ) {
        return this._cmax;
    }

    /**
     * Returns the value of field 'cmin'. The field 'cmin' has the
     * following description: The minimum count for this event
     * 
     * @return the value of field 'Cmin'.
     */
    public String getCmin(
    ) {
        return this._cmin;
    }

    /**
     * Returns the value of field 'ctime'. The field 'ctime' has
     * the following description: The correlation time for this
     * event
     * 
     * @return the value of field 'Ctime'.
     */
    public String getCtime(
    ) {
        return this._ctime;
    }

    /**
     * Method getCuei.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public String getCuei(
            final int index)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._cueiList.size()) {
            throw new IndexOutOfBoundsException("getCuei: Index value '" + index + "' not in range [0.." + (this._cueiList.size() - 1) + "]");
        }
        
        return (String) _cueiList.get(index);
    }

    /**
     * Method getCuei.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public String[] getCuei(
    ) {
        String[] array = new String[0];
        return (String[]) this._cueiList.toArray(array);
    }

    /**
     * Method getCueiCollection.Returns a reference to '_cueiList'.
     * No type checking is performed on any modifications to the
     * Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<String> getCueiCollection(
    ) {
        return this._cueiList;
    }

    /**
     * Method getCueiCount.
     * 
     * @return the size of this collection
     */
    public int getCueiCount(
    ) {
        return this._cueiList.size();
    }

    /**
     * Returns the value of field 'path'.
     * 
     * @return the value of field 'Path'.
     */
    public String getPath(
    ) {
        return this._path;
    }

    /**
     * Returns the value of field 'state'. The field 'state' has
     * the following description: The state determines if event is
     *  correlated
     * 
     * @return the value of field 'State'.
     */
    public String getState(
    ) {
        return this._state;
    }

    /**
     * Method iterateCuei.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<String> iterateCuei(
    ) {
        return this._cueiList.iterator();
    }

    /**
     */
    public void removeAllCuei(
    ) {
        this._cueiList.clear();
    }

    /**
     * Method removeCuei.
     * 
     * @param vCuei
     * @return true if the object was removed from the collection.
     */
    public boolean removeCuei(
            final String vCuei) {
        return _cueiList.remove(vCuei);
    }

    /**
     * Method removeCueiAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public String removeCueiAt(
            final int index) {
        return this._cueiList.remove(index);
    }

    /**
     * Sets the value of field 'cmax'. The field 'cmax' has the
     * following description: The maximum count for this event
     * 
     * @param cmax the value of field 'cmax'.
     */
    public void setCmax(
            final String cmax) {
        this._cmax = cmax;
    }

    /**
     * Sets the value of field 'cmin'. The field 'cmin' has the
     * following description: The minimum count for this event
     * 
     * @param cmin the value of field 'cmin'.
     */
    public void setCmin(
            final String cmin) {
        this._cmin = cmin;
    }

    /**
     * Sets the value of field 'ctime'. The field 'ctime' has the
     * following description: The correlation time for this event
     * 
     * @param ctime the value of field 'ctime'.
     */
    public void setCtime(
            final String ctime) {
        this._ctime = ctime;
    }

    /**
     * 
     * 
     * @param index
     * @param vCuei
     * @throws IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCuei(
            final int index,
            final String vCuei)
    throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._cueiList.size()) {
            throw new IndexOutOfBoundsException("setCuei: Index value '" + index + "' not in range [0.." + (this._cueiList.size() - 1) + "]");
        }
        
        this._cueiList.set(index, vCuei);
    }

    /**
     * Sets the value of '_cueiList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vCueiList the Vector to copy.
     */
    public void setCuei(
            final java.util.List<String> vCueiList) {
        // copy vector
        this._cueiList.clear();
        
        this._cueiList.addAll(vCueiList);
    }

    /**
     * Sets the value of '_cueiList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param cueiList the Vector to set.
     */
    public void setCueiCollection(
            final java.util.List<String> cueiList) {
        this._cueiList = cueiList;
    }

    /**
     * Sets the value of field 'path'.
     * 
     * @param path the value of field 'path'.
     */
    public void setPath(
            final String path) {
        this._path = path.intern();
    }

    /**
     * Sets the value of field 'state'. The field 'state' has the
     * following description: The state determines if event is
     *  correlated
     * 
     * @param state the value of field 'state'.
     */
    public void setState(
            final String state) {
        this._state = state;
    }

        @Override
    public String toString() {
    	return new OnmsStringBuilder(this).toString();
    }
}
