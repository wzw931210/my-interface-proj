package cn.jinjing.plat.unicominner.pojo;

import cn.jinjing.plat.unicominner.util.FindTagConfig;

public enum UnicomUrl {

    //访问联通接口URL
    GETREALNAME("realName", FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETONLINEDUR("onlineDur",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETMDNLAT("mdnLat",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETOWNMDNCNT("ownMdnCnt",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS3FLOWAVG("s3FlowAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS3TALKDURAVG("s3TalkDurAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS3BDTALKDURAVG("s3BdTalkDurAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETM1FLOW("m1Flow",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETM1TALKDUR("m1TalkDur",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETM1MESSAGECNT("m1MessageCnt",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS6FLOWAVG("s6FlowAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS6TALKDURAVG("s6TalkDurAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS6MESSAGECNTAVG("s6MessageCntAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETPAYCHARGE("payCharge",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS3PAYCHARGEAVG("s3PayChargeAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS6PAYCHARGEAVG("s6PayChargeAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETTERMINALPRICEPREF("terminalPricePref",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETGROUPFLAG("groupFlag",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETOWEBEHAVIOR("oweBehavior",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETOWEBEHAVIORFREQ("oweBehaviorFreq",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETBDTALKDURSCORE("bdTalkDurScore",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETPHONEPRICELEVEL("phonePriceLevel",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETFIDUCIARYLOANSCORE("fiduciaryLoanScore",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),

    GETCAREERTYPE1("careerType1",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETS3MESSAGECNTAVG("s3MessageCntAvg",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETSTOPPREDICT("stopPredict",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETBEHAVIORSTABILITYLEVEL("behaviorStabilityLevel",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),
    GETTERMINALBRANDPREF("terminalBrandPref",FindTagConfig.HTTP_REQUEST_URL_RC,"result"),

    GETS3EBANKAPPTOPONE("s3EbankAppTopOne",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3EBANKAPPTOPTHREE("s3EbankAppTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3INSURANCEAPPTOPONE("s3InsuranceAppTopOne",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3INSURANCEAPPTOPTHREE("s3InsuranceAppTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3CREDITCARDTOPONE("s3CreditCardTopOne",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3CREDITCARDTOPTHREE("s3CreditCardTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3MAMONEYMAAPPTOPONE("s3MamoneyMaappTopOne",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3MAMONEYMAAPPTOPTHREE("s3MamoneyMaappTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3CONSUMERFINAAPPTOPONE("s3ConsumerFinaAppTopOne",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETS3CONSUMERFINAAPPTOPTHREE("s3ConsumerFinaAppTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETIFMULTIFINANCE("ifMultiFinance",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETIFBADFINANCE("ifBadFinance",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETWORKSTABILITYLEVEL("workStabilityLevel",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETLIFESTABILITYLEVEL("lifeStabilityLevel",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETINCOMELEVEL("incomeLevel",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETTRAVELPRE("travelPre",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETOWNCARP("ownCarP",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETHOMEINCOMELEVEL("homeIncomeLevel",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    GETHOMEECONOMICLEVEL("homeEconomicLevel",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),

    SUNPRENATAL("sunPrenatal",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNMEDICINE("sunMedicine",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNCOSMETOLOGY("sunCosmetology",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNDEPARTURE("sunDeparture",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNINSURANCE("sunInsurance",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNBANK("sunBank",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNFOREX("sunForex",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    SUNWEALTH("sunWealth",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3INSURANCEAPPCNT("s3InsuranceAppCnt",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CREDITCARDCNT("s3CreditCardCnt",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3EBANKAPPCNT("s3EbankAppCnt",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3MAMONEYMAAPPCNT("s3MamoneyMaappCnt",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CONSUMERFINAAPPCNT("s3ConsumerFinaAppCnt",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3EBANKAPPDAYS("s3EbankAppDays",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3INSURANCEAPPDAYS("s3InsuranceAppDays",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CREDITCARDAPPDAYS("s3CreditCardAppDays",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3MAMONEYMAAPPDAYS("s3MamoneyMaappDays",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CONSUMERFINAAPPDAYS("s3ConsumerFinaAppDays",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),


    GETCAREERTYPE("careerType",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3EBANKAPPDAYSTOPTHREE("s3EbankAppDaysTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3INSURANCEAPPDAYSTOPTHREE("s3InsuranceAppDaysTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CREDITCARDDAYSTOPTHREE("s3CreditCardDaysTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3MAMONEYMAAPPDAYSTOPTHREE("s3MamoneyMaappDaysTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result"),
    S3CONSUMERFINAAPPDAYSTOPTHREE("s3ConsumerFinaAppDaysTopThree",FindTagConfig.HTTP_REQUEST_URL_DC,"result");




    UnicomUrl(String label,String url,String valueTag){
        this.label = label;
        this.url = url;
        this.valueTag = valueTag;
    }
    String label;

    String url;

    String valueTag;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValueTag() {
        return valueTag;
    }

    public void setValueTag(String valueTag) {
        this.valueTag = valueTag;
    }
}
