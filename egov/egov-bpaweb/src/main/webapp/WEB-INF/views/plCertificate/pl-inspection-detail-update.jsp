<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2017>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<div class="row">
	<div class="col-md-12">
		<form:form role="form" action="" method="post"
			modelAttribute="plInspection" id="plInspectionUpdateform"
			cssClass="form-horizontal form-groups-bordered"
			enctype="multipart/form-data">
			<form:hidden path="id" value="${plInspection.id}" />
			<input name="plInspection" value="${plInspection.id}" type="hidden" />
			<form:hidden id="plId" path="pl" value="${plInspection.pl.id}" />
			<input id="mode" name="mode" value="${mode}" type="hidden" />
			<input type="hidden" name="applicationNumber" id="applicationNumber"
				value="${applicationNumber}">
			<input type="hidden" name="inspectionDate" id="inspectionDate"
				value="${plInspection.inspection.inspectionDate}">

			<ul class="nav nav-tabs" id="settingstab">
				<li class="active"><a data-toggle="tab"
					href="#pl-inspn-details" data-tabidx=0><spring:message
							code='lbl.inspn.details' /></a></li>
				<c:if
					test="${not empty plInspection.inspection.getInspectionSupportDocs()}">
					<li><a data-toggle="tab" href="#site-images" data-tabidx=2><spring:message
								code='title.site.image' /></a></li>
				</c:if>
			</ul>
			<div class="tab-content">
				<div id="pl-inspn-details" class="tab-pane fade in active">
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-body custom-form ">
							<jsp:include page="view-pl-application-details.jsp"></jsp:include>
						</div>
					</div>
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-body custom-form ">
							<jsp:include page="pl-inspection-detail-form.jsp"></jsp:include>
						</div>
					</div>
				</div>
				<c:if test="${not empty plInspection.inspection.getInspectionSupportDocs()}">
					<div id="site-images" class="tab-pane fade">
						<div class="panel panel-primary" data-collapsed="0">
							<div class="panel-body custom-form ">
								<c:set var="inspectionSupportDocs"
									value="${plInspection.inspection.getInspectionSupportDocs()}"
									scope="request"></c:set>
								<jsp:include
									page="../application/upload-inspection-documents.jsp"></jsp:include>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			<div align="center">
				<form:button type="submit" id="buttonSubmit" class="btn btn-primary"
					value="createinspectiondetails">
					<spring:message code="lbl.btn.submit" />
				</form:button>
				<input type="button" name="button2" id="button2" value="Close"
					class="btn btn-default" onclick="window.close();" />
			</div>
		</form:form>
	</div>
</div>

