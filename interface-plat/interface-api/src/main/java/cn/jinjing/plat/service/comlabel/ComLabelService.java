package cn.jinjing.plat.service.comlabel;

import cn.jinjing.plat.api.entity.ReLabel;

public interface ComLabelService {

    /**
     * 查询用户手机是否实名制接口
     * @param label
     * @param mdn
     * @return
     */
    public ReLabel getComLabel(String month, String label, String mdn,String userCode);

    /**
     * 2、3要素
     * @param label
     * @param mdn
     * @return
     */
    public ReLabel getElement(String label, String mdn,String name,String userCode);

}
