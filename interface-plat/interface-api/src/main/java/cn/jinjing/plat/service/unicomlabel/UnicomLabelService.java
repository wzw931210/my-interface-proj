package cn.jinjing.plat.service.unicomlabel;

import cn.jinjing.plat.api.entity.ReLabel;

public interface UnicomLabelService {

    ReLabel getUnicomLabel(String month, String label, String mdn,String userCode);

    ReLabel get3Elements(String label,String mdn,String certType,String certCode,String userName ,String userCode);
}
