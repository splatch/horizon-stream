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

import java.io.Serializable;
import java.util.StringJoiner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.opennms.horizon.events.model.IAutoAcknowledge;

/**
 * The autoacknowledge information for the user with state
 *  controlling if event is marked acknoledged when inserted into
 * the
 *  database
 * 
 * @version $Revision$ $Date$
 */

@XmlRootElement(name="autoacknowledge")
@XmlAccessorType(XmlAccessType.FIELD)
//@ValidateUsing("event.xsd")
public class Autoacknowledge implements Serializable {
	private static final long serialVersionUID = -3959431672151994490L;

	//--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
	@XmlValue
	@NotNull
    private String _content = "";

    /**
     * Field _state.
     */
	@XmlAttribute(name="state")
	@Pattern(regexp="(on|off)")
    private String _state = "on";


      //----------------/
     //- Constructors -/
    //----------------/

    public Autoacknowledge() {
        super();
        setContent("");
        setState("on");
    }

    public static Autoacknowledge copyFrom(IAutoAcknowledge source) {
        if (source == null) {
            return null;
        }

        Autoacknowledge autoAck = new Autoacknowledge();
        autoAck.setContent(source.getContent());
        autoAck.setState(source.getState());
        return autoAck;
    }

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'content'. The field 'content'
     * has the following description: internal content storage
     * 
     * @return the value of field 'Content'.
     */
    public String getContent(
    ) {
        return this._content;
    }

    /**
     * Returns the value of field 'state'.
     * 
     * @return the value of field 'State'.
     */
    public String getState(
    ) {
        return this._state;
    }

    /**
     * Sets the value of field 'content'. The field 'content' has
     * the following description: internal content storage
     * 
     * @param content the value of field 'content'.
     */
    public void setContent(
            final String content) {
        this._content = content;
    }

    /**
     * Sets the value of field 'state'.
     * 
     * @param state the value of field 'state'.
     */
    public void setState(
            final String state) {
        this._state = state;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Autoacknowledge.class.getSimpleName() + "[", "]")
                .add("_content='" + _content + "'")
                .add("_state='" + _state + "'")
                .toString();
    }
}
