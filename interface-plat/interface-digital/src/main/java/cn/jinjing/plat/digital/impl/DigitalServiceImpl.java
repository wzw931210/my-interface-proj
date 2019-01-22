package cn.jinjing.plat.digital.impl;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.digital.util.LabelUtil;
import cn.jinjing.plat.service.digital.DigitalService;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.StatusCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class DigitalServiceImpl implements DigitalService {

    public static Log log = LogFactory.getLog(DigitalServiceImpl.class);
    private static String LOCAL_LABEL_TABLE = ConfigUtil.getProperties("local_label_table");
    private static String DX_LABEL_PRODUCT = ConfigUtil.getProperties("dx_label_product");
    private static String DX_LABEL_MODULE = ConfigUtil.getProperties("dx_label_module");

    /**
     * 查询标签接口
     */
    @Override
    public ReLabel getLabelResult(String label, String mdn, String month,String userCode) {
        ReLabel ro = new ReLabel();
        String localTableName = LOCAL_LABEL_TABLE;
        String product = DX_LABEL_PRODUCT;
        String module = DX_LABEL_MODULE;
        try {
            ro = LabelUtil.getLabel(label, mdn, month, localTableName, product, module,userCode);
        } catch (Exception e) {
            ro.setFlag(false);
            ro.setMessage(StatusCode.SYSERR2.getValue());
            ro.setCode(StatusCode.SYSERR2.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return ro;
    }

}