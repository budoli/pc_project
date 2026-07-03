package kd.mgproj.domain.policy.dto;

public record YouthPolicyItem(
        String plcyNo,
        String plcyNm,
        String plcyKywdNm,
        String plcyExplnCn,

        String lclsfNm,
        String mclsfNm,

        String plcySprtCn,

        String sprvsnInstCdNm,
        String operInstCdNm,

        String bizPrdBgngYmd,
        String bizPrdEndYmd,
        String bizPrdEtcCn,

        String aplyYmd,
        String plcyAplyMthdCn,
        String srngMthdCn,
        String aplyUrlAddr,
        String sbmsnDcmntCn,
        String etcMttrCn,
        String refUrlAddr1,
        String refUrlAddr2,

        String sprtTrgtMinAge,
        String sprtTrgtMaxAge,
        String zipCd,

        String earnEtcCn,
        String addAplyQlfcCndCn,
        String ptcpPrpTrgtCn,

        String jobCd,
        String schoolCd,

        String frstRegDt,
        String lastMdfcnDt
) {
}