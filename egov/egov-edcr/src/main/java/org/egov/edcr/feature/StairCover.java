/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import static org.egov.edcr.utility.DcrConstants.OBJECTDEFINED;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.cdg.CDGAConstant;
import org.egov.edcr.service.cdg.CDGAdditionalService;
import org.springframework.stereotype.Service;

@Service
public class StairCover extends FeatureProcess {

	private static final Logger LOG = Logger.getLogger(StairCover.class);
	private static final String RULE_44_C = "44-c";
	public static final String STAIRCOVER_DESCRIPTION = "Mumty";

	@Override
	public Plan validate(Plan pl) {

		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.setKey("Common_Mumty");
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, VERIFIED);
		scrutinyDetail.addColumnHeading(4, ACTION);
		scrutinyDetail.addColumnHeading(5, STATUS);

		Map<String, String> details = new HashMap<>();
		Map<String, String> errors = new HashMap<>();
		details.put(RULE_NO, CDGAdditionalService.getByLaws(pl, CDGAConstant.STAIRCASE));

		BigDecimal minHeight = BigDecimal.ZERO;
		OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
				
		if(pl.isRural()) {

			for (Block b : pl.getBlocks()) {
				minHeight = BigDecimal.ZERO;
				if (b.getStairCovers() != null && !b.getStairCovers().isEmpty() && mostRestrictiveFarHelper != null
						&& mostRestrictiveFarHelper.getSubtype() != null) {
					//minHeight = b.getStairCovers().stream().reduce(BigDecimal::min).get();
					minHeight = b.getStairCovers().stream().reduce(BigDecimal::max).get();

					if (minHeight.compareTo(new BigDecimal(2.44)) <= 0) {
						details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
						details.put(VERIFIED, "Verified whether stair cover height is <= 2.44 meters");
						details.put(ACTION, "Not included stair cover height(" + minHeight + ") to building height");
						details.put(STATUS, Result.Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					} else {
						details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
						details.put(VERIFIED, "Verified whether stair cover height is <= 2.44 meters");
						details.put(ACTION, "Included stair cover height(" + minHeight + ") to building height");
						details.put(STATUS, Result.Verify.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
				}

			}
		
		}
				
		if (!isOccupancyNotApplicable(mostRestrictiveFarHelper)) {
			for (Block b : pl.getBlocks()) {
				minHeight = BigDecimal.ZERO;
				if (b.getStairCovers() != null && !b.getStairCovers().isEmpty() && mostRestrictiveFarHelper != null
						&& mostRestrictiveFarHelper.getSubtype() != null) {
					//minHeight = b.getStairCovers().stream().reduce(BigDecimal::min).get();
					minHeight = b.getStairCovers().stream().reduce(BigDecimal::max).get();

					if (DxfFileConstants.MARLA.equals(pl.getPlanInfoProperties().get(DxfFileConstants.PLOT_TYPE))
							&& DxfFileConstants.A_P.equals(mostRestrictiveFarHelper.getSubtype().getCode())) {
						errors.put("MumtyNotAllowed",
								getLocaleMessage(OBJECTDEFINED, " Mumty is not allowed in block " + b.getName()));
					}
					

					if (minHeight.compareTo(new BigDecimal(2.75)) <= 0) {
						details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
						details.put(VERIFIED, "Verified whether stair cover height is <= 2.75 meters");
						details.put(ACTION, "Not included stair cover height(" + minHeight + ") to building height");
						details.put(STATUS, Result.Accepted.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					} else {
						details.put(DESCRIPTION, STAIRCOVER_DESCRIPTION);
						details.put(VERIFIED, "Verified whether stair cover height is <= 2.75 meters");
						details.put(ACTION, "Included stair cover height(" + minHeight + ") to building height");
						details.put(STATUS, Result.Verify.getResultVal());
						scrutinyDetail.getDetail().add(details);
						pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
					}
				}

			}
		}
		if(!errors.isEmpty()) {
			pl.addErrors(errors);
		}
		
		return pl;
	}

	private boolean isOccupancyNotApplicable(OccupancyTypeHelper occupancyTypeHelper) {
		boolean flage = false;

		if (DxfFileConstants.F_PP.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_SCO.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_B.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_CD.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.R1.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.T1.equals(occupancyTypeHelper.getSubtype().getCode()))
			flage = true;

		return flage;
	}

	@Override
	public Map<String, Date> getAmendments() {
		return new LinkedHashMap<>();
	}

}
