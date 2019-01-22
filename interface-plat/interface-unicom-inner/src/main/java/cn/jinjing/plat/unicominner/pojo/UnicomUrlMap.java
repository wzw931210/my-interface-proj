package cn.jinjing.plat.unicominner.pojo;

import java.util.HashMap;
import java.util.Map;

public class UnicomUrlMap {

    public final static Map<String,UnicomUrl> unicomUrlMap = new HashMap<>();

    static{
        unicomUrlMap.put("realName",UnicomUrl.GETREALNAME);
        unicomUrlMap.put("onlineDur",UnicomUrl.GETONLINEDUR);
        unicomUrlMap.put("mdnLat",UnicomUrl.GETMDNLAT);
        unicomUrlMap.put("ownMdnCnt",UnicomUrl.GETOWNMDNCNT);
        unicomUrlMap.put("s3FlowAvg",UnicomUrl.GETS3FLOWAVG);
        unicomUrlMap.put("s3TalkDurAvg",UnicomUrl.GETS3TALKDURAVG);
        unicomUrlMap.put("s3BdTalkDurAvg",UnicomUrl.GETS3BDTALKDURAVG);
        unicomUrlMap.put("m1Flow",UnicomUrl.GETM1FLOW);
        unicomUrlMap.put("m1TalkDur",UnicomUrl.GETM1TALKDUR);
        unicomUrlMap.put("m1MessageCnt",UnicomUrl.GETM1MESSAGECNT);
        unicomUrlMap.put("s6FlowAvg",UnicomUrl.GETS6FLOWAVG);
        unicomUrlMap.put("s6TalkDurAvg",UnicomUrl.GETS6TALKDURAVG);
        unicomUrlMap.put("s6MessageCntAvg",UnicomUrl.GETS6MESSAGECNTAVG);
        unicomUrlMap.put("payCharge",UnicomUrl.GETPAYCHARGE);
        unicomUrlMap.put("s3PayChargeAvg",UnicomUrl.GETS3PAYCHARGEAVG);
        unicomUrlMap.put("s6PayChargeAvg",UnicomUrl.GETS6PAYCHARGEAVG);
        unicomUrlMap.put("terminalPricePref",UnicomUrl.GETTERMINALPRICEPREF);
        unicomUrlMap.put("groupFlag",UnicomUrl.GETGROUPFLAG);
        unicomUrlMap.put("oweBehavior",UnicomUrl.GETOWEBEHAVIOR);
        unicomUrlMap.put("oweBehaviorFreq",UnicomUrl.GETOWEBEHAVIORFREQ);
        unicomUrlMap.put("bdTalkDurScore",UnicomUrl.GETBDTALKDURSCORE);
        unicomUrlMap.put("phonePriceLevel",UnicomUrl.GETPHONEPRICELEVEL);
        unicomUrlMap.put("fiduciaryLoanScore",UnicomUrl.GETFIDUCIARYLOANSCORE);
        unicomUrlMap.put("careerType",UnicomUrl.GETCAREERTYPE);
        unicomUrlMap.put("careerType1",UnicomUrl.GETCAREERTYPE1);
        unicomUrlMap.put("s3MessageCntAvg",UnicomUrl.GETS3MESSAGECNTAVG);
        unicomUrlMap.put("stopPredict",UnicomUrl.GETSTOPPREDICT);
        unicomUrlMap.put("behaviorStabilityLevel",UnicomUrl.GETBEHAVIORSTABILITYLEVEL);
        unicomUrlMap.put("terminalBrandPref",UnicomUrl.GETTERMINALBRANDPREF);
        unicomUrlMap.put("s3EbankAppTopOne",UnicomUrl.GETS3EBANKAPPTOPONE);
        unicomUrlMap.put("s3EbankAppTopThree",UnicomUrl.GETS3EBANKAPPTOPTHREE);
        unicomUrlMap.put("s3InsuranceAppTopOne",UnicomUrl.GETS3INSURANCEAPPTOPONE);
        unicomUrlMap.put("s3InsuranceAppTopThree",UnicomUrl.GETS3INSURANCEAPPTOPTHREE);
        unicomUrlMap.put("s3CreditCardTopOne",UnicomUrl.GETS3CREDITCARDTOPONE);
        unicomUrlMap.put("s3CreditCardTopThree",UnicomUrl.GETS3CREDITCARDTOPTHREE);
        unicomUrlMap.put("s3MamoneyMaappTopOne",UnicomUrl.GETS3MAMONEYMAAPPTOPONE);
        unicomUrlMap.put("s3MamoneyMaappTopThree",UnicomUrl.GETS3MAMONEYMAAPPTOPTHREE);
        unicomUrlMap.put("s3ConsumerFinaAppTopOne",UnicomUrl.GETS3CONSUMERFINAAPPTOPONE);
        unicomUrlMap.put("s3ConsumerFinaAppTopThree",UnicomUrl.GETS3CONSUMERFINAAPPTOPTHREE);
        unicomUrlMap.put("ifMultiFinance",UnicomUrl.GETIFMULTIFINANCE); // 原为getFinanceAppAction
        unicomUrlMap.put("ifBadFinance",UnicomUrl.GETIFBADFINANCE); // 原为getAppAction
        unicomUrlMap.put("workStabilityLevel",UnicomUrl.GETWORKSTABILITYLEVEL);
        unicomUrlMap.put("lifeStabilityLevel",UnicomUrl.GETLIFESTABILITYLEVEL);
        unicomUrlMap.put("incomeLevel",UnicomUrl.GETINCOMELEVEL);
        unicomUrlMap.put("travelPre",UnicomUrl.GETTRAVELPRE);
        unicomUrlMap.put("ownCarP",UnicomUrl.GETOWNCARP);
        unicomUrlMap.put("homeIncomeLevel",UnicomUrl.GETHOMEINCOMELEVEL);
        unicomUrlMap.put("homeEconomicLevel",UnicomUrl.GETHOMEECONOMICLEVEL); // 原为getEconomicLevel

        unicomUrlMap.put("sunPrenatal",UnicomUrl.SUNPRENATAL);
        unicomUrlMap.put("sunMedicine",UnicomUrl.SUNMEDICINE);
        unicomUrlMap.put("sunCosmetology",UnicomUrl.SUNCOSMETOLOGY);
        unicomUrlMap.put("sunDeparture",UnicomUrl.SUNDEPARTURE);
        unicomUrlMap.put("sunInsurance",UnicomUrl.SUNINSURANCE);
        unicomUrlMap.put("sunBank",UnicomUrl.SUNBANK);
        unicomUrlMap.put("sunForex",UnicomUrl.SUNFOREX);
        unicomUrlMap.put("sunWealth",UnicomUrl.SUNWEALTH);
        unicomUrlMap.put("s3InsuranceAppCnt",UnicomUrl.S3INSURANCEAPPCNT);
        unicomUrlMap.put("s3CreditCardCnt",UnicomUrl.S3CREDITCARDCNT);
        unicomUrlMap.put("s3EbankAppCnt",UnicomUrl.S3EBANKAPPCNT);
        unicomUrlMap.put("s3MamoneyMaappCnt",UnicomUrl.S3MAMONEYMAAPPCNT);
        unicomUrlMap.put("s3ConsumerFinaAppCnt",UnicomUrl.S3CONSUMERFINAAPPCNT);
        unicomUrlMap.put("s3EbankAppDays",UnicomUrl.S3EBANKAPPDAYS);
        unicomUrlMap.put("s3InsuranceAppDays",UnicomUrl.S3INSURANCEAPPDAYS);
        unicomUrlMap.put("s3CreditCardAppDays",UnicomUrl.S3CREDITCARDAPPDAYS);
        unicomUrlMap.put("s3MamoneyMaappDays",UnicomUrl.S3MAMONEYMAAPPDAYS);
        unicomUrlMap.put("s3ConsumerFinaAppDays",UnicomUrl.S3CONSUMERFINAAPPDAYS);

        unicomUrlMap.put("s3EbankAppDaysTopThree",UnicomUrl.S3EBANKAPPDAYSTOPTHREE);
        unicomUrlMap.put("s3InsuranceAppDaysTopThree",UnicomUrl.S3INSURANCEAPPDAYSTOPTHREE);
        unicomUrlMap.put("s3CreditCardDaysTopThree",UnicomUrl.S3CREDITCARDDAYSTOPTHREE);
        unicomUrlMap.put("s3MamoneyMaappDaysTopThree",UnicomUrl.S3MAMONEYMAAPPDAYSTOPTHREE);
        unicomUrlMap.put("s3ConsumerFinaAppDaysTopThree",UnicomUrl.S3CONSUMERFINAAPPDAYSTOPTHREE);


    }

}
