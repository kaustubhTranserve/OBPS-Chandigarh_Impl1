package org.egov.edcr.feature;

import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.lucene.search.spans.FilterSpans.AcceptStatus;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.cdg.CDGAConstant;
import org.egov.edcr.service.cdg.CDGADeviationConstant;
import org.egov.edcr.service.cdg.CDGAdditionalService;
import org.springframework.stereotype.Service;

import B.A.A.C.P;
import freemarker.core.BugException;

@Service
public class CompoundWallService extends FeatureProcess {

	private static Logger LOG = Logger.getLogger(CompoundWallService.class);
	private static final String RULE_1 = "1";
	private static final String RULE_2 = "2";
	private static final String RULE_3 = "3";
	private static final BigDecimal ONE_POINT_ONETHREE = new BigDecimal("1.13");
	private static final BigDecimal ONE_POINT_EIGHT = new BigDecimal("1.8");
	private static final BigDecimal POINT_SIXNINE = new BigDecimal("0.69");
	private static final BigDecimal POINT_NINE = new BigDecimal("0.90");
	
	private static final BigDecimal POINT_SEVENFIVE = new BigDecimal("0.75");

	private static final String COMPOUNDWALL = "CompoundWall";
	private static final String FRONT_HEIGHT = "FrontHeight";
	private static final String REAR_HEIGHT = "RearHeight";
	private static final String RAILING_HEIGHT = "RailingHeight";

	private static final String WALL_HEIGHT_FRONT_DESCRIPTION = "Wall Front height";
	private static final String WALL_HEIGHT_REAR_DESCRIPTION = "Wall Rear height";
	private static final String WALL_RAILING_HIGHT_DESCRIPTION = "Wall Railing height";

	@Override
	public Map<String, Date> getAmendments() {
		return null;
	}

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan pl) {

		// write use color code LayerNameService.java or
		// plan.getSubFeatureColorCodesMaster

		HashMap<String, String> errors = new HashMap<>();
		
		OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
				
		if(mostRestrictiveFarHelper!=null && mostRestrictiveFarHelper.getSubtype()!=null &&!isOccupancyNotApplicable(mostRestrictiveFarHelper)) {
			
			if(!isOccupancyOptional(mostRestrictiveFarHelper)) {
				if (pl.getCompoundWall() == null) {
					errors.put("compoundWallNotDefined",
							getLocaleMessage(OBJECTNOTDEFINED, " Compund wall not defined in plan"));
				} else {

					if (pl.getCompoundWall().getWallHeights().size() == 0) {
						errors.put("compoundWallWallHeightsNotDefined",
								getLocaleMessage(OBJECTNOTDEFINED, " Compund wall - WallHeight not defined in plan"));
					}
				}
			}

			if (errors.size() > 0) {
				pl.addErrors(errors);
				return pl;
			}

			if (pl.getCompoundWall() != null) {

				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.setKey("Common_Compound Wall");
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, DESCRIPTION);
				scrutinyDetail.addColumnHeading(3, REQUIRED);
				scrutinyDetail.addColumnHeading(4, PROVIDED);
				scrutinyDetail.addColumnHeading(5, STATUS);

				processWallHeights(pl, scrutinyDetail);
				processRailingHeights(pl, scrutinyDetail);

			}
			
		}

		return pl;
	}

	private void processWallHeights(Plan pl, ScrutinyDetail scrutinyDetail) {

		Map<String, Integer> map = pl.getSubFeatureColorCodesMaster().get(COMPOUNDWALL);
		OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		BigDecimal frontMaxFrontHeight=BigDecimal.ZERO;
		BigDecimal frontMaxRearHeight=BigDecimal.ZERO;
		 try {
			 frontMaxFrontHeight = pl.getCompoundWall().getWallHeights().stream()
						.filter(hm -> hm.getColorCode() == map.get(FRONT_HEIGHT)).map(n -> n.getHeight())
						.reduce(BigDecimal::max).get();
				 frontMaxRearHeight = pl.getCompoundWall().getWallHeights().stream()
						.filter(hm -> hm.getColorCode() == map.get(REAR_HEIGHT)).map(n -> n.getHeight()).reduce(BigDecimal::max)
						.get();
		 }catch (Exception e) {
			LOG.error(e.getMessage());
		}

		Map<String, String> details = new HashMap<>();

//		details.put(RULE_NO, RULE_1);
		details.put(RULE_NO, CDGAdditionalService.getByLaws(mostRestrictiveOccupancyType, CDGAConstant.COMPOUND_WALL_SERVICE));
		details.put(DESCRIPTION, WALL_HEIGHT_FRONT_DESCRIPTION);
		if(pl.getDrawingPreference().getInFeets()) {
			frontMaxFrontHeight=CDGAdditionalService.inchToFeet(frontMaxFrontHeight);
			frontMaxRearHeight=CDGAdditionalService.inchToFeet(frontMaxRearHeight);
		}
			
		details.put(PROVIDED, CDGAdditionalService.viewLenght(pl, frontMaxFrontHeight));
		
		BigDecimal exceptedFrontMaxFrontHeight=BigDecimal.ZERO;
		BigDecimal exceptedFrontMaxRearHeight=BigDecimal.ZERO;
		
		OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding() != null
				? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
				: null;
		
		if(DxfFileConstants.A_P.equals(mostRestrictiveFarHelper.getSubtype().getCode())
				) {
			exceptedFrontMaxFrontHeight=ONE_POINT_ONETHREE;
			exceptedFrontMaxRearHeight=ONE_POINT_EIGHT;
			
			if(pl.getDrawingPreference().getInFeets()) {
				exceptedFrontMaxFrontHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxFrontHeight.toString());
				exceptedFrontMaxFrontHeight=exceptedFrontMaxFrontHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_13);
				exceptedFrontMaxRearHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxRearHeight.toString());
				exceptedFrontMaxRearHeight=exceptedFrontMaxRearHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_8);
			}
			
		}
		
		else if(DxfFileConstants.A_G.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.F_H.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.F_M.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.F_CFI.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.F_BH.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_D.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_P.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_F.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_N.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_H.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_CC.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_SS.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_CNA.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.P_R.equals(mostRestrictiveFarHelper.getSubtype().getCode())
		|| DxfFileConstants.IT.equals(mostRestrictiveFarHelper.getType().getCode())
		|| DxfFileConstants.ITH.equals(mostRestrictiveFarHelper.getType().getCode())
		|| DxfFileConstants.IP.equals(mostRestrictiveFarHelper.getType().getCode())
				) {
			exceptedFrontMaxFrontHeight=ONE_POINT_ONETHREE;
			exceptedFrontMaxRearHeight=ONE_POINT_ONETHREE;
			if(pl.getDrawingPreference().getInFeets()) {
				exceptedFrontMaxFrontHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxFrontHeight.toString());
				exceptedFrontMaxFrontHeight=exceptedFrontMaxFrontHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_13);
				exceptedFrontMaxRearHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxRearHeight.toString());
				exceptedFrontMaxRearHeight=exceptedFrontMaxRearHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_13);
			}
		}else {
			exceptedFrontMaxFrontHeight=ONE_POINT_EIGHT;
			exceptedFrontMaxRearHeight=ONE_POINT_EIGHT;
			if(pl.getDrawingPreference().getInFeets()) {
				exceptedFrontMaxFrontHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxFrontHeight.toString());
				exceptedFrontMaxFrontHeight=exceptedFrontMaxFrontHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_8);
				exceptedFrontMaxRearHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxRearHeight.toString());
				exceptedFrontMaxRearHeight=exceptedFrontMaxRearHeight.add(CDGADeviationConstant.COMPOUND_WALL_DEVIATION_FEET_1_0_8);
			}
		}
		
//		if(pl.getDrawingPreference().getInFeets()) {
//			exceptedFrontMaxFrontHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxFrontHeight.toString());
//			exceptedFrontMaxRearHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxRearHeight.toString());
//		}

		details.put(REQUIRED, "Maximum height " + CDGAdditionalService.viewLenght(pl, exceptedFrontMaxFrontHeight));
		if (frontMaxFrontHeight.compareTo(exceptedFrontMaxFrontHeight) <= 0) {
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}
		if(DxfFileConstants.A_P.equalsIgnoreCase(mostRestrictiveFarHelper.getSubtype().getCode()) && DxfFileConstants.MARLA.equals(pl.getPlanInfoProperties().get(DxfFileConstants.PLOT_TYPE)) && frontMaxFrontHeight.compareTo(BigDecimal.ZERO)==0)
			details.put(STATUS, Result.Accepted.getResultVal());
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

		Map<String, String> details2 = new HashMap<>();

//		details2.put(RULE_NO, RULE_2);
		details2.put(RULE_NO, CDGAdditionalService.getByLaws(mostRestrictiveOccupancyType, CDGAConstant.COMPOUND_WALL_SERVICE));
		details2.put(DESCRIPTION, WALL_HEIGHT_REAR_DESCRIPTION);
		details2.put(PROVIDED, CDGAdditionalService.viewLenght(pl, frontMaxRearHeight));

		details2.put(REQUIRED, "Maximum height " + CDGAdditionalService.viewLenght(pl, exceptedFrontMaxRearHeight));
		if (frontMaxRearHeight.compareTo(exceptedFrontMaxRearHeight) <= 0) {
			details2.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details2.put(STATUS, Result.Not_Accepted.getResultVal());
		}
		scrutinyDetail.getDetail().add(details2);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}

	private void processRailingHeights(Plan pl, ScrutinyDetail scrutinyDetail) {

		Map<String, Integer> map = pl.getSubFeatureColorCodesMaster().get(COMPOUNDWALL);
		OccupancyTypeHelper mostRestrictiveOccupancyType = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
		
		if(isOccupancyNotApplicable(mostRestrictiveOccupancyType))
			return;
		
		BigDecimal frontMaxRailingHeight=BigDecimal.ZERO;
		
		if(pl.getCompoundWall()!=null && pl.getCompoundWall().getRailingHeights()!=null && pl.getCompoundWall().getRailingHeights().size()!=0)
			frontMaxRailingHeight = pl.getCompoundWall().getRailingHeights().stream()
				.filter(hm -> hm.getColorCode() == map.get(RAILING_HEIGHT)).map(n -> n.getHeight())
				.reduce(BigDecimal::max).get();
		else {
			if(isRaillingOptional(mostRestrictiveOccupancyType))
				return;
		}
		if(pl.getDrawingPreference().getInFeets())
			frontMaxRailingHeight=CDGAdditionalService.inchToFeet(frontMaxRailingHeight);
		
		BigDecimal exceptedFrontMaxRailingHeight=BigDecimal.ZERO;
		
		if(DxfFileConstants.B.equals(mostRestrictiveOccupancyType.getType().getCode())
				) {
			exceptedFrontMaxRailingHeight=POINT_NINE;
		}else {
			exceptedFrontMaxRailingHeight=POINT_SIXNINE;
		}
		
		if(pl.getDrawingPreference().getInFeets()) {
			exceptedFrontMaxRailingHeight=CDGAdditionalService.meterToFoot(exceptedFrontMaxRailingHeight.toString());
		}

		Map<String, String> details = new HashMap<>();

//		details.put(RULE_NO, RULE_3);
		details.put(RULE_NO, CDGAdditionalService.getByLaws(mostRestrictiveOccupancyType, CDGAConstant.COMPOUND_WALL_SERVICE));
		details.put(DESCRIPTION, WALL_RAILING_HIGHT_DESCRIPTION);
		details.put(PROVIDED, CDGAdditionalService.viewLenght(pl, frontMaxRailingHeight));

		details.put(REQUIRED, "Maximum height " + CDGAdditionalService.viewLenght(pl, exceptedFrontMaxRailingHeight));
		if (frontMaxRailingHeight.compareTo(exceptedFrontMaxRailingHeight) <= 0) {
			details.put(STATUS, Result.Accepted.getResultVal());
		} else {
			details.put(STATUS, Result.Not_Accepted.getResultVal());
		}
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	}
	
	private boolean isRaillingOptional(OccupancyTypeHelper occupancyTypeHelper) {
		boolean flage = false;
		if (DxfFileConstants.A_P.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_H.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_H.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_M.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_CFI.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_BH.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_BBM.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_TCIM.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.G_GBZP.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.B.equals(occupancyTypeHelper.getType().getCode())
				|| DxfFileConstants.IT.equals(occupancyTypeHelper.getType().getCode())
				|| DxfFileConstants.ITH.equals(occupancyTypeHelper.getType().getCode())
				|| DxfFileConstants.T1.equals(occupancyTypeHelper.getSubtype().getCode())
				)
			flage = true;
		return flage;
	}
	
//
	private boolean isOccupancyNotApplicable(OccupancyTypeHelper occupancyTypeHelper) {
		boolean flage = false;

		if (DxfFileConstants.F_SCO.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_B.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_TS.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_TCIM.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_PP.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_CD.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.G_GBAC.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.R1.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.T1.equals(occupancyTypeHelper.getSubtype().getCode()))
			flage = true;

		return flage;
	}
	
	private boolean isOccupancyOptional(OccupancyTypeHelper occupancyTypeHelper) {
		boolean flage = false;

		if (DxfFileConstants.F_H.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_M.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_CFI.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.F_BH.equals(occupancyTypeHelper.getSubtype().getCode())
				|| DxfFileConstants.B.equals(occupancyTypeHelper.getType().getCode())
				|| DxfFileConstants.IT.equals(occupancyTypeHelper.getType().getCode())
				|| DxfFileConstants.ITH.equals(occupancyTypeHelper.getType().getCode())
				)
			flage = true;

		return flage;
	}

}
