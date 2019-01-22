package cn.jinjing.plat.service.digital;

import cn.jinjing.plat.api.entity.ReLabel;

public interface DigitalService {

    /**
     * 查询标签接口
     * @param label
     * @param mdn
     * @param month
     * @return
     */
    public ReLabel getLabelResult(String label, String mdn, String month,String userCode);
}
