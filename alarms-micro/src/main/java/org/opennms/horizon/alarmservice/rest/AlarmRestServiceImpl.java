/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alarmservice.rest;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.EnumUtils;
//import org.apache.commons.lang3.StringUtils;
import org.opennms.horizon.alarmservice.db.impl.entity.Alarm;
//import org.opennms.horizon.db.model.TroubleTicketState;
//import org.opennms.horizon.db.model.mapper.AlarmMapper;
import org.opennms.horizon.alarmservice.drools.AlarmService;
import org.opennms.horizon.alarmservice.model.AlarmDTO;
import org.opennms.horizon.alarmservice.web.rest.support.MultivaluedMapImpl;
import org.opennms.horizon.alarmservice.web.rest.support.SecurityHelper;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.transaction.annotation.Transactional;

@Path("/alarms")
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class AlarmRestServiceImpl implements AlarmRestService {

//    private AlarmDao alarmDao;
//    private AlarmMapper alarmMapper;
//    private SessionUtils sessionUtils;
//    private AlarmRepository alarmRepository;
    private AlarmService alarmService;

    protected Class<Alarm> getDaoClass() {
        return Alarm.class;
    }

    protected WebApplicationException getException(final Status status, String msg, String... params) throws WebApplicationException {
        if (params != null) msg = MessageFormatter.arrayFormat(msg, params).getMessage();
        log.error(msg);
        return new WebApplicationException(Response.status(status).type(MediaType.TEXT_PLAIN).entity(msg).build());
    }

//    private boolean isTicketerPluginEnabled() {
//        return SystemProperties.getBooleanWithDefaultAsTrue("opennms.alarmTroubleTicketEnabled");
//    }
//
//    private Response runIfTicketerPluginIsEnabled(Callable<Response> callable) throws Exception {
//        if (!isTicketerPluginEnabled()) {
//            return Response.status(Status.NOT_IMPLEMENTED).entity("AlarmTroubleTicketer is not enabled. Cannot perform operation").build();
//        }
//        Objects.requireNonNull(callable);
//        final Response response = callable.call();
//        return response;
//    }


//========================================
// Interface
//========================================

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" })
    @ApiResponse(
            description = "Retrieve the list of alarms"
    )
    public Response getAlarms(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo) {
        // replace the next line with @RolesAllowed("")
        //SecurityHelper.assertUserReadCredentials(securityContext);

//        return this.sessionUtils.withReadOnlyTransaction(() -> {
            //CriteriaBuilder builder = getCriteriaBuilder(uriInfo);
            //builder.distinct();

            List<AlarmDTO> matchingAlarms = alarmService.getAllAlarms("TODO:MMF blah");

            //TODO:MMF why was this being done?
//            List<AlarmDTO> dtoAlarmList =
//                    matchingAlarms
//                            .stream()
//                            .map(this.alarmMapper::alarmToAlarmDTO)
//                            .collect(Collectors.toList());

            //TODO:MMF do we need to do this????
//            AlarmCollectionDTO alarmsCollection = new AlarmCollectionDTO(dtoAlarmList);
//            alarmsCollection.setTotalCount(dtoAlarmList.size());

            return Response.status(Status.OK).entity(matchingAlarms).build();
//        });

    }

//    @POST
//    @Path("{id}/ack")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String ackAlarm(@PathParam("id") int id, AlarmAckDTO alarmAck) {
//        return sessionUtils.withTransaction(() -> {
//
//            updateAlarmTicket(id, alarmAck);
//
//            return "acknowledged";
//        });
//    }

//    @DELETE
//    @Path("{id}/ack")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String unackAlarm(@PathParam("id") int id) {
//        return sessionUtils.withTransaction(() -> {
//            OnmsAcknowledgment acknowledgment = new OnmsAcknowledgment(new Date(), "DELETE_USER__TODO_CLEAN_THIS_UP");
//            acknowledgment.setRefId(id);
//            acknowledgment.setAckAction(AckAction.UNACKNOWLEDGE);
//            acknowledgment.setAckType(AckType.ALARM);
//            acknowledgmentDao.processAck(acknowledgment);
//
//            return "unacknowledged";
//        });
//    }

    //TODO:MMF keep this one, don't use acknowledgment, jsut set alarm serverity to cleared.
    // also clear related alarms
//    @POST
//    @Path("{id}/clear")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String clearAlarm(@PathParam("id") int id, AlarmAckDTO alarmAck) {
//        return sessionUtils.withTransaction(() -> {
//            OnmsAcknowledgment acknowledgment = new OnmsAcknowledgment(new Date(), alarmAck.getUser());
//            acknowledgment.setRefId(id);
//            acknowledgment.setAckAction(AckAction.CLEAR);
//            acknowledgment.setAckType(AckType.ALARM);
//            acknowledgmentDao.processAck(acknowledgment);
//
//            updateAlarmTicket(id, alarmAck);
//
//            return "acknowledged";
//        });
//    }

    @PUT
    @Path("{id}/memo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({ "admin" })
    @ApiResponse(
            description = "Update the memo for an Alarm"
    )
    @Transactional
    public Response updateMemo(@Context final SecurityContext securityContext, @PathParam("id") final Integer alarmId, final MultivaluedMapImpl params) {
        // replace the next two lines with @RolesAllowed("")
        final String user = params.containsKey("user") ? params.getFirst("user") : securityContext.getUserPrincipal().getName();
        SecurityHelper.assertUserEditCredentials(securityContext, user);

//        return this.sessionUtils.withTransaction(() -> {
            final String body = params.getFirst("body");
            if (body == null) {
                throw getException(Status.BAD_REQUEST, "Body cannot be null.");
            }
            //alarmRepository.updateStickyMemo(alarmId, body, user); // TODO doing anything??
            return Response.noContent().build();
//        });
    }

    @PUT
    @Path("{id}/journal")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({ "admin" })
    @ApiResponse(
            description = "Update the journal for an Alarm"
    )
    @Transactional
    public Response updateJournal(@Context final SecurityContext securityContext, @PathParam("id") final Integer alarmId, final MultivaluedMapImpl params) {
//        return this.sessionUtils.withTransaction(() -> {
            final String user = params.containsKey("user") ? params.getFirst("user") : securityContext.getUserPrincipal().getName();
            // SecurityHelper.assertUserEditCredentials(securityContext, user);
            final String body = params.getFirst("body");
            if (body == null) throw getException(Status.BAD_REQUEST, "Body cannot be null.");
            //alarmRepository.updateReductionKeyMemo(alarmId, body, user); // TODO doing anything??
            return Response.noContent().build();
//        });
    }

    @DELETE
    @Path("{id}/memo")
    @RolesAllowed({ "admin" })
    @ApiResponse(
            description = "Remove the memo for an Alarm"
    )
    //TODO:MMF ask jesse about this
//    Yes, still need this
    public Response removeMemo(@Context final SecurityContext securityContext, @PathParam("id") final Integer alarmId) {
        //SecurityHelper.assertUserEditCredentials(securityContext, securityContext.getUserPrincipal().getName());
        try {
//            return runIfTicketerPluginIsEnabled(() -> {
//                return this.sessionUtils.withTransaction(() -> {
//                   // alarmRepository.removeStickyMemo(alarmId); // TODO doing anything??
//                    return Response.noContent().build();
//                });
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
